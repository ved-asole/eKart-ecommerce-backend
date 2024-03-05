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

        /**
     * Creates a new Category record in the database and returns the CategoryDTO representation of the newly created record.
     *
     * @param categoryDto the CategoryDTO representation of the Category record to be created
     * @return the CategoryDTO representation of the newly created Category record
     */
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category addedCategory = this.categoryRepo.save(dtoToCategory(categoryDto));
        return categoryToDto(addedCategory);
    }

        /**
     * Updates an existing Category record in the database and returns the CategoryDTO representation of the updated record.
     *
     * @param categoryDto the CategoryDTO representation of the updated Category record
     * @param categoryId the unique identifier of the Category record to be updated
     * @return the CategoryDTO representation of the updated Category record
     */
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

    /**
     * Returns a list of all categories in the database as CategoryDTOs.
     *
     * @return a list of all categories in the database as CategoryDTOs
     */
    @Override
    public List<CategoryDto> getAllCategories() {
        return this.categoryRepo.findAll().stream()
                .map(this::categoryToDto)
                .toList();
    }

    /**
     * Returns the CategoryDTO representation of the Category record with the specified id.
     *
     * @param categoryId the unique identifier of the Category record to be retrieved
     * @return the CategoryDTO representation of the Category record with the specified id
     * @throws ResourceNotFoundException if the Category record with the specified id is not found
     */
    @Override
    public CategoryDto getCategoryById(Long categoryId) throws ResourceNotFoundException {
        return categoryToDto(
                this.categoryRepo.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Category", "id", categoryId)));
    }

    /**
     * Deletes a category from the database based on the given category ID.
     *
     * @param categoryId the ID of the category to be deleted
     * @return true if the category was successfully deleted, false otherwise
     */
    @Override
    public boolean deleteCategory(Long categoryId) {
        try {
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
