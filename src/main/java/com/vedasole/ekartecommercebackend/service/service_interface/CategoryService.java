package com.vedasole.ekartecommercebackend.service.service_interface;

import com.vedasole.ekartecommercebackend.entity.Category;
import com.vedasole.ekartecommercebackend.payload.CategoryDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto updateCategory(CategoryDto categoryDto , Long categoryId);

    List<CategoryDto> getAllParentCategories();

    Page<CategoryDto> getAllParentCategoriesByPage(int page, int size, String sortBy, String sortOrder);

    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(Long categoryId);

    Page<CategoryDto> getAllCategoriesByPage(int page, int size, String sortBy, String sortOrder);

    void deleteCategory(Long categoryId);

    Long getTotalCategoriesCount();

    CategoryDto convertToDto(Category category);

    Category convertToEntity(CategoryDto categoryDto);

}