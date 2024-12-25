package com.vedasole.ekartecommercebackend.service.service_impl;

import com.vedasole.ekartecommercebackend.entity.Category;
import com.vedasole.ekartecommercebackend.entity.Product;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.ProductDto;
import com.vedasole.ekartecommercebackend.repository.CategoryRepo;
import com.vedasole.ekartecommercebackend.repository.ProductRepo;
import com.vedasole.ekartecommercebackend.service.service_interface.ProductService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.CATEGORY;
import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.PRODUCT;


/**
 * This class implements the ProductService interface. It uses the ProductRepo, CategoryRepo
 * and ModelMapper objects to perform operations on the product entities.
 */
@Service
@AllArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ModelMapper modelMapper;
    private final CategoryRepo categoryRepo;

    /**
     * This method creates a new Product entity in the database and returns a ProductDto with the new entity's information.
     * @param productDto the ProductDto containing the information for the new Product
     * @return a ProductDto with the information for the new Product
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "allProducts", allEntries = true),
            @CacheEvict(value = "allProductsPage", allEntries = true),
            @CacheEvict(value = "allProductsPerCategoryPage", allEntries = true)
    })
    public ProductDto createProduct(ProductDto productDto) {

        Product product = dtoToProduct(productDto);

        product.setCategory(this.categoryRepo.findById(productDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        CATEGORY.getValue(), "id" , productDto.getCategoryId()
                        )
                )
        );
        // Added temp sku due to not null and not blank annotations for sku in Product
        product.setSku(product.getCategory().getName().substring(0, 3)
                .concat("-")
                .concat(product.getName().substring(0, 5).replace(" ", "X"))
                .concat("-")
                .concat(product.getDesc().substring(0, 3).replace(" ", "X"))
        );

        Product addedProduct = this.productRepo.save(product);

        return productToDto(addedProduct);
    }

    /**
     * This method updates an existing Product entity in the database and returns a ProductDto with the updated entity's information.
     * @param productDto the ProductDto containing the updated information for the Product
     * @param productId the ID of the Product to update
     * @return a ProductDto with the updated information for the Product
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "allProducts", allEntries = true),
            @CacheEvict(value = "allProductsPage", allEntries = true),
            @CacheEvict(value = "allProductsPerCategoryPage", allEntries = true)
    })
    public ProductDto updateProduct(ProductDto productDto, Long productId) {
        Product product = dtoToProduct(productDto);
        Product productInDB = this.productRepo.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException(
                        PRODUCT.getValue(), "id" , productId));
        productInDB.setName(product.getName());
        productInDB.setImage(product.getImage());
        productInDB.setDesc(product.getDesc());
        productInDB.setPrice(product.getPrice());
        productInDB.setQtyInStock(product.getQtyInStock());
        Optional<Category> categoryOptional = categoryRepo.findById(product.getCategory().getCategoryId());
        categoryOptional.ifPresentOrElse(
                productInDB::setCategory,
                () -> {
                    throw new ResourceNotFoundException(
                            CATEGORY.getValue(), "id", product.getCategory().getCategoryId()
                    );
                }
        );

        this.productRepo.save(productInDB);

        return productToDto(productInDB);
    }

    /**
     * This method deletes an existing Product from the database.
     *
     * @param productId the ID of the Product to delete
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "product", key = "#productId"),
            @CacheEvict(value = "allProducts", allEntries = true),
            @CacheEvict(value = "allProductsPage", allEntries = true),
            @CacheEvict(value = "allProductsPerCategoryPage", allEntries = true)
    })
    public void deleteProduct(Long productId) throws IllegalArgumentException {
        this.productRepo.deleteById(productId);
    }

    /**
     * This method returns a list of all ProductDtos for all Products in the database.
     * @return a list of all ProductDtos
     */
    @Override
    @Cacheable(value = "allProducts", sync = true)
    public List<ProductDto> getAllProducts() {
        return this.productRepo.findAll(Sort.by("productId")).stream()
                .map(this::productToDto)
                .toList();
    }

    /**
     * This method returns a list of ProductDtos for all Products in the database, based on the specified page, size, sort order, and sort by parameters.
     *
     * @param page      the page number to retrieve
     * @param size      the number of Products to retrieve
     * @param sortBy    the field to sort by
     * @param sortOrder the order to sort by
     * @return a list of ProductDtos for the specified Products
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "allProductsPage",
            key = "#page + '-' + #size + '-' + #sortBy + '-' + #sortOrder",
            sync = true
    )
    public Page<ProductDto> getAllProductsPerPage(int page, int size, String sortBy, String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        Page<Product> productsPage = productRepo.findAll(pageRequest);
        return productsPage.map(this::productToDto);
    }

    /**
     * This method returns a ProductDto for an existing Product in the database, based on the Product's ID.
     * @param productId the ID of the Product to retrieve
     * @return a ProductDto for the specified Product
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "product", key = "#productId")
    public ProductDto getProductById(Long productId) {
        return productToDto(
                this.productRepo.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException(
                        PRODUCT.getValue(), "id", productId)
                )
        );
    }

    /**
     * This method returns a list of ProductDtos for Products that match the specified search key in their name or description.
     * @param page the page number to retrieve
     * @param size the number of Products to retrieve
     * @param searchKey the search key to match against Product names and descriptions
     * @return a list of ProductDtos for the matching Products
     */
    @Override
    public List<ProductDto> getProductsByNameOrDesc(int page, int size, String searchKey) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return this.productRepo.findByNameIsContainingIgnoreCaseOrDescContainingIgnoreCase(searchKey, searchKey, pageRequest)
                .stream()
                .map(this::productToDto)
                .toList();
    }

    /**
     * This method returns a list of ProductDtos for all Products in the specified category.
     * @param categoryId the ID of the category to retrieve Products for
     * @return a list of ProductDtos for the specified category
     */
    @Override
    public List<ProductDto> getAllProductsByCategory(long categoryId) {
        return this.productRepo.findByCategoryCategoryId(categoryId)
                .stream()
                .map(this::productToDto)
                .toList();
    }

    /**
     * This method returns a list of ProductDtos for all Products in the specified category, based on the specified page, size, sort order, and sort by parameters.
     * @param categoryId the ID of the category to retrieve Products for
     * @param page the page number to retrieve
     * @param size the number of Products to retrieve
     * @param sortBy the field to sort by
     * @param sortOrder the order to sort by
     * @return a list of ProductDtos for the specified Products
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "allProductsPerCategoryPage",
            key = "#page + '-' + #size + '-' + #sortBy + '-' + #sortOrder",
            sync = true
    )
    public Page<ProductDto> getAllProductsByCategoryPerPage(long categoryId, int page, int size, String sortBy, String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        Page<Product> productsPage = productRepo.findByCategoryCategoryId(categoryId, pageRequest);
        return productsPage.map(this::productToDto);
    }

    /**
     * This method returns the total number of Products in the database.
     * @return the total number of Products
     */
    @Override
    @Transactional(readOnly = true)
    public Long getTotalProductsCount() {
        return this.productRepo.count();
    }

    /**
     * This method maps a ProductDto to a Product object.
     * @param productDto the ProductDto to map
     * @return the mapped Product object
     */
    @Override
    public Product productDtoToEntity(ProductDto productDto) {
        return dtoToProduct(productDto);
    }

    /**
     * This method maps a Product object to a ProductDto.
     * @param product the Product to map
     * @return the mapped ProductDto object
     */
    @Override
    public ProductDto productEntityToDto(Product product) {
        return productToDto(product);
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