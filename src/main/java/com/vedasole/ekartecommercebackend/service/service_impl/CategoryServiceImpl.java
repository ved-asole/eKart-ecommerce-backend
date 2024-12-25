package com.vedasole.ekartecommercebackend.service.service_impl;

import com.vedasole.ekartecommercebackend.entity.Category;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.CategoryDto;
import com.vedasole.ekartecommercebackend.repository.CategoryRepo;
import com.vedasole.ekartecommercebackend.service.service_interface.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.CATEGORY;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

        /**
     * Creates a new Category record in the database and returns the CategoryDTO representation of the newly created record.
     *
     * @param categoryDto the CategoryDTO representation of the Category record to be created
     * @return the CategoryDTO representation of the newly created Category record
     */
    @Override
    @Caching(evict = {
                    @CacheEvict(value = "allCategories", allEntries = true),
                    @CacheEvict(value = "allCategoriesByPage", allEntries = true),
                    @CacheEvict(value = "allParentCategories", allEntries = true),
                    @CacheEvict(value = "allParentCategoriesByPage", allEntries = true),
                    @CacheEvict(value = "totalCategoriesCount", allEntries = true)
    })
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = dtoToCategory(categoryDto);
        if(category.getParentCategory() !=  null) {
            Category parentCategory = categoryRepo.findById(category.getParentCategory().getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            CATEGORY.getValue(), "id", category.getParentCategory().getCategoryId())
                    );
            category.setParentCategory(parentCategory);
        }
        Category addedCategory = this.categoryRepo.save(category);
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
    @Caching(evict = {
            @CacheEvict(value = "category", key = "#categoryId"),
            @CacheEvict(value = "allCategories", allEntries = true),
            @CacheEvict(value = "allCategoriesByPage", allEntries = true),
            @CacheEvict(value = "allParentCategories", allEntries = true),
            @CacheEvict(value = "allParentCategoriesByPage", allEntries = true),
            @CacheEvict(value = "totalCategoriesCount", allEntries = true)
    })
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category category = dtoToCategory(categoryDto);
        Category categoryInDB = this.categoryRepo.findById(categoryId).
                orElseThrow(() -> new ResourceNotFoundException(
                        CATEGORY.getValue(), "id" , categoryId));
        categoryInDB.setCategoryId(category.getCategoryId());
        categoryInDB.setName(category.getName());
        categoryInDB.setImage(category.getImage());
        categoryInDB.setDesc(category.getDesc());
        if(category.getParentCategory() !=  null) {
            Category parentCategory = categoryRepo.findById(category.getParentCategory().getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            CATEGORY.getValue(), "id", category.getParentCategory().getCategoryId())
                    );
            categoryInDB.setParentCategory(parentCategory);
        } else {
            categoryInDB.setParentCategory(null);
        }
        categoryInDB.setActive(category.isActive());

        this.categoryRepo.save(categoryInDB);

        return categoryToDto(categoryInDB);
    }

    /**
     * Deletes a category from the database based on the given category ID.
     *
     * @param categoryId the ID of the category to be deleted
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "category", key = "#categoryId"),
            @CacheEvict(value = "allCategories", allEntries = true),
            @CacheEvict(value = "allCategoriesByPage", allEntries = true),
            @CacheEvict(value = "allParentCategories", allEntries = true),
            @CacheEvict(value = "allParentCategoriesByPage", allEntries = true),
            @CacheEvict(value = "totalCategoriesCount", allEntries = true)
    })
    public void deleteCategory(Long categoryId) {
        this.categoryRepo.deleteById(categoryId);
    }

    /**
     * Returns the total number of categories in the database.
     *
     * @return the total number of categories in the database
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "totalCategoriesCount", sync = true)
    public Long getTotalCategoriesCount() {
        return this.categoryRepo.count();
    }

    /**
     * Returns the CategoryDTO representation of the Category record with the specified id.
     *
     * @param categoryId the unique identifier of the Category record to be retrieved
     * @return the CategoryDTO representation of the Category record with the specified id
     * @throws ResourceNotFoundException if the Category record with the specified id is not found
     */
    @Override
    @Cacheable(value = "category", key = "#categoryId")
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) throws ResourceNotFoundException {
        return categoryToDto(
                this.categoryRepo.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Category", "id", categoryId)));
    }

    /**
     * Returns a list of all categories in the database as CategoryDTOs.
     *
     * @return a list of all categories in the database as CategoryDTOs
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allCategories", sync = true)
    public List<CategoryDto> getAllCategories() {
        return this.categoryRepo.findAll().stream()
                .map(this::categoryToDto)
                .toList();
    }

    /**
     * Returns a list of all parent categories in the database as CategoryDTOs.
     *
     * @return a list of all parent categories in the database as CategoryDTOs
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "allParentCategories", sync = true)
    public List<CategoryDto> getAllParentCategories() {
        return this.categoryRepo.findAllByParentCategoryIsNull().stream()
                .map(this::categoryToDto)
                .toList();
    }

    /**
     * Returns a page of parent categories in the database as CategoryDTOs.
     *
     * @return a page of parent categories in the database as CategoryDTOs
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "allParentCategoriesByPage",
            key = "#page + '-' + #size + '-' + #sortBy + '-' + #sortOrder",
            sync = true
    )
    public Page<CategoryDto>getAllParentCategoriesByPage(int page, int size, String sortBy, String sortOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        return this.categoryRepo.findAllByParentCategoryIsNull(pageRequest)
                .map(this::categoryToDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "allCategoriesByPage",
            key = "#page + '-' + #size + '-' + #sortBy + '-' + #sortOrder",
            sync = true
    )
    public Page<CategoryDto> getAllCategoriesByPage(int page, int size, String sortBy, String sortOrder) {
        return this.categoryRepo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy)))
                .map(this::categoryToDto);
    }

    /**
     * This method converts Category to CategoryDto
     * @param category Category
     * @return CategoryDto
     */
    @Override
    public CategoryDto convertToDto(Category category) {
        return categoryToDto(category);
    }

    /**
     * This method converts CategoryDto to Category
     * @param categoryDto CategoryDto
     * @return Category
     */
    @Override
    public Category convertToEntity(CategoryDto categoryDto) {
        return dtoToCategory(categoryDto);
    }

    private Category dtoToCategory(CategoryDto categoryDto){
        Category category = this.modelMapper.map(categoryDto, Category.class);
        category.setParentCategory(categoryDto.getParentCategory());
        return category;
    }
    private CategoryDto categoryToDto(Category category)
    {
        return this.modelMapper.map(category, CategoryDto.class);
    }
}