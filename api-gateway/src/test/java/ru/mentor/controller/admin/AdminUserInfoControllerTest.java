package ru.mentor.controller.admin;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.services.JwtService;
import ru.mentor.services.UserInfoService;
import ru.mentor.services.UserService;

@WebMvcTest(AdminUserInfoController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserInfoService userInfoService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private final long userId = 1L;

    @Test
    void getMyUserInfo_success() throws Exception {
        // Given
        UserInfoDto dto = constructUserInfoDto();
        Mockito.when(userInfoService.getMyUserInfo()).thenReturn(dto);

        // When
        mockMvc.perform(get("/admin/user/me"))

               // Then
               .andExpect(status().isOk());
    }

    @Test
    void getOtherUserInfo_success() throws Exception {
        // Given
        UserInfoDto dto = constructUserInfoDto();
        Mockito.when(userInfoService.getOtherUserInfo(anyLong())).thenReturn(dto);

        // When
        mockMvc.perform(get("/admin/user/{userId}", userId))

               // Then
               .andExpect(status().isOk());
    }

    private UserInfoDto constructUserInfoDto() {
        return UserInfoDto.builder().build();
    }

}