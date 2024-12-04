package com.vedasole.ekartecommercebackend.service.service_impl;

import com.vedasole.ekartecommercebackend.entity.Customer;
import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import com.vedasole.ekartecommercebackend.entity.ShoppingCartItem;
import com.vedasole.ekartecommercebackend.exception.APIException;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartItemDto;
import com.vedasole.ekartecommercebackend.repository.CustomerRepo;
import com.vedasole.ekartecommercebackend.repository.ShoppingCartItemRepo;
import com.vedasole.ekartecommercebackend.repository.ShoppingCartRepo;
import com.vedasole.ekartecommercebackend.service.service_interface.ShoppingCartItemService;
import com.vedasole.ekartecommercebackend.service.service_interface.ShoppingCartService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.CUSTOMER;
import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.SHOPPING_CART;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepo shoppingCartRepo;
    private final CustomerRepo customerRepo;
    private final ShoppingCartItemRepo shoppingCartItemRepo;
    private final ShoppingCartItemService shoppingCartItemService;

    /**
     * This method creates a new shopping cart for the given user.
     *
     * @param shoppingCartDto the DTO object containing the shopping cart details
     * @return the created shopping cart object
     */
    @Override
    public ShoppingCart createCart(ShoppingCartDto shoppingCartDto) {
        ShoppingCart shoppingCart = dtoToShoppingCart(shoppingCartDto);
        shoppingCart.setShoppingCartItems(null);
        shoppingCart.setCustomer(shoppingCart.getCustomer());
        try {
            return shoppingCartRepo.save(shoppingCart);
        } catch (Exception ex) {
            throw new APIException(ex.getMessage());
        }
    }

    /**
     * This method creates a new shopping cart for the given user with the given items.
     *
     * @param shoppingCartDto the DTO object containing the shopping cart details
     * @return the created shopping cart object
     */
    @Override
    public ShoppingCartDto createCartWithItems(ShoppingCartDto shoppingCartDto) {
        List<ShoppingCartItemDto> shoppingCartItemDtos = shoppingCartDto.getShoppingCartItems();
        shoppingCartDto.setShoppingCartItems(null);
        ShoppingCart savedShoppingCart;
        Optional<ShoppingCart> shoppingCart = shoppingCartRepo.findByCustomer_CustomerId(shoppingCartDto.getCustomerId());
        savedShoppingCart = shoppingCart.orElseGet(() -> createCart(shoppingCartDto));
        List<ShoppingCartItem> savedShoppingCartItems = shoppingCartItemRepo
                .saveAll(shoppingCartItemDtos.stream()
                .map(shoppingCartItemDto -> {
                    shoppingCartItemDto.setCartId(savedShoppingCart.getCartId());
                    return shoppingCartItemService.convertToShoppingCartItem(shoppingCartItemDto);
                }).toList()
        );
        savedShoppingCart.setShoppingCartItems(savedShoppingCartItems);
        updateShoppingCartTotalAndDiscount(savedShoppingCart);
        try {
            ShoppingCart updatedShoppingCart = shoppingCartRepo.save(savedShoppingCart);
            return shoppingCartToDto(updatedShoppingCart);
        } catch (Exception e) {
            throw new APIException(e.getMessage());
        }
    }

    /**
     * This method adds or updates an item in the shopping cart.
     * If the item already exists in the cart, it updates the quantity.
     * If the item does not exist, it adds the item to the cart.
     *
     * @param shoppingCartItemDto the DTO object containing the item details
     * @return the updated shopping cart DTO object
     * @throws ResourceNotFoundException if the shopping cart with the given ID is not found
     */
    @Override
    @CacheEvict(value = "shoppingCart", key = "#shoppingCartItemDto.cartId")
    public ShoppingCartDto addOrUpdateItemInCart(ShoppingCartItemDto shoppingCartItemDto) {
        ShoppingCart shoppingCart = shoppingCartRepo.findById(shoppingCartItemDto.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException(SHOPPING_CART.getValue(), "id", shoppingCartItemDto.getCartId()));
        ShoppingCartItem shoppingCartItem = shoppingCart.getShoppingCartItems().stream()
                .filter(item -> item.getProduct().getProductId() == shoppingCartItemDto.getProduct().getProductId())
                .findFirst()
                .orElseGet(() -> {
                    ShoppingCartItem shoppingCartItem1 = shoppingCartItemService.convertToShoppingCartItem(shoppingCartItemDto);
                    shoppingCart.getShoppingCartItems().add(shoppingCartItem1);
                    return shoppingCartItem1;
                });
        shoppingCartItem.setQuantity(shoppingCartItemDto.getQuantity());
        shoppingCartItemRepo.save(shoppingCartItem);
        updateShoppingCartTotalAndDiscount(shoppingCart);
        shoppingCartRepo.save(shoppingCart);
        return shoppingCartToDto(shoppingCart);
    }

    /**
     * This method deletes the shopping cart for the given user.
     *
     * @param customerId the ID of the user
     * @throws ResourceNotFoundException if the user with the given ID is not found
     */
    @Override
    public void deleteCart(long customerId) {
        try{
            shoppingCartRepo.deleteByCustomer_CustomerId(customerId);
        } catch (Exception e) {
            log.error("Error deleting cart with user id: {}", customerId);
            throw e;
        }
    }

    /**
     * This method retrieves the shopping cart for the given user.
     *
     * @param customerId the ID of the user
     * @return the shopping cart DTO object
     * @throws ResourceNotFoundException if the user with the given ID is not found
     */
    @Override
    @Transactional(readOnly = true)
    public ShoppingCartDto getCart(long customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException(CUSTOMER.getValue(), "customerId", customerId));
        ShoppingCart shoppingCart = Optional.ofNullable(customer.getShoppingCart())
                .orElseThrow(() -> new ResourceNotFoundException(SHOPPING_CART.getValue(), "customerId", customerId));
        return shoppingCartToDto(shoppingCart);
    }

    private void updateShoppingCartTotalAndDiscount(ShoppingCart shoppingCart) {
        double total = 0;
        double discount = 0;
        for (ShoppingCartItem item : shoppingCart.getShoppingCartItems()) {
            total += item.getProduct().getPrice() * item.getQuantity();
            discount += item.getProduct().getDiscount() * item.getProduct().getPrice() / 100 * item.getQuantity();
        }
        shoppingCart.setTotal(total);
        shoppingCart.setDiscount(discount);
    }

    /**
     * This method maps a ShoppingCartDto object to a ShoppingCart object.
     *
     * @param shoppingCartDto the ShoppingCartDto object to map
     * @return the mapped ShoppingCart object
     */
    @Override
    public ShoppingCart convertToShoppingCart(ShoppingCartDto shoppingCartDto) {
        return dtoToShoppingCart(shoppingCartDto);
    }

    /**
     * This method maps a Product object to a ProductDto.
     * @param shoppingCartDto the ProductDto to map
     * @return the mapped ProductDto object
     */
    private ShoppingCart dtoToShoppingCart(ShoppingCartDto shoppingCartDto){
       ShoppingCart shoppingCart = new ShoppingCart();
       shoppingCart.setCartId(shoppingCartDto.getCartId());
       Customer customer = customerRepo.findById(shoppingCartDto.getCustomerId())
               .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", shoppingCartDto.getCustomerId()));
       shoppingCart.setCustomer(customer);
        if ( shoppingCartDto.getShoppingCartItems() != null &&  !shoppingCartDto.getShoppingCartItems().isEmpty()) {
            shoppingCart.setShoppingCartItems(
                    shoppingCartDto.getShoppingCartItems().stream()
                            .map(shoppingCartItemService::convertToShoppingCartItem)
                            .toList()
            );
        }
        return shoppingCart;
    }

    /**
     * This method maps a ShoppingCart object to a ShoppingCartDto.
     *
     * @param shoppingCart the ShoppingCart object to map
     * @return the mapped ShoppingCartDto object
     */
    private ShoppingCartDto shoppingCartToDto(ShoppingCart shoppingCart){
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setCartId(shoppingCart.getCartId());
        shoppingCartDto.setCustomerId(shoppingCart.getCustomer().getCustomerId());
        shoppingCartDto.setShoppingCartItems(
                shoppingCart.getShoppingCartItems().stream()
                        .map(shoppingCartItemService::convertToShoppingCartItemDto)
                        .toList()
        );
        shoppingCartDto.setTotal(shoppingCart.getTotal());
        shoppingCartDto.setDiscount(shoppingCart.getDiscount());
        return shoppingCartDto;
    }

}