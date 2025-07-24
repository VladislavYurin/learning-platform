package ru.mentor.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentor.ApiGatewayApplication;

@Testcontainers
@SpringBootTest(classes = ApiGatewayApplication.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("spring.sql.init.mode", () -> "always");
        registry.add("spring.liquibase.enabled", () -> "true");
    }

    @Test
    void reg_Success() throws Exception {
        mockMvc.perform(post("/auth/reg")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                 {
                                                     "username": "test@example.com",
                                                     "password": "testtesttest",
                                                     "tgName": "@tgRandomName",
                                                     "firstName": "randomName",
                                                     "lastName": "randomLastName"
                                                 }
                                                 """))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.accessToken").exists())
               .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void login_Success() throws Exception {
        // Регистрация пользователя
        mockMvc.perform(post("/auth/reg")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                 {
                                                     "username": "test1@example.com",
                                                     "password": "testtesttest",
                                                     "tgName": "@tgRandomName",
                                                     "firstName": "randomName",
                                                     "lastName": "randomLastName"
                                                 }
                                                 """))
               .andExpect(status().isOk());

        // Авторизация
        mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                 {
                                                     "username": "test1@example.com",
                                                     "password": "testtesttest"
                                                 }
                                                 """))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.accessToken").exists())
               .andExpect(jsonPath("$.refreshToken").exists());
    }

}