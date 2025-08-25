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
import ru.mentor.dto.ModuleDto;
import ru.mentor.services.JwtService;
import ru.mentor.services.RedirectAdminModuleService;
import ru.mentor.services.UserService;

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

    private final Long moduleId = 1L;

    @Test
    void getModuleById_success() throws Exception {
        // Given
        ModuleDto dto = constructModuleDto();
        Mockito.when(redirectAdminModuleService.getModuleById(anyLong())).thenReturn(dto);

        // When / Then
        mockMvc.perform(get("/admin/module/get-one")
                                .param("moduleId", moduleId.toString()))
               .andExpect(status().isOk());
    }

    private ModuleDto constructModuleDto() {
        return ModuleDto.builder().build();
    }

}