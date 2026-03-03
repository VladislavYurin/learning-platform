package ru.mentor.controller;

import java.time.LocalDateTime;
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
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;
import ru.mentor.security.JwtAuthenticationFilter;
import ru.mentor.services.RedirectModuleService;

@WebMvcTest(ModuleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ModuleControllerTest {

    private static final Long COURSE_ID = 10L;
    private static final Long MODULE_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectModuleService redirectModuleService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void createModule_delegatesToRedirectService_returnsOkAndBody() throws Exception {
        ModuleDto created = ModuleDto.builder()
                .id(100L)
                .moduleTitle("Intro")
                .moduleOrderNumber(1)
                .moduleContent("# Hello")
                .isActive(true)
                .createdAt(LocalDateTime.of(2026, 1, 1, 12, 0))
                .build();

        Mockito.when(redirectModuleService.createModule(ArgumentMatchers.any(CreateModuleRequest.class)))
                .thenReturn(created);

        mockMvc.perform(MockMvcRequestBuilders.post("/module/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseId": 10,
                                  "moduleTitle": "Intro",
                                  "moduleOrderNumber": 1,
                                  "moduleContentDescription": "# Hello"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.moduleTitle").value("Intro"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.moduleOrderNumber").value(1));

        Mockito.verify(redirectModuleService).createModule(ArgumentMatchers.any(CreateModuleRequest.class));
    }

    @Test
    void deleteModule_delegatesToRedirectService_returnsNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/module/{courseId}/{moduleId}", COURSE_ID, MODULE_ID))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(redirectModuleService).deleteModule(COURSE_ID, MODULE_ID);
    }

    @Test
    void getModuleById_delegatesToRedirectService_returnsOkAndBody() throws Exception {
        ModuleDto dto = ModuleDto.builder()
                .id(50L)
                .moduleTitle("Basics")
                .moduleOrderNumber(1)
                .moduleContent("text")
                .isActive(true)
                .createdAt(LocalDateTime.of(2026, 2, 1, 10, 0))
                .build();

        Mockito.when(redirectModuleService.getModuleById(
                        ArgumentMatchers.eq(COURSE_ID),
                        ArgumentMatchers.eq(MODULE_ID)))
                .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/module/{courseId}/{moduleId}", COURSE_ID, MODULE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(50))
                .andExpect(MockMvcResultMatchers.jsonPath("$.moduleTitle").value("Basics"));

        Mockito.verify(redirectModuleService).getModuleById(COURSE_ID, MODULE_ID);
    }
}
