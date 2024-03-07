package com.vedasole.ekartecommercebackend.service.serviceInterface;
import com.vedasole.ekartecommercebackend.payload.ProductDto;

import java.util.List;

public interface ProductService {

    public ProductDto createProduct(ProductDto productDto);

    public ProductDto updateProduct(ProductDto productDto , Long productId);

    public List<ProductDto> getAllProducts();

    public ProductDto getProductById(Long productId);

    boolean deleteProduct(Long productId);

    List<ProductDto> getProductsByName(String searchString);
}
