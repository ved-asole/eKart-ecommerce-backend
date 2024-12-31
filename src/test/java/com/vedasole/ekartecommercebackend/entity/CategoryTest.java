package com.vedasole.ekartecommercebackend.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void shouldCreateCategoryWithValidName() {
        // Arrange
        String expectedName = "Electronics";

        // Act
        Category category = Category.builder()
                .categoryId(0)
                .name(expectedName)
                .active(true)
                .build();

        // Assert
        assertEquals(expectedName, category.getName());
    }

    @Test
    void shouldCreateCategoryWithActiveValueFalse() {
        // Act
        Category category = Category.builder()
                .categoryId(0)
                .name("Home Appliances")
                .active(false)
                .build();

        // Assert
        assertFalse(category.isActive());
    }

    @Test
    void shouldCreateCategoryWithParentCategory() {
        // Arrange
        Category parentCategory = Category.builder()
                .categoryId(0)
                .name("Parent Category")
                .active(true)
                .build();

        // Act
        Category category = Category.builder()
                .categoryId(0)
                .name("Child Category")
                .active(true)
                .parentCategory(parentCategory)
                .build();

        // Assert
        assertNotNull(category.getParentCategory());
        assertEquals("Parent Category", category.getParentCategory().getName());
    }

    @Test
    void shouldCreateCategoryWithImageAndVerifyImageUrlIsSetCorrectly() {
        // Arrange
        String expectedImageUrl = "http://example.com/image.jpg";

        // Act
        Category category = Category.builder()
                .categoryId(0)
                .name("Fashion")
                .active(true)
                .image(expectedImageUrl)
                .build();

        // Assert
        assertEquals(expectedImageUrl, category.getImage());
    }

    @Test
    void shouldCreateCategoryWithDescriptionAndVerifyDescriptionIsSetCorrectly() {
        // Arrange
        String expectedDescription = "This category includes all electronic gadgets.";

        // Act
        Category category = Category.builder()
                .categoryId(0)
                .name("Electronics")
                .active(true)
                .desc(expectedDescription)
                .build();

        // Assert
        assertEquals(expectedDescription, category.getDesc());
    }

}