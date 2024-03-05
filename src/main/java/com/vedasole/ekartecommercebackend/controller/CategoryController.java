package com.vedasole.ekartecommercebackend.controller;

import com.vedasole.ekartecommercebackend.payload.ApiResponse;
import com.vedasole.ekartecommercebackend.payload.CategoryDto;
import com.vedasole.ekartecommercebackend.service.serviceInterface.CategoryService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.CATEGORIES;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin("http://localhost:5173")
public class CategoryController {

    private final CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Creates a new category.
     *
     * @param categoryDto the category details
     * @return the created category
     */
    @PostMapping
    public ResponseEntity<EntityModel<CategoryDto>> createCategory(
            @Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = this.categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(
                EntityModel.of(
                        createdCategory,
                        linkTo(methodOn(CategoryController.class).getCategory(createdCategory.getCategoryId())).withSelfRel(),
                        linkTo(methodOn(CategoryController.class).getAllCategories()).withRel(CATEGORIES.getValue())
                ),
                HttpStatus.CREATED
        );
    }

        /**
     * Updates an existing category.
     *
     * @param categoryDto the updated category details
     * @param categoryId the ID of the category to update
     * @return the updated category
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<EntityModel<CategoryDto>> updateCategory(
            @Valid @RequestBody CategoryDto categoryDto,
            @PathVariable Long categoryId) {
        CategoryDto updatedCategory = this.categoryService.updateCategory(categoryDto, categoryId);
        return new ResponseEntity<>(
                EntityModel.of(
                        updatedCategory,
                        linkTo(methodOn(CategoryController.class).getCategory(updatedCategory.getCategoryId())).withSelfRel()
                ),
                HttpStatus.OK
        );
    }

        /**
     * Deletes an existing category.
     *
     * @param categoryId the ID of the category to delete
     * @return an ApiResponse indicating whether the deletion was successful or not
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> deleteCategory(
            @PathVariable Long categoryId) {
        this.categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(
                new ApiResponse(
                        "Category deleted successfully",
                        true),
                HttpStatus.OK);
    }

        /**
     * Returns a specific category based on its ID.
     *
     * @param categoryId the ID of the category to retrieve
     * @return the requested category, or a 404 Not Found error if the category does not exist
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<EntityModel<CategoryDto>> getCategory(
            @PathVariable Long categoryId
    ) {
        CategoryDto category = this.categoryService.getCategoryById(categoryId);
        return category == null ?
                new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                new ResponseEntity<>(
                        EntityModel.of(
                                category,
                                linkTo(methodOn(CategoryController.class).getCategory(category.getCategoryId())).withSelfRel()
                        ),
                        HttpStatus.OK
                );
    }

        /**
     * Returns a list of all categories.
     *
     * @return a list of all categories
     */
    @GetMapping
    public ResponseEntity<CollectionModel<CategoryDto>> getAllCategories(){
        List<CategoryDto> allCategories = this.categoryService.
                getAllCategories();
        return new ResponseEntity<>(
                CollectionModel.of(
                        allCategories,
                        linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel()
                ),
                HttpStatus.OK
        );
    }

}