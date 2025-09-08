package ru.mentor.utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.mentor.dto.auth.JwtAuthResponse;

@Component
public class AuthTestUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthResponse getRegistration(MockMvc mockMvc) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/reg")
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content("""
                                                                    {
                                                                        "username": "test@example.com",
                                                                        "password": "testtesttest",
                                                                        "confirmPassword": "testtesttest",
                                                                        "tgNickname": "@tgRandomName",
                                                                        "firstName": "randomName",
                                                                        "lastName": "randomLastName"
                                                                    }
                                                                    """))
                                  .andExpect(status().isOk())
                                  .andExpect(jsonPath("$.accessToken").exists())
                                  .andExpect(jsonPath("$.refreshToken").exists())
                                  .andExpect(jsonPath("$.role").value("USER"))
                                  .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, JwtAuthResponse.class);
    }

    public JwtAuthResponse getAuth(MockMvc mockMvc) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content("""
                                                                    {
                                                                        "username": "test@example.com",
                                                                        "password": "testtesttest"
                                                                    }
                                                                    """))
                                  .andExpect(status().isOk())
                                  .andExpect(jsonPath("$.accessToken").exists())
                                  .andExpect(jsonPath("$.refreshToken").exists())
                                  .andExpect(jsonPath("$.role").value("USER"))
                                  .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, JwtAuthResponse.class);
    }

}
