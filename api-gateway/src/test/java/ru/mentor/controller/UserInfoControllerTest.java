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
import ru.mentor.services.RedirectMentorTagService;
import ru.mentor.services.UserAvatarService;
import ru.mentor.services.UserInfoService;
import ru.mentor.services.UserService;
import ru.mentor.testUtil.TestEntityStubGenerator;

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

    @MockBean
    private RedirectMentorTagService mentorTagService;

    @Test
    void updateMyUserInfo() throws Exception {
        UserInfoDto updateDto = TestEntityStubGenerator.constructUserInfoDtoWithRole(Role.USER);
        String jsonResponse = TestEntityStubGenerator.constructUserJsonWithRole(Role.USER);

        Mockito.when(userInfoService.updateMyUserInfo(ArgumentMatchers.any(UserInfoDto.class)))
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

}