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
import ru.mentor.dto.ModuleDto;
import ru.mentor.services.JwtService;
import ru.mentor.services.RedirectAdminModuleService;
import ru.mentor.services.UserService;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;

@WebMvcTest(value = AdminModuleController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminModuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectAdminModuleService redirectAdminModuleService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Test
    void getModuleById_success() throws Exception {
        ModuleDto dto = TestEntityStubGenerator.constructModuleDto();
        Mockito.when(redirectAdminModuleService.getModuleById(ArgumentMatchers.anyLong()))
               .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/module/get-one")
                                              .param(
                                                      "moduleId",
                                                      String.valueOf(TestConstantHolder.moduleId)
                                              ))
               .andExpect(MockMvcResultMatchers.status().isOk());
    }

}