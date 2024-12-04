package com.vedasole.ekartecommercebackend.service.service_impl;

import com.vedasole.ekartecommercebackend.entity.Product;
import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import com.vedasole.ekartecommercebackend.entity.ShoppingCartItem;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.ProductDto;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartItemDto;
import com.vedasole.ekartecommercebackend.repository.ShoppingCartItemRepo;
import com.vedasole.ekartecommercebackend.repository.ShoppingCartRepo;
import com.vedasole.ekartecommercebackend.service.service_interface.ProductService;
import com.vedasole.ekartecommercebackend.service.service_interface.ShoppingCartItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.SHOPPING_CART;
import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.SHOPPING_CART_ITEM;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ShoppingCartItemServiceImpl implements ShoppingCartItemService {

    private final ShoppingCartItemRepo shoppingCartItemRepo;
    private final ProductService productService;
    private final ShoppingCartRepo shoppingCartRepo;

    
    /**
     * Creates a new shopping cart item and associates it with the specified shopping cart.
     *
     * @param shoppingCartItemDto The shopping cart item data transfer object (DTO) containing the necessary information.
     * @return The DTO of the newly created shopping cart item.
     * @throws ResourceNotFoundException If the specified shopping cart does not exist.
     */
    @Override
    @CacheEvict(value = "shoppingCartItems", key = "#shoppingCartItemDto.cartId")
    public ShoppingCartItemDto createShoppingCartItem(ShoppingCartItemDto shoppingCartItemDto) {
        log.debug("Creating shopping cart item: {}", shoppingCartItemDto);
        ShoppingCartItem savedCartItem = shoppingCartItemRepo.save(convertToShoppingCartItem(shoppingCartItemDto));
        savedCartItem.getShoppingCart().getShoppingCartItems().add(savedCartItem);
        savedCartItem.getShoppingCart().calculateTotalAndDiscount();
        log.debug("Shopping cart item created successfully: {}", savedCartItem);
        ShoppingCart savedShoppingCart = shoppingCartRepo.save(savedCartItem.getShoppingCart());
        log.debug("Shopping cart updated successfully: {}", savedShoppingCart);
        savedCartItem.setShoppingCart(savedShoppingCart);
        log.debug("Shopping cart item updated successfully: {}", savedCartItem);
        return convertToShoppingCartItemDto(savedCartItem);
    }

    /**
     * Creates a list of new shopping cart items and associates them with the specified shopping cart.
     *
     * @param shoppingCartItemDtos The list of shopping cart item data transfer objects (DTOs) containing the necessary information.
     * @return The list of DTOs of the newly created shopping cart items.
     */
    @Override
    @CacheEvict(value = "shoppingCartItems", key = "#shoppingCartItemDtos.get(0).cartId")
    public List<ShoppingCartItemDto> createShoppingCartWithAllItems(List<ShoppingCartItemDto> shoppingCartItemDtos) {
        List<ShoppingCartItem> savedShoppingCartItems = shoppingCartItemRepo.saveAll(
                shoppingCartItemDtos.stream()
                        .map(this::shoppingCartItemDtoToEntity)
                        .toList()
        );
        savedShoppingCartItems.get(0).getShoppingCart().calculateTotalAndDiscount();
        ShoppingCart savedShoppingCart = shoppingCartRepo.save(savedShoppingCartItems.get(0).getShoppingCart());
        return savedShoppingCartItems.stream()
                .map(shoppingCartItem -> {
                    shoppingCartItem.setShoppingCart(savedShoppingCart);
                    return shoppingCartItemToDto(shoppingCartItem);
                })
                .toList();
    }

    /**
     * Updates the specified shopping cart item.
     *
     * @param shoppingCartItemDto The shopping cart item data transfer object (DTO) containing the necessary information.
     * @return The DTO of the updated shopping cart item.
     * @throws ResourceNotFoundException If the specified shopping cart item does not exist.
     */
    @Override
    @CacheEvict(value = "shoppingCartItem", key = "#shoppingCartItemDto.cartItemId")
    public ShoppingCartItemDto updateShoppingCartItem(ShoppingCartItemDto shoppingCartItemDto) {
        ShoppingCart shoppingCart = shoppingCartRepo.findById(shoppingCartItemDto.getCartId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        SHOPPING_CART.getValue(),
                        "cartId",
                        shoppingCartItemDto.getCartId()
                ));
        return shoppingCartItemRepo.findById(shoppingCartItemDto.getCartItemId())
                .map(shoppingCartItem -> {
                    ProductDto product = productService.getProductById(shoppingCartItemDto.getProduct().getProductId());
                    shoppingCartItem.setProduct(productService.productDtoToEntity(product));
                    shoppingCartItem.setShoppingCart(shoppingCart);
                    shoppingCartItem.setQuantity(shoppingCartItemDto.getQuantity());
                    ShoppingCartItem savedShoppingCartItem = shoppingCartItemRepo.save(shoppingCartItem);
                    shoppingCart.getShoppingCartItems().stream()
                                    .filter(item -> item.getCartItemId() == savedShoppingCartItem.getCartItemId())
                                    .findFirst()
                                            .ifPresentOrElse(
                                                    item -> {
                                                        item.setCartItemId(savedShoppingCartItem.getCartItemId());
                                                        item.setProduct(savedShoppingCartItem.getProduct());
                                                        item.setQuantity(savedShoppingCartItem.getQuantity());
                                                        item.setCreatedAt(savedShoppingCartItem.getCreatedAt());
                                                        item.setUpdatedAt(savedShoppingCartItem.getUpdatedAt());
                                                        item.setShoppingCart(shoppingCart);
                                                    },
                                                    () -> {
                                                        throw new ResourceNotFoundException(
                                                                SHOPPING_CART_ITEM.getValue(),
                                                                "cartItemId",
                                                                savedShoppingCartItem.getCartItemId()
                                                        );
                                                    }
                                            );
                    shoppingCart.calculateTotalAndDiscount();
                    ShoppingCart savedShoppingCart = shoppingCartRepo.save(shoppingCart);
                    savedShoppingCartItem.setShoppingCart(savedShoppingCart);
                    return shoppingCartItemToDto(savedShoppingCartItem);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        SHOPPING_CART_ITEM.getValue(),
                        "cartItemId",
                        shoppingCartItemDto.getCartItemId()
                ));
    }

    /**
     * Deletes the specified shopping cart item.
     *
     * @param cartItemId The ID of the shopping cart item to delete.
     * @throws ResourceNotFoundException If the specified shopping cart item does not exist.
     */
    @Override
    @CacheEvict(value = "shoppingCartItem", key = "#cartItemId")
    public void deleteShoppingCartItem(long cartItemId) {
        this.shoppingCartItemRepo.findById(cartItemId)
                .ifPresentOrElse(orderItem -> {
                    orderItem.getShoppingCart().getShoppingCartItems().remove(orderItem);
                    this.shoppingCartItemRepo.deleteById(cartItemId);
                    orderItem.getShoppingCart().calculateTotalAndDiscount();
                    this.shoppingCartRepo.save(orderItem.getShoppingCart());
                    log.info("Shopping Cart Item {} deleted successfully", cartItemId);
                }, () -> {
                    throw new ResourceNotFoundException(SHOPPING_CART_ITEM.getValue(), "cartItemId", cartItemId);
                });
    }

    /**
     * Deletes all shopping cart items for a specific shopping cart.
     *
     * @param cartId The ID of the shopping cart for which to delete all shopping cart items.
     */
    @Override
    @CacheEvict(value = "shoppingCartItems", key = "#cartId")
    public void deleteAllShoppingCartItems(long cartId) {
        shoppingCartItemRepo.deleteAllByShoppingCartCartId(cartId);
        shoppingCartRepo.findById(cartId)
                .ifPresentOrElse(
                        shoppingCart -> {
                            shoppingCart.setShoppingCartItems(null);
                            shoppingCart.calculateTotalAndDiscount();
                            shoppingCartRepo.save(shoppingCart);
                        },
                        () -> {
                            throw new ResourceNotFoundException(
                                    SHOPPING_CART.getValue(),
                                    "cartId",
                                    cartId
                            );
                        }
                );
        log.info("All shopping cart items for cart {} deleted successfully", cartId);
    }

    /**
     * Retrieves a shopping cart item from the database.
     *
     * @param cartItemId The ID of the shopping cart item to retrieve.
     * @return The DTO of the retrieved shopping cart item.
     * @throws ResourceNotFoundException If the specified shopping cart item does not exist.
     */
    @Override
    @Cacheable(value = "shoppingCartItem", key = "#cartItemId")
    @Transactional(readOnly = true)
    public ShoppingCartItemDto getShoppingCartItem(long cartItemId) {
        return shoppingCartItemRepo.findById(cartItemId)
                .map(this::shoppingCartItemToDto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        SHOPPING_CART_ITEM.getValue(),
                        "cartItemId",
                        cartItemId
                ));
    }

    /**
     * Retrieves all shopping cart items for a specific shopping cart from the database.
     *
     * @param cartId The ID of the shopping cart for which to retrieve all shopping cart items.
     * @return The list of DTOs of the retrieved shopping cart items.
     */
    @Override
    @Cacheable(value = "shoppingCartItems", key = "#cartId")
    @Transactional(readOnly = true)
    public List<ShoppingCartItemDto> getAllShoppingCartItems(long cartId) {
        return shoppingCartItemRepo.findAllByShoppingCartCartId(cartId)
                .stream()
                .map(this::shoppingCartItemToDto)
                .toList();
    }

    /**
     * Converts a ShoppingCartItemDto object to a ShoppingCartItem entity.
     *
     * @param shoppingCartItemDto The DTO object to be converted.
     * @return The converted ShoppingCartItem entity.
     */
    @Override
    public ShoppingCartItem convertToShoppingCartItem(ShoppingCartItemDto shoppingCartItemDto) {
        return shoppingCartItemDtoToEntity(shoppingCartItemDto);
    }

    /**
     * Converts a ShoppingCartItem entity to a ShoppingCartItemDto object.
     *
     * @param shoppingCartItem The entity object to be converted.
     * @return The converted ShoppingCartItemDto object.
     */
    @Override
    public ShoppingCartItemDto convertToShoppingCartItemDto(ShoppingCartItem shoppingCartItem) {
        return shoppingCartItemToDto(shoppingCartItem);
    }

    private ShoppingCartItemDto shoppingCartItemToDto(ShoppingCartItem shoppingCartItem) {
        ShoppingCartItemDto shoppingCartItemDto = new ShoppingCartItemDto();
        shoppingCartItemDto.setCartItemId(shoppingCartItem.getCartItemId());
        shoppingCartItemDto.setProduct(productService.productEntityToDto(shoppingCartItem.getProduct()));
        shoppingCartItemDto.setCartId(shoppingCartItem.getShoppingCart().getCartId());
        shoppingCartItemDto.setQuantity(shoppingCartItem.getQuantity());
        return shoppingCartItemDto;
    }

    private ShoppingCartItem shoppingCartItemDtoToEntity(ShoppingCartItemDto shoppingCartItemDto) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setCartItemId(shoppingCartItemDto.getCartItemId());
        shoppingCartRepo.findById(shoppingCartItemDto.getCartId())
                        .ifPresentOrElse(
                                shoppingCartItem::setShoppingCart,
                                () -> {
                                    throw new ResourceNotFoundException(
                                            SHOPPING_CART.getValue(),
                                            "cartId",
                                            shoppingCartItemDto.getCartId()
                                    );
                                }
                        );
        shoppingCartItem.setQuantity(shoppingCartItemDto.getQuantity());
        Product product = productService.productDtoToEntity(productService.getProductById(shoppingCartItemDto.getProduct().getProductId()));
        shoppingCartItem.setProduct(product);
        return shoppingCartItem;
    }
}