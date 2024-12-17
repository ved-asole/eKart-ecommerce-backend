package com.vedasole.ekartecommercebackend.service.service_interface;

import com.vedasole.ekartecommercebackend.entity.Product;
import com.vedasole.ekartecommercebackend.payload.ProductDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto , Long productId);

    List<ProductDto> getAllProducts();

    Page<ProductDto> getAllProductsPerPage(int page, int size, String sortBy, String sortOrder);

    ProductDto getProductById(Long productId);

    void deleteProduct(Long productId);

    List<ProductDto> getProductsByNameOrDesc(int page, int size, String searchKey);

    List<ProductDto> getAllProductsByCategory(long categoryId);

    Page<ProductDto> getAllProductsByCategoryPerPage(long categoryId, int page, int size, String sortBy, String sortOrder);

    Long getTotalProductsCount();

    Product productDtoToEntity(ProductDto productDto);

    ProductDto productEntityToDto(Product product);
}