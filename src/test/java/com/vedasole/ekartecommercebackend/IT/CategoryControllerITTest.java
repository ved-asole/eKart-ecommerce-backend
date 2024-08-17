package com.vedasole.ekartecommercebackend.IT;

import com.vedasole.ekartecommercebackend.entity.Category;
import com.vedasole.ekartecommercebackend.payload.CategoryDto;
import com.vedasole.ekartecommercebackend.repository.CategoryRepo;
import com.vedasole.ekartecommercebackend.service.serviceInterface.CategoryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerITTest {

    private static RestTemplate restTemplate;
    @LocalServerPort
    private int port;
    private String baseUrl="http://localhost";
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private CategoryService categoryService;
    private Category expected;

    @BeforeAll
    static void init() {
        restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.getObjectMapper().registerModule(new Jackson2HalModule());
        restTemplate.getMessageConverters().add(0, converter);
    }

    @BeforeEach
    void setUp() {
        baseUrl = baseUrl.concat(":").concat(String.valueOf(port)).concat("/api/v1/categories");

        Category category = new Category(
                1L,
                "Mobiles & Tablets",
                "/images/categories/mobile-and-tablets.webp",
                "Mobile phones are no more merely a part of our lives. Whether it's to stay connected with friends and family or to keep abreast of important developments around the world, mobiles are no longer for sending a text or making a call. From budget to state-of-the-art smartphones; indigenous names to global big-wigs - a whole universe of mobiles await you on Flipkart. Whether you’re looking for waterdrop notch screens, a high screen to body ratio, AI-powered sensational cameras, high storage capacity, blazing quick processing engines or reflective glass designs, rest assured you won’t have to venture anywhere else for your smartphone needs.",
                null,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        expected = categoryRepo.save(category);
    }

    @AfterEach
    void tearDown() {
        categoryRepo.deleteAll();
    }

    @Test
    void testCreateCategory() {
        categoryRepo.deleteAll();
        Category category = new Category(
                1L,
                "Mobiles & Tablets",
                "/images/categories/mobile-and-tablets.webp",
                "Mobile phones are no more merely a part of our lives. Whether it's to stay connected with friends and family or to keep abreast of important developments around the world, mobiles are no longer for sending a text or making a call. From budget to state-of-the-art smartphones; indigenous names to global big-wigs - a whole universe of mobiles await you on Flipkart. Whether you’re looking for waterdrop notch screens, a high screen to body ratio, AI-powered sensational cameras, high storage capacity, blazing quick processing engines or reflective glass designs, rest assured you won’t have to venture anywhere else for your smartphone needs.",
                null,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        ResponseEntity<CategoryDto> categoryDtoResponseEntity = restTemplate.postForEntity(baseUrl, category, CategoryDto.class);
        assert categoryDtoResponseEntity.getStatusCode().is2xxSuccessful();
        assertThat(categoryDtoResponseEntity.getBody()).isNotNull();
        CategoryDto body = categoryDtoResponseEntity.getBody();
        category.setCategoryId(body.getCategoryId());
        assertThat(body).isEqualTo(categoryService.convertToDto(category));

        CategoryDto createCategoryDto = categoryDtoResponseEntity.getBody();
        assertThat(createCategoryDto.getName()).isEqualTo(category.getName());
        assertThat(createCategoryDto.getImage()).isEqualTo(category.getImage());
        assertThat(createCategoryDto.getDesc()).isEqualTo(category.getDesc());
        assertThat(createCategoryDto.getParentCategory()).isEqualTo(category.getParentCategory());
        assertThat(createCategoryDto.isActive()).isEqualTo(category.isActive());
    }

    /**
     * This test method is responsible for updating an existing category in the system.
     *
     * @throws Exception If any error occurs during the test.
     */
    @Test
    void updateCategory() {
        // Set the updated values for the expected category
        expected.setName("Electronics");
        expected.setImage("/images/categories/electronics.webp");
        expected.setDesc("Choose from a wide range of products such as washing machine, camera, laptop & many more from the top brands & get exciting deals & offers");
        expected.setActive(false);

        // Send a PUT request to the server to update the category
        restTemplate.put(baseUrl.concat("/").concat(String.valueOf(expected.getCategoryId())), expected);

        // Retrieve the updated category from the database
        Optional<Category> categoryOptional = categoryRepo.findById(expected.getCategoryId());
        assertTrue(categoryOptional.isPresent());
        Category updatedCategory = categoryOptional.get();

        // Validate the updated category
        assertThat(updatedCategory).isNotNull();
        assertThat(updatedCategory.getCreatedAt()).isBefore(expected.getCreatedAt().plusSeconds(1));
        assertThat(updatedCategory.getUpdatedAt()).isAfter(expected.getUpdatedAt());
        expected.setUpdatedAt(updatedCategory.getUpdatedAt());
        expected.setCreatedAt(updatedCategory.getCreatedAt());
        assertThat(updatedCategory).isEqualTo(expected);
    }

    @Test
    void deleteCategory() {
        List<Category> categories = categoryRepo.findAll();
        if(!categories.isEmpty()) {
            Category category = categories.get(0);
            restTemplate.delete(baseUrl.concat("/").concat(String.valueOf(category.getCategoryId())));
            categoryRepo.findById(category.getCategoryId()).ifPresentOrElse(
                    c -> Assertions.fail("Category not deleted"),
                    () -> assertTrue(true)
            );
        } else {
            Assertions.fail("No categories found to delete");
        }
    }

    @Test
    void getCategory() {
        ResponseEntity<CategoryDto> categoryDtoResponseEntity = restTemplate.getForEntity(
                baseUrl.concat("/").concat(Long.toString(expected.getCategoryId())), CategoryDto.class
        );
        assert categoryDtoResponseEntity.getStatusCode().is2xxSuccessful();
        assertThat(categoryDtoResponseEntity.getBody()).isNotNull().isEqualTo(categoryService.convertToDto(expected));
    }

    @Test
    void getAllCategories() {
        Category category = new Category(
                2L,
                "Electronics",
                "/images/categories/electronics.webp",
                "Choose from a wide range of products such as washing machine, camera, laptop & many more from the top brands & get exciting deals & offers",
                null,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        categoryRepo.save(category);

        ParameterizedTypeReference<CollectionModel<CategoryDto>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<CollectionModel<CategoryDto>> responseEntity = restTemplate.exchange(baseUrl, HttpMethod.GET, null, responseType);

        CollectionModel<CategoryDto> collectionModel = responseEntity.getBody();
        assertThat(collectionModel).isNotNull();
        assertThat(collectionModel.getContent()).isNotNull().hasSize(categoryRepo.findAll().size());
        List<CategoryDto> categoryDtoList = new ArrayList<>(collectionModel.getContent());
        assertThat(categoryDtoList).isNotNull().contains(categoryService.convertToDto(expected));
        assertThat(categoryDtoList.stream()
                        .filter(dto -> dto.getName().equals(category.getName()))
                        .findFirst()
        ).isPresent();
    }

}