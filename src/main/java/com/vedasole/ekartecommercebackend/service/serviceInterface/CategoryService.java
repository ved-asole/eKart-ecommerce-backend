package com.vedasole.ekartecommercebackend.service.serviceInterface;

import com.vedasole.ekartecommercebackend.payload.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto , Long categoryId);

    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(Long categoryId);

    void deleteCategory(Long categoryId);

}
