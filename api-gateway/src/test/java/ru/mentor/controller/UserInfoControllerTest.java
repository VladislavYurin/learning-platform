package ru.mentor.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.mentor.constant.Role;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.security.JwtAuthenticationFilter;
import ru.mentor.services.UserAvatarService;
import ru.mentor.services.UserInfoService;
import ru.mentor.services.UserService;
import ru.mentor.testUtil.TestEntityStubGenerator;
import java.util.Collections;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(UserInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserInfoService userInfoService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserService userService;

    @MockBean
    private UserAvatarService userAvatarService;

    @Test
    void updateMyUserInfo() throws Exception {
        UserInfoDto updateDto = TestEntityStubGenerator.constructUserInfoDtoWithRole(Role.USER);
        String jsonResponse = TestEntityStubGenerator.constructUserJsonWithRole(Role.USER);

        when(userInfoService.updateMyUserInfo(ArgumentMatchers.any(UserInfoDto.class)))
               .thenReturn(updateDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/user/me")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(jsonResponse))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(updateDto.getId()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.username")
                                               .value(updateDto.getUsername()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.role")
                                               .value(updateDto.getRole().name()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.firstName")
                                               .value(updateDto.getFirstName()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.lastName")
                                               .value(updateDto.getLastName()))
               .andExpect(MockMvcResultMatchers.jsonPath("$.tgChatId")
                                               .value(updateDto.getTgChatId()));
    }
    // ================== Тесты для searchUsers ==================

    @Test
    void searchUsers_byEmail_shouldReturnUser() throws Exception {
        UserInfoDto user = UserInfoDto.builder()
                .username("mentor@gmail.com")
                .firstName("Владислав")
                .lastName("Юрин")
                .tgNickname("@vladislavyurin")
                .build();
        List<UserInfoDto> mockResult = List.of(user);

        when(userInfoService.searchUsers("mentor@gmail.com")).thenReturn(mockResult);

        mockMvc.perform(get("/user/search")
                        .param("query", "mentor@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("mentor@gmail.com"));
    }

    @Test
    void searchUsers_byTelegram_shouldReturnUser() throws Exception {
        UserInfoDto user = UserInfoDto.builder()
                .username("mentor@gmail.com")
                .tgNickname("@vladislavyurin")
                .build();
        List<UserInfoDto> mockResult = List.of(user);

        when(userInfoService.searchUsers("@vladislavyurin")).thenReturn(mockResult);

        mockMvc.perform(get("/user/search")
                        .param("query", "@vladislavyurin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tgNickname").value("@vladislavyurin"))
                .andExpect(jsonPath("$[0].username").value("mentor@gmail.com"));
    }

    @Test
    void searchUsers_byName_shouldReturnUser() throws Exception {
        UserInfoDto user = UserInfoDto.builder()
                .firstName("Владислав")
                .lastName("Юрин")
                .build();
        List<UserInfoDto> mockResult = List.of(user);

        when(userInfoService.searchUsers("Владислав")).thenReturn(mockResult);

        mockMvc.perform(get("/user/search")
                        .param("query", "Владислав"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Владислав"))
                .andExpect(jsonPath("$[0].lastName").value("Юрин"));
    }

    @Test
    void searchUsers_byLastName_shouldReturnUser() throws Exception {
        UserInfoDto user = UserInfoDto.builder()
                .lastName("Юрин")
                .build();
        List<UserInfoDto> mockResult = List.of(user);

        when(userInfoService.searchUsers("Юрин")).thenReturn(mockResult);

        mockMvc.perform(get("/user/search")
                        .param("query", "Юрин"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Юрин"));
    }

    @Test
    void searchUsers_emptyQuery_shouldReturnEmptyList() throws Exception {
        when(userInfoService.searchUsers("")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/search")
                        .param("query", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchUsers_noQueryParam_shouldReturnEmptyList() throws Exception {
        when(userInfoService.searchUsers(null)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchUsers_notFound_shouldReturnEmptyList() throws Exception {
        when(userInfoService.searchUsers("nonexistent@example.com")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/search")
                        .param("query", "nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchUsers_partialMatch_shouldReturnMultipleUsers() throws Exception {
        List<UserInfoDto> mockResult = List.of(
                UserInfoDto.builder().username("test1@test.com").build(),
                UserInfoDto.builder().username("test2@test.com").build()
        );
        when(userInfoService.searchUsers("test")).thenReturn(mockResult);

        mockMvc.perform(get("/user/search")
                        .param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void searchUsers_caseInsensitive_shouldWork() throws Exception {
        UserInfoDto user = UserInfoDto.builder()
                .username("mentor@gmail.com")
                .build();
        List<UserInfoDto> mockResult = List.of(user);

        when(userInfoService.searchUsers("MENTOR@GMAIL.COM")).thenReturn(mockResult);

        mockMvc.perform(get("/user/search")
                        .param("query", "MENTOR@GMAIL.COM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("mentor@gmail.com"));
    }

}