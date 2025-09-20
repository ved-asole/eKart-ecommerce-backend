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

        Category category1 = Category.builder()
                .name("Category 1")
                .image("cat_img1.jpg")
                .desc("Description 1")
                .parentCategory(null)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Category category2 = Category.builder()
                .name("Category 2")
                .image("cat_img2.jpg")
                .desc("Description 2")
                .parentCategory(null)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        category1 = categoryRepo.save(category1);
        category2 = categoryRepo.save(category2);

        // Initialize the products in the database
        Product product1 = Product.builder()
                .name("Sample Product Name 1 containing word car")
                .image("img1.jpg")
                .sku("PRD-001")
                .desc("Sample Description to test same description methods with word : apple")
                .price(100.0)
                .qtyInStock(10)
                .discount(50)
                .category(category1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Product product2 = Product.builder()
                .name("Product 2")
                .image("img2.jpg")
                .sku("PRD-002")
                .desc("Description 2")
                .price(200.0)
                .qtyInStock(20)
                .discount(40)
                .category(category1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Product product3 = Product.builder()
                .name("Sample Product Name 3 containing word car")
                .image("img3.jpg")
                .sku("PRD-003")
                .desc("Sample Description to test same description methods with word : apple")
                .price(300.0)
                .qtyInStock(30)
                .discount(30)
                .category(category2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
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