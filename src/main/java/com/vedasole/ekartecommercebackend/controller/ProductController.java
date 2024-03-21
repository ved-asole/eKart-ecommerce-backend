package com.vedasole.ekartecommercebackend.controller;

import com.vedasole.ekartecommercebackend.payload.ApiResponse;
import com.vedasole.ekartecommercebackend.payload.ProductDto;
import com.vedasole.ekartecommercebackend.service.serviceInterface.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.CATEGORY;
import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.PRODUCTS;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(value = {"http://localhost:5173","https://ekart.vedasole.cloud","https://develop--ekart-shopping.netlify.app"}, allowCredentials = "true")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;



    /**
     * Creates a new product.
     *
     * @param productDto the details of the product to create
     * @return the created product, along with a link to it and links to related resources
     */
    @PostMapping
    public ResponseEntity<EntityModel<ProductDto>> createProduct(
        @Valid @RequestBody ProductDto productDto) {
        ProductDto createdProduct = this.productService.createProduct(productDto);
        EntityModel<ProductDto> productDtoEntityModel = EntityModel.of(
            createdProduct,
            linkTo(methodOn(ProductController.class).getProduct(createdProduct.getProductId())).withSelfRel(),
            linkTo(methodOn(CategoryController.class).getCategory(createdProduct.getCategoryId())).withRel(CATEGORY.getValue()),
            linkTo(methodOn(ProductController.class).getAllProducts()).withRel(PRODUCTS.getValue())
        );
        return new ResponseEntity<>(productDtoEntityModel, HttpStatus.CREATED);
    }

    /**
     * Updates an existing product.
     *
     * @param productDto the updated product details
     * @param productId the ID of the product to update
     * @return the updated product
     */
    @PutMapping("/{productId}")
    public ResponseEntity<EntityModel<ProductDto>> updateProduct(
        @Valid @RequestBody ProductDto productDto,
        @PathVariable Long productId) {
        ProductDto updatedProduct = this.productService.updateProduct(productDto, productId);
        EntityModel<ProductDto> productDtoEntityModel = EntityModel.of(
                updatedProduct,
                linkTo(methodOn(ProductController.class).getProduct(updatedProduct.getProductId())).withSelfRel(),
                linkTo(methodOn(CategoryController.class).getCategory(updatedProduct.getCategoryId())).withRel(CATEGORY.getValue()),
                linkTo(methodOn(ProductController.class).getAllProducts()).withRel(PRODUCTS.getValue())
        );
        return new ResponseEntity<>(productDtoEntityModel, HttpStatus.OK);
    }

    /**
     * Deletes an existing product.
     *
     * @param productId the ID of the product to delete
     * @return an API response indicating whether the deletion was successful or not
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse> deleteProduct(
            @PathVariable Long productId) {
        boolean isDeletedSuccessfully = this.productService.deleteProduct(productId);
        return isDeletedSuccessfully
                ? ResponseEntity.ok(
                        new ApiResponse(
                                "Product deleted successfully",
                                true
                        )
                )
                : new ResponseEntity<>(
                        new ApiResponse(
                                "Unable to delete the product",
                                false
                        ),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
    }

    /**
     * Returns a product with the specified ID.
     *
     * @param productId the ID of the product to retrieve
     * @return the product with the specified ID, or an error response if the product does not exist
     */
    @GetMapping("/{productId}")
    public ResponseEntity<EntityModel<ProductDto>> getProduct(
        @PathVariable Long productId
    ) {
        ProductDto productDto = this.productService.getProductById(productId);
        return ResponseEntity.ok(
                    EntityModel.of(
                            productDto,
                            linkTo(methodOn(ProductController.class).getProduct(productDto.getProductId())).withSelfRel(),
                            linkTo(methodOn(CategoryController.class).getCategory(productDto.getCategoryId())).withRel(CATEGORY.getValue())
                    )
            );
    }

    @GetMapping("/search")
    public ResponseEntity<CollectionModel<ProductDto>> getProductsByName(
            @RequestParam String searchString
    ) {
        List<ProductDto> productsByName = this.productService.getProductsByName(searchString);
        return productsByName == null || productsByName.isEmpty() ?
                new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(
                        CollectionModel.of(
                                productsByName,
                                linkTo(methodOn(ProductController.class).getProductsByName(searchString)).withSelfRel()
                        ),
                        HttpStatus.OK
                );
    }

    /**
     * Returns a list of all products.
     *
     * @return a list of all products
     */
    @GetMapping
    public ResponseEntity<CollectionModel<ProductDto>> getAllProducts(){
        List<ProductDto> allProducts = this.productService.
                getAllProducts();
        return new ResponseEntity<>(
                CollectionModel.of(
                        allProducts,
                        linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel()
                ),
                HttpStatus.OK
        );
    }

}
