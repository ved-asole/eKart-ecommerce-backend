package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
class CategoryRepoTest {

    @Autowired
    private CategoryRepo categoryRepo;
    private final CategoryRepo mockCategoryRepo = mock(CategoryRepo.class);

    @BeforeEach
    void setUp() {
        categoryRepo.deleteAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoParentCategories() {
        // Given
        // Assuming no categories are saved in the database

        // When
        List<Category> result = categoryRepo.findAllByParentCategoryIsNull();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAllTopLevelCategoriesWhenFindAllByParentCategoryIsNullIsCalled() {
        // Given
        Category category1 = new Category();
        category1.setName("Electronics");
        Category category2 = new Category();
        category2.setName("Clothing");
        Category subCategory = new Category();
        subCategory.setName("Smartphones");
        subCategory.setParentCategory(category1);

        categoryRepo.saveAll(Arrays.asList(category1, category2, subCategory));

        // When
        List<Category> result = categoryRepo.findAllByParentCategoryIsNull();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("Electronics")));
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("Clothing")));
        assertFalse(result.stream().anyMatch(c -> c.getName().equals("Smartphones")));
    }

    @Test
    void shouldReturnPaginatedTopLevelCategoriesWhenFindAllByParentCategoryIsNullIsCalledWithPageable() {
        // Given
        Category category1 = new Category();
        category1.setName("Electronics");
        Category category2 = new Category();
        category2.setName("Clothing");
        Category category3 = new Category();
        category3.setName("Books");
        Category subCategory = new Category();
        subCategory.setName("Smartphones");
        subCategory.setParentCategory(category1);

        categoryRepo.saveAll(Arrays.asList(category1, category2, category3, subCategory));

        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<Category> result = categoryRepo.findAllByParentCategoryIsNull(pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(2, result.getSize());
        assertEquals(0, result.getNumber());
        assertTrue(result.getContent().stream().allMatch(c -> c.getParentCategory() == null));
    }

    @Test
    void shouldReturnCorrectNumberOfTopLevelCategories() {
        // Given
        Category category1 = new Category();
        category1.setName("Electronics");
        Category category2 = new Category();
        category2.setName("Clothing");
        Category category3 = new Category();
        category3.setName("Books");
        Category subCategory = new Category();
        subCategory.setName("Smartphones");
        subCategory.setParentCategory(category1);

        categoryRepo.saveAll(Arrays.asList(category1, category2, category3, subCategory));

        // When
        List<Category> result = categoryRepo.findAllByParentCategoryIsNull();

        // Then
        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("Electronics")));
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("Clothing")));
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("Books")));
        assertFalse(result.stream().anyMatch(c -> c.getName().equals("Smartphones")));
    }

    @Test
    void shouldNotReturnAnyChildCategoriesWhenFindAllByParentCategoryIsNullIsCalled() {
        // Given
        Category parentCategory = new Category();
        parentCategory.setName("Electronics");
        Category childCategory1 = new Category();
        childCategory1.setName("Smartphones");
        childCategory1.setParentCategory(parentCategory);
        Category childCategory2 = new Category();
        childCategory2.setName("Laptops");
        childCategory2.setParentCategory(parentCategory);

        categoryRepo.saveAll(Arrays.asList(parentCategory, childCategory1, childCategory2));

        // When
        List<Category> result = categoryRepo.findAllByParentCategoryIsNull();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getName().equals("Electronics")));
        assertFalse(result.stream().anyMatch(c -> c.getName().equals("Smartphones")));
        assertFalse(result.stream().anyMatch(c -> c.getName().equals("Laptops")));
    }

    @Test
    void shouldReturnCategoriesInCorrectOrderWhenSorted() {
        // Given
        Category category1 = new Category();
        category1.setName("Electronics");
        Category category2 = new Category();
        category2.setName("Books");
        Category category3 = new Category();
        category3.setName("Clothing");
        Category subCategory = new Category();
        subCategory.setName("Smartphones");
        subCategory.setParentCategory(category1);

        categoryRepo.saveAll(Arrays.asList(category1, category2, category3, subCategory));

        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageable = PageRequest.of(0, 10, sort);

        // When
        Page<Category> result = categoryRepo.findAllByParentCategoryIsNull(pageable);

        // Then
        assertEquals(3, result.getContent().size());
        assertEquals("Books", result.getContent().get(0).getName());
        assertEquals("Clothing", result.getContent().get(1).getName());
        assertEquals("Electronics", result.getContent().get(2).getName());
    }

    @Test
    void shouldReturnCorrectPageSizeWhenPaginated() {
        // Given
        Category category1 = new Category();
        category1.setName("Electronics");
        Category category2 = new Category();
        category2.setName("Clothing");
        Category category3 = new Category();
        category3.setName("Books");
        Category category4 = new Category();
        category4.setName("Toys");
        Category subCategory = new Category();
        subCategory.setName("Smartphones");
        subCategory.setParentCategory(category1);

        categoryRepo.saveAll(Arrays.asList(category1, category2, category3, category4, subCategory));

        int pageSize = 2;
        Pageable pageable = PageRequest.of(0, pageSize);

        // When
        Page<Category> result = categoryRepo.findAllByParentCategoryIsNull(pageable);

        // Then
        assertEquals(pageSize, result.getContent().size());
        assertEquals(4, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    void shouldHandleLargeNumberOfCategoriesEfficiently() {
        // Given
        int numberOfCategories = 10000;
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < numberOfCategories; i++) {
            Category category = new Category();
            category.setName("Category " + i);
            categories.add(category);
        }
        categoryRepo.saveAll(categories);

        // When
        long startTime = System.currentTimeMillis();
        Page<Category> result = categoryRepo.findAllByParentCategoryIsNull(PageRequest.of(0, 100));
        long endTime = System.currentTimeMillis();

        // Then
        assertEquals(numberOfCategories, result.getTotalElements());
        System.out.println("Query took " + (endTime - startTime) + " milliseconds to execute");
        assertTrue((endTime - startTime) < 3000, "Query took longer than 2 second to execute");
    }

    @Test
    void shouldReturnConsistentResultsAcrossMultipleCalls() {
        // Given
        Category category1 = new Category();
        category1.setName("Electronics");
        Category category2 = new Category();
        category2.setName("Clothing");
        Category subCategory = new Category();
        subCategory.setName("Smartphones");
        subCategory.setParentCategory(category1);

        categoryRepo.saveAll(Arrays.asList(category1, category2, subCategory));

        // When
        List<Category> result1 = categoryRepo.findAllByParentCategoryIsNull();
        List<Category> result2 = categoryRepo.findAllByParentCategoryIsNull();
        List<Category> result3 = categoryRepo.findAllByParentCategoryIsNull();

        // Then
        assertEquals(2, result1.size());
        assertEquals(2, result2.size());
        assertEquals(2, result3.size());

        assertTrue(result1.containsAll(result2) && result2.containsAll(result1));
        assertTrue(result2.containsAll(result3) && result3.containsAll(result2));
        assertTrue(result1.containsAll(result3) && result3.containsAll(result1));
    }

    @Test
    void shouldThrowExceptionWhenDatabaseConnectionFails() {
        // Given
        doThrow(new DataAccessResourceFailureException("Database connection failed"))
            .when(mockCategoryRepo).findAllByParentCategoryIsNull();

        // When & Then
        assertThrows(DataAccessResourceFailureException.class, mockCategoryRepo::findAllByParentCategoryIsNull);

        // Verify
        verify(mockCategoryRepo, times(1)).findAllByParentCategoryIsNull();
    }

}