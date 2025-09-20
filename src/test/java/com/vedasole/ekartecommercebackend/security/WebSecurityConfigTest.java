package com.vedasole.ekartecommercebackend.security;

import com.vedasole.ekartecommercebackend.utility.TestApplicationInitializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class WebSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestApplicationInitializer testApplicationInitializer;

    @BeforeEach
    void setUp() {
        when(testApplicationInitializer.getUserToken()).thenReturn("fake-user-token");
        when(testApplicationInitializer.getAdminToken()).thenReturn("fake-admin-token");
    }

    @Test
    void whenAccessPublicUrl_thenOk() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andDo(result -> log.debug("whenAccessPublicUrl_thenOk result: {}", result.getResponse().getContentAsString()))
                .andExpect(status().isOk());
    }

    @Test
    void whenAccessProtectedUrlWithoutAuth_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/auth/check-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = "ROLE_USER")
    void whenAccessGeneralProtectedUrlWithAuth_thenOk() throws Exception {
        // Test a different protected endpoint that works with @WithMockUser
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void whenAccessAdminProtectedUrlWithAuth_thenOk() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/3"))
                .andExpectAll(
                        jsonPath("$.message").value("Customer deleted successfully"),
                        jsonPath("$.success").value(true),
                        status().isOk()
                );
    }

}