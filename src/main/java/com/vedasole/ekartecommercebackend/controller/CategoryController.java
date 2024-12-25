package com.vedasole.ekartecommercebackend.controller;

import com.vedasole.ekartecommercebackend.payload.ApiResponse;
import com.vedasole.ekartecommercebackend.payload.CategoryDto;
import com.vedasole.ekartecommercebackend.service.service_interface.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@CrossOrigin(value = {
        "http://localhost:5173",
        "https://ekart.vedasole.me",
        "https://ekart-shopping.netlify.app",
        "https://develop--ekart-shopping.netlify.app"
}, allowCredentials = "true")
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
            @PathVariable Long categoryId
    ) {
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
            @PathVariable Long categoryId
    ) {
        try {
            this.categoryService.deleteCategory(categoryId);
            return ResponseEntity.ok(
                    new ApiResponse(
                    "Category deleted successfully",
                    true
                    )
            );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
     * Returns a list of all parent categories.
     *
     * @return a list of all parent categories
     */
    @GetMapping("/parent")
    public ResponseEntity<CollectionModel<CategoryDto>> getAllParentCategories(){
        List<CategoryDto> allParentCategories = this.categoryService.getAllParentCategories();
        return new ResponseEntity<>(
                CollectionModel.of(
                        allParentCategories,
                        linkTo(methodOn(CategoryController.class).getAllParentCategories()).withSelfRel()
                ),
                HttpStatus.OK
        );
    }

    /**
     * Returns a page of all parent categories.
     *
     * @return a page of all parent categories
     */
    @GetMapping("/parent/page")
    public ResponseEntity<Page<CategoryDto>> getAllParentCategoriesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "categoryId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ){
        Page<CategoryDto> allParentCategoriesByPage = this.categoryService.getAllParentCategoriesByPage(page, size, sortBy, sortOrder);
        return new ResponseEntity<>(allParentCategoriesByPage,HttpStatus.OK);
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
  
    /**
     * Returns a paginated list of all categories.
     *
     * @param page the page number
     * @param size the number of items per page
     * @param sortBy the field to sort by
     * @param sortOrder the sort order
     * @return a paginated list of all categories
     */
    @GetMapping("/page")
    public ResponseEntity<Page<CategoryDto>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "categoryId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ){
        Page<CategoryDto> allCategoriesByPage = this.categoryService.getAllCategoriesByPage(page, size, sortBy, sortOrder);
        return new ResponseEntity<>(allCategoriesByPage,HttpStatus.OK);
    }
  
    /**
     * Returns the total number of categories in the database.
     *
     * @return the total number of categories in the database
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCategoriesCount() {
        return ResponseEntity.ok(this.categoryService.getTotalCategoriesCount());
    }

}