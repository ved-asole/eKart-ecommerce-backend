package com.vedasole.ekartecommercebackend.service.serviceImpl;

import com.vedasole.ekartecommercebackend.entity.Product;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.ProductDto;
import com.vedasole.ekartecommercebackend.repository.CategoryRepo;
import com.vedasole.ekartecommercebackend.repository.ProductRepo;
import com.vedasole.ekartecommercebackend.service.serviceInterface.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * This class implements the ProductService interface. It uses the ProductRepo and ModelMapper objects to perform operations on the database.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ModelMapper modelMapper;
    private final CategoryRepo categoryRepo;
    private static final String CATEGORY = "Category";
    private static final String PRODUCT = "Product";

    public ProductServiceImpl(ProductRepo productRepo, ModelMapper modelMapper, CategoryRepo categoryRepo) {
        this.productRepo = productRepo;
        this.modelMapper = modelMapper;
        this.categoryRepo = categoryRepo;
    }

    /**
     * This method creates a new Product entity in the database and returns a ProductDto with the new entity's information.
     * @param productDto the ProductDto containing the information for the new Product
     * @return a ProductDto with the information for the new Product
     */
    @Override
    public ProductDto createProduct(ProductDto productDto) {

        Product product = dtoToProduct(productDto);

        product.setCategory(this.categoryRepo.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                CATEGORY, "id" , productDto.getCategoryId())));
        // Added temp sku due to not null and not blank annotations for sku in Product
        product.setSku("temp-sku");

        Product addedProduct = this.productRepo.save(product);

        addedProduct.setSku(product.getCategory().getName().substring(0, 3).concat("-").concat(Long.toString(product.getProductId())));

        addedProduct = this.productRepo.save(addedProduct);

        return productToDto(addedProduct);
    }

    /**
     * This method updates an existing Product entity in the database and returns a ProductDto with the updated entity's information.
     * @param productDto the ProductDto containing the updated information for the Product
     * @param productId the ID of the Product to update
     * @return a ProductDto with the updated information for the Product
     */
    @Override
    public ProductDto updateProduct(ProductDto productDto, Long productId) {
        Product product = dtoToProduct(productDto);
        Product productInDB = this.productRepo.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException(
                        PRODUCT, "id" , productId));
        productInDB.setProductId(product.getProductId());
        productInDB.setName(product.getName());
        productInDB.setImage(product.getImage());
        productInDB.setDesc(product.getDesc());
        productInDB.setPrice(product.getPrice());
        productInDB.setQtyInStock(product.getQtyInStock());
        productInDB.setCategory(product.getCategory());

        this.productRepo.save(productInDB);

        return productToDto(productInDB);
    }

    /**
     * This method returns a list of all ProductDtos for all Products in the database.
     * @return a list of all ProductDtos
     */
    @Override
    public List<ProductDto> getAllProducts() {
        return this.productRepo.findAll().stream()
                .map(this::productToDto)
                .toList();
    }

    /**
     * This method returns a ProductDto for an existing Product in the database, based on the Product's ID.
     * @param productId the ID of the Product to retrieve
     * @return a ProductDto for the specified Product
     */
    @Override
    public ProductDto getProductById(Long productId) {
        return productToDto(
                this.productRepo.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException(
                        PRODUCT, "id", productId)
                )
        );
    }

    /**
     * This method deletes an existing Product from the database.
     * @param productId the ID of the Product to delete
     * @return true if the Product was deleted, false if there was an error
     */
    @Override
    public boolean deleteProduct(Long productId) {
        try{
            this.productRepo.deleteById(productId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<ProductDto> getProductsByName(String searchString) {
        return this.productRepo.findByNameIsContainingIgnoreCase(searchString).stream()
                .map(this::productToDto)
                .limit(5)
                .toList();
    }

    /**
     * This method maps a ProductDto to a Product object.
     * @param productDto the ProductDto to map
     * @return the mapped Product object
     */
    private Product dtoToProduct(ProductDto productDto){
        return this.modelMapper.map(productDto, Product.class);
    }

    /**
     * This method maps a Product object to a ProductDto.
     * @param product the Product to map
     * @return the mapped ProductDto object
     */
    private ProductDto productToDto(Product product)
    {
        return this.modelMapper.map(product, ProductDto.class);
    }

}
