package ru.mentor.controller.admin;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.mentor.constant.Role;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.mapper.UserInfoDtoMapper;
import ru.mentor.services.JwtService;
import ru.mentor.services.UserInfoService;
import ru.mentor.services.UserService;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;

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

    @MockBean
    private UserInfoDtoMapper userInfoDtoMapper;

    @Test
    void getMyUserInfo_success() throws Exception {
        UserInfoDto dto = TestEntityStubGenerator.constructUserInfoDtoWithRole(Role.USER);
        Mockito.when(userInfoService.getMyUserInfo()).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/user/me"))

               .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getOtherUserInfo_success() throws Exception {
        UserInfoDto dto = TestEntityStubGenerator.constructUserInfoDtoWithRole(Role.USER);
        Mockito.when(userInfoService.getOtherUserInfo(ArgumentMatchers.anyLong())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/user/{userId}", TestConstantHolder.userId))

               .andExpect(MockMvcResultMatchers.status().isOk());
    }

}