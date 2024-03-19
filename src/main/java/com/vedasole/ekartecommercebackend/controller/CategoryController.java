package com.vedasole.ekartecommercebackend.controller;

import com.vedasole.ekartecommercebackend.payload.ApiResponse;
import com.vedasole.ekartecommercebackend.payload.CategoryDto;
import com.vedasole.ekartecommercebackend.service.serviceInterface.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.RELATIONS.CATEGORIES;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(value = {"http://localhost:5173","https://ekart.vedasole.cloud"})
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

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
        Link selfLink = linkTo(methodOn(CategoryController.class).getCategory(createdCategory.getCategoryId())).withSelfRel();
        Link categoriesLink = linkTo(methodOn(CategoryController.class).getAllCategories()).withRel(CATEGORIES.getValue());
        return ResponseEntity
                .created(URI.create(selfLink.getHref()))
                .body(EntityModel.of(createdCategory, selfLink, categoriesLink));
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
        CategoryDto updatedCategory = categoryService.updateCategory(categoryDto, categoryId);
        Link selfLink = linkTo(CategoryController.class).slash(updatedCategory.getCategoryId()).withSelfRel();
        return ResponseEntity.ok(EntityModel.of(updatedCategory, selfLink));
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
        boolean deletedSuccessfully = this.categoryService.deleteCategory(categoryId);
        if(!deletedSuccessfully) return new ResponseEntity<>(new ApiResponse("Unable to delete the cateogry", deletedSuccessfully), HttpStatus.INTERNAL_SERVER_ERROR);
        else return ResponseEntity.ok(new ApiResponse("Customer deleted Category", deletedSuccessfully));
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
        if (category == null) {
            return ResponseEntity.notFound().build();
        } else {
            Link selfLink = linkTo(CategoryController.class).slash(category.getCategoryId()).withSelfRel();
            Link allCategoriesLink = linkTo(CategoryController.class).slash("all").withRel(CATEGORIES.getValue());
            return ResponseEntity.ok(EntityModel.of(category, selfLink, allCategoriesLink));
        }
    }

        /**
     * Returns a list of all categories.
     *
     * @return a list of all categories
     */
    @GetMapping
    public ResponseEntity<CollectionModel<CategoryDto>> getAllCategories(){
        List<CategoryDto> allCategories = this.categoryService.getAllCategories();
        return new ResponseEntity<>(
                CollectionModel.of(
                        allCategories,
                        linkTo(methodOn(CategoryController.class).getAllCategories()).withSelfRel()
                ),
                HttpStatus.OK
        );
    }
}