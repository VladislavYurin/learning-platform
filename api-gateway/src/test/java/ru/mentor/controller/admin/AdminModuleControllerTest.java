package ru.mentor.controller.admin;

import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    public static final String PAGE_NUMBER_REQUEST_PARAMETER = "pageNumber";
    public static final String PAGE_SIZE_REQUEST_PARAMETER = "pageSize";
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectAdminModuleService redirectAdminModuleService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void getModuleById_success() {
        ModuleDto dto = TestEntityStubGenerator.constructModuleDto();

        Mockito.when(redirectAdminModuleService.getModuleById(ArgumentMatchers.anyLong()))
               .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get(
                       "/admin/module/{moduleId}",
                       TestConstantHolder.moduleId
               ))
               .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @SneakyThrows
    void getAllModules_success() {
        ModuleDto moduleDto = TestEntityStubGenerator.constructModuleDto();

        Mockito.when(redirectAdminModuleService.getAllModules(
                       ArgumentMatchers.anyInt(),
                       ArgumentMatchers.anyInt()
               ))
               .thenReturn(new PageImpl<>(
                       List.of(moduleDto),
                       PageRequest.of(0, 10),
                       TestConstantHolder.totalElementsCount
               ));

        mockMvc.perform(MockMvcRequestBuilders.get(
                                                      "/admin/module/all"
                                              )
                                              .param(
                                                      PAGE_NUMBER_REQUEST_PARAMETER,
                                                      String.valueOf(TestConstantHolder.pageNumber)
                                              )
                                              .param(
                                                      PAGE_SIZE_REQUEST_PARAMETER,
                                                      String.valueOf(TestConstantHolder.pageSize)
                                              ))
               .andExpect(MockMvcResultMatchers.status().isOk());
    }

}