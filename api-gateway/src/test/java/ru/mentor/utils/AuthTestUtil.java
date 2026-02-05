package ru.mentor.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.mentor.dto.auth.JwtAuthResponse;

@Component
public class AuthTestUtil {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthResponse getRegistration(MockMvc mockMvc) throws Exception {
        MockMultipartFile requestPart = new MockMultipartFile(
                "request",
                "request.json",
                MediaType.APPLICATION_JSON_VALUE,
                """
                {
                    "username": "test@example.com",
                    "password": "testtesttest",
                    "confirmPassword": "testtesttest",
                    "tgNickname": "@tgRandomName",
                    "firstName": "randomName",
                    "lastName": "randomLastName"
                }
                """.getBytes(StandardCharsets.UTF_8)
        );

        MvcResult result = mockMvc.perform(
                                          MockMvcRequestBuilders.multipart("/auth/reg")
                                                                .file(requestPart)
                                  )
                                  .andExpect(MockMvcResultMatchers.status().isOk())
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists())
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").exists())
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("USER"))
                                  .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, JwtAuthResponse.class);
    }

    public JwtAuthResponse getAuth(MockMvc mockMvc) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                                                                 .contentType(MediaType.APPLICATION_JSON)
                                                                 .content("""
                                                                                  {
                                                                                      "username": "test@example.com",
                                                                                      "password": "testtesttest"
                                                                                  }
                                                                                  """))
                                  .andExpect(MockMvcResultMatchers.status().isOk())
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken")
                                                                  .exists())
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken")
                                                                  .exists())
                                  .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("USER"))
                                  .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        return objectMapper.readValue(responseContent, JwtAuthResponse.class);
    }

}
