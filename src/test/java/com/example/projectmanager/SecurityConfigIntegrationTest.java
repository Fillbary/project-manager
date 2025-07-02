package com.example.projectmanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPublicEndpoints_AccessWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/auth/register"))
                .andExpect(status().isBadRequest()); // Ожидаем 400, а не 401/403
    }

//    @Test
//    public void testSecuredEndpoints_AccessDeniedWithoutAuth() throws Exception {
//        mockMvc.perform(get("/api/projects"))
//                .andExpect(status().isUnauthorized());
//    }

    @Test
    @WithMockUser
    public void testSecuredEndpoints_AccessWithAuth() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isNotFound()); // Ожидаем 404, так как endpoint не реализован, но доступ разрешен
    }

    @Test
    public void testLogout_Accessible() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}