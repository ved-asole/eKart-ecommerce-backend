package com.vedasole.ekartecommercebackend.service.serviceImpl;

import com.vedasole.ekartecommercebackend.entity.Category;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.CategoryDto;
import com.vedasole.ekartecommercebackend.repository.CategoryRepo;
import com.vedasole.ekartecommercebackend.service.serviceInterface.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepo categoryRepo, ModelMapper modelMapper) {
        this.categoryRepo = categoryRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category addedCategory = this.categoryRepo.save(dtoToCategory(categoryDto));
        return categoryToDto(addedCategory);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category category = dtoToCategory(categoryDto);
        Category categoryInDB = this.categoryRepo.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException(
                        "Category", "id" , categoryId));
        categoryInDB.setCategoryId(category.getCategoryId());
        categoryInDB.setName(category.getName());
        categoryInDB.setImage(category.getImage());
        categoryInDB.setDesc(category.getDesc());
        categoryInDB.setParentCategory(category.getParentCategory());
        categoryInDB.setActive(category.isActive());

        this.categoryRepo.save(categoryInDB);

        return categoryToDto(categoryInDB);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return this.categoryRepo.findAll().stream()
                .map(this::categoryToDto)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = this.categoryRepo.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException(
                        "Category", "id" , categoryId));

        return categoryToDto(category);
    }

    @Override
    public boolean deleteCategory(Long categoryId) {
        try{
            this.categoryRepo.deleteById(categoryId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Category dtoToCategory(CategoryDto categoryDto){
        return this.modelMapper.map(categoryDto, Category.class);
    }
    private CategoryDto categoryToDto(Category category)
    {
        return this.modelMapper.map(category, CategoryDto.class);
    }
}
