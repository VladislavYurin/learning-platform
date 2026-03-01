package ru.mentor.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/test_init_db.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@WithMockUser
public class UserInfoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/mentor");
        registry.add("spring.datasource.username", () -> "user");
        registry.add("spring.datasource.password", () -> "psw");
        registry.add("minio.endpoint", () -> "http://localhost:9000");
        registry.add("minio.access-key", () -> "minioadmin");
        registry.add("minio.secret-key", () -> "minioadmin");
        registry.add("minio.bucket", () -> "avatars");
    }

    @Test
    void searchUsers_byEmail_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/user/search")
                        .param("query", "mentor@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("mentor@gmail.com"));
    }

    @Test
    void searchUsers_byTelegram_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/user/search")
                        .param("query", "vladislavyurin")) // убрали '@'
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tgNickname").value("vladislavyurin"))
                .andExpect(jsonPath("$[0].username").value("mentor@gmail.com"));
    }

    @Test
    void searchUsers_byName_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/user/search")
                        .param("query", "Владислав"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Владислав"))
                .andExpect(jsonPath("$[0].lastName").value("Юрин"));
    }

    @Test
    void searchUsers_byLastName_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/user/search")
                        .param("query", "Юрин"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Юрин"));
    }

    @Test
    void searchUsers_emptyQuery_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/user/search")
                        .param("query", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchUsers_noQueryParam_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/user/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchUsers_notFound_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/user/search")
                        .param("query", "nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchUsers_partialMatch_shouldReturnMultipleUsers() throws Exception {
        mockMvc.perform(get("/user/search")
                        .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));
    }

    @Test
    void searchUsers_caseInsensitive_shouldWork() throws Exception {
        mockMvc.perform(get("/user/search")
                        .param("query", "MENTOR@GMAIL.COM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("mentor@gmail.com"));
    }
}