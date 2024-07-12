package com.vedasole.ekartecommercebackend.service.serviceInterface;

import com.vedasole.ekartecommercebackend.payload.CategoryDto;

import java.util.List;

public interface CategoryService {

    public CategoryDto createCategory(CategoryDto categoryDto);

    public CategoryDto updateCategory(CategoryDto categoryDto , Long categoryId);

    public List<CategoryDto> getAllCategories();

    public CategoryDto getCategoryById(Long categoryId);

    void deleteCategory(Long categoryId);

}
