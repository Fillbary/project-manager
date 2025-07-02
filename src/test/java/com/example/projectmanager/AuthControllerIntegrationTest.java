package com.example.projectmanager;

import com.example.projectmanager.dto.request.RegisterRequest;
import com.example.projectmanager.entity.User;
import com.example.projectmanager.repositorу.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.MatcherAssert.assertThat;  // Сам метод
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll(); // Очищаем базу перед каждым тестом
    }

    @Test
    public void testRegistration_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").exists());

        // Проверяем, что пользователь сохранен в БД
        User user = userRepository.findByUsername("testuser").orElseThrow();

        assertThat(user.getEmail(), is("test@example.com"));
    }

    @Test
    public void testRegistration_PasswordsNotMatch() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser2");
        request.setEmail("test2@example.com");
        request.setPassword("password123");
        request.setConfirmPassword("different");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("")));
    }

//    @Test
//    public void testLogin_Success() throws Exception {
//        // Предварительно создаем пользователя
//        User user = new User();
//        user.setUsername("loginuser");
//        user.setEmail("login@example.com");
//        user.setPassword(passwordEncoder.encode("password123"));
//        user.setRole(Role.USER);
//        userRepository.save(user);
//
//        LoginRequest request = new LoginRequest();
//        request.setUsername("loginuser");
//        request.setPassword("password123");
//
//        mockMvc.perform(post("/api/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").exists())
//                .andExpect(jsonPath("$.username").value("loginuser"))
//                .andExpect(jsonPath("$.role").value("USER"));
//    }

//    @Test
//    public void testLogin_InvalidCredentials() throws Exception {
//        LoginRequest request = new LoginRequest();
//        request.setUsername("nonexistent");
//        request.setPassword("wrong");
//
//        mockMvc.perform(post("/api/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isUnauthorized());
//    }
}

