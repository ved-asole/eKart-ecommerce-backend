package com.vedasole.ekartecommercebackend.repository;

import com.vedasole.ekartecommercebackend.entity.Category;
import com.vedasole.ekartecommercebackend.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductRepoTest {

    @Autowired
    private ProductRepo underTest;

    @Autowired
    private CategoryRepo categoryRepo;

    @BeforeEach
    void setUp() {

        Category category1 = new Category(1L, "Category 1", "cat_img1.jpg", "Description 1", null, true, LocalDateTime.now(), LocalDateTime.now());
        Category category2 = new Category(2L, "Category 2", "cat_img2.jpg", "Description 2", null, true, LocalDateTime.now(), LocalDateTime.now());

        category1 = categoryRepo.save(category1);
        category2 = categoryRepo.save(category2);

        // Initialize the products in the database
        Product product1 = new Product(
                1L,
                "Sample Product Name 1 containing word car",
                "img1.jpg",
                "PRD-001",
                "Sample Description to test same description methods with word : apple",
                100.0,
                10,
                50,
                category1,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Product product2 = new Product(
                2L,
                "Product 2",
                "img2.jpg",
                "PRD-002",
                "Description 2",
                200.0,
                20,
                40,
                category1,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Product product3 = new Product(
                3L,
                "Sample Product Name 3 containing word car",
                "img3.jpg",
                "PRD-003",
                "Sample Description to test same description methods with word : apple",
                300.0,
                30,
                30,
                category2,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        underTest.saveAll(List.of(product1, product2, product3));
    }

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldFindProductContainingNameWithIgnoreCaseWhenExists() {

        //When
        List<Product> products = underTest.findByNameIsContainingIgnoreCaseOrDescContainingIgnoreCase("Car", null, PageRequest.of(0, 10));

        //Then
        assertThat(products).hasSize(2);
        products.forEach(product -> assertThat(product.getName()).containsIgnoringCase("Car"));

    }

    @Test
    void shouldNotFindProductContainingNameWithIgnoreCase() {

        //When
        List<Product> products = underTest.findByNameIsContainingIgnoreCaseOrDescContainingIgnoreCase("Banana", null, PageRequest.of(0, 10));

        //Then
        assertTrue(products.isEmpty());

    }

    @Test
    void shouldFindProductContainingDescWithIgnoreCaseWhenExists() {

        //When
        List<Product> products = underTest.findByNameIsContainingIgnoreCaseOrDescContainingIgnoreCase(null, "Apple", PageRequest.of(0, 10));

        //Then
        assertThat(products).hasSize(2);
        products.forEach(product -> assertThat(product.getDesc()).containsIgnoringCase("Apple"));

    }

    @Test
    void shouldNotFindProductContainingDescWithIgnoreCase() {

        //When
        List<Product> products = underTest.findByNameIsContainingIgnoreCaseOrDescContainingIgnoreCase(null, "Banana", PageRequest.of(0, 10));

        //Then
        assertTrue(products.isEmpty());

    }

    @Test
    void shouldFindProductByCategoryWhenExists() {

        //Given
        List<Category> categories = categoryRepo.findAll();
        Optional<Category> categoryOptional = categories.stream()
                .filter(category -> category.getName().equalsIgnoreCase("Category 1"))
                .findFirst();
        assertTrue(categoryOptional.isPresent());
        long categoryId = categoryOptional.get().getCategoryId();

        //When
        List<Product> products = underTest.findByCategoryCategoryId(categoryId);

        //Then
        assertThat(products).hasSize(2);
        products.forEach(product -> {
            assertThat(product.getCategory().getCategoryId()).isEqualTo(categoryId);
            assertThat(product.getCategory()).isEqualTo(categoryOptional.get());
        });

    }

    @Test
    void shouldNotFindAnyProductsByCategoryAndThrowsIllegalArgumentException() {
        List<Product> savedProducts = underTest.findByCategoryCategoryId(0L);
        assertTrue(savedProducts.isEmpty());
    }

}