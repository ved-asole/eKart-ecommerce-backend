package com.vedasole.ekartecommercebackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vedasole.ekartecommercebackend.config.TestMailConfig;
import com.vedasole.ekartecommercebackend.entity.Category;
import com.vedasole.ekartecommercebackend.payload.CategoryDto;
import com.vedasole.ekartecommercebackend.service.service_interface.CategoryService;
import com.vedasole.ekartecommercebackend.utility.TestApplicationInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestMailConfig.class)
class CategoryControllerTest {

    @LocalServerPort
    private int port;
    private String baseUrl="http://localhost";
    @MockBean
    private CategoryService categoryService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestApplicationInitializer testApplicationInitializer;

    private Category expected;

    @BeforeEach
    void setUp() {
        baseUrl = baseUrl.concat(":").concat(String.valueOf(port)).concat("/api/v1/categories");

        expected = new Category(
                1L,
                "Mobiles & Tablets",
                "/images/categories/mobile-and-tablets.webp",
                "Mobile phones are no more merely a part of our lives. Whether it's to stay connected with friends and family or to keep abreast of important developments around the world, mobiles are no longer for sending a text or making a call. From budget to state-of-the-art smartphones; indigenous names to global big-wigs - a whole universe of mobiles await you on Flipkart. Whether you’re looking for waterdrop notch screens, a high screen to body ratio, AI-powered sensational cameras, high storage capacity, blazing quick processing engines or reflective glass designs, rest assured you won’t have to venture anywhere else for your smartphone needs.",
                null,
                true,
                LocalDateTime.of(2022, 1, 1, 1, 0),
                LocalDateTime.of(2022, 1, 1, 1, 0)
        );
    }

    /**
     * This test method is responsible for creating a new category in the system.
     *
     * @throws Exception If any error occurs during the test.
     */
    @Test
    void testCreateCategory() throws Exception {
        //given
        given(categoryService.createCategory(convertToCategoryDto(expected))).willReturn(convertToCategoryDto(expected));

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(baseUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer ".concat(testApplicationInitializer.getAdminToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(convertToCategoryDto(expected)))
                )
               // then
                .andExpect(status().isCreated());
        compareCategoryDto("$", resultActions, expected);
    }

    /**
     * This test method is responsible for updating an existing category in the system.
     *
     * @throws Exception If any error occurs during the test.
     */
    @Test
    void updateCategory() throws Exception {
        // Set the updated values for the expected category
        expected.setName("Electronics");
        expected.setImage("/images/categories/electronics.webp");
        expected.setDesc("Choose from a wide range of products such as washing machine, camera, laptop & many more from the top brands & get exciting deals & offers");
        expected.setActive(false);

        given(categoryService.updateCategory(convertToCategoryDto(expected), expected.getCategoryId())).willReturn(convertToCategoryDto(expected));

        // Send a PUT request to the server to update the category
        ResultActions resultActions = mockMvc.perform(
                        put(baseUrl.concat("/").concat(String.valueOf(expected.getCategoryId())))
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Authorization","Bearer ".concat(testApplicationInitializer.getAdminToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(convertToCategoryDto(expected)))
                )
                .andExpect(status().isOk());
        compareCategoryDto("$", resultActions, expected);
    }

    /**
     * This test method is responsible for retrieving all categories from the system.
     *
     * @throws Exception If any error occurs during the test.
     */
    @Test
    void deleteCategory() throws Exception {
        //when
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(baseUrl.concat("/").concat(String.valueOf(expected.getCategoryId())))
                                .accept(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer ".concat(testApplicationInitializer.getAdminToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"))
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * This test method is responsible for retrieving a specific category based on its ID.
     *
     * @throws Exception If any error occurs during the test.
     */
    @Test
    void getCategory() throws Exception {
        //given
        String categoryId = String.valueOf(expected.getCategoryId());
        given(categoryService.getCategoryById(expected.getCategoryId())).willReturn(convertToCategoryDto(expected));

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(baseUrl.concat("/").concat(categoryId))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpect(status().isOk());
        compareCategoryDto("$", resultActions, expected);
    }

    /**
     * This test method is responsible for retrieving all categories from the system.
     *
     * @throws Exception If any error occurs during the test.
     */
    @Test
    void getAllCategories() throws Exception {
        //given
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
        List<Category> categories= new ArrayList<>(List.of(expected, category));
        List<CategoryDto> categoryDtoList = categories.stream().map(this::convertToCategoryDto).toList();
        given(categoryService.getAllCategories()).willReturn(categoryDtoList);

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(baseUrl)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.categories").exists())
                .andExpect(jsonPath("$._embedded.categories").isArray());
        compareCategoryDto("$._embedded.categories[0]", resultActions, expected);
        compareCategoryDto("$._embedded.categories[1]", resultActions, category);
    }

    public void compareCategoryDto(String startingPathExpression, ResultActions resultActions, Category compareWith) throws Exception {
        if (compareWith== null) compareWith = expected;
        resultActions.andExpect(jsonPath("$").exists())
                .andExpect(jsonPath(startingPathExpression + ".categoryId").value(compareWith.getCategoryId()))
                .andExpect(jsonPath(startingPathExpression + ".name").value(compareWith.getName()))
                .andExpect(jsonPath(startingPathExpression + ".image").value(compareWith.getImage()))
                .andExpect(jsonPath(startingPathExpression + ".desc").value(compareWith.getDesc()))
                .andExpect(jsonPath(startingPathExpression + ".parentCategory").value(compareWith.getParentCategory()))
                .andExpect(jsonPath(startingPathExpression + ".active").value(compareWith.isActive()));
    }

    private CategoryDto convertToCategoryDto(Category expected) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(expected, CategoryDto.class);
    }

}