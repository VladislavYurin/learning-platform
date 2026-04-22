package ru.mentor.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.mentor.dto.front.CourseAccessRequest;
import ru.mentor.dto.front.ModuleAccessRequest;
import ru.mentor.security.JwtAuthenticationFilter;
import ru.mentor.services.RedirectAccessService;

@WebMvcTest(AccessController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectAccessService redirectAccessService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void giveCourseAccess_delegatesToRedirectService_returnsOk() throws Exception {
        Mockito.when(redirectAccessService.giveCourseAccess(ArgumentMatchers.any(CourseAccessRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(MockMvcRequestBuilders.post("/access/course/get-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 2,
                                  "courseId": 10
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(redirectAccessService).giveCourseAccess(ArgumentMatchers.any(CourseAccessRequest.class));
    }

    @Test
    void revokeCourseAccess_delegatesToRedirectService_returnsOk() throws Exception {
        Mockito.when(redirectAccessService.revokeCourseAccess(ArgumentMatchers.any(CourseAccessRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(MockMvcRequestBuilders.post("/access/course/delete-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 2,
                                  "courseId": 10
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(redirectAccessService).revokeCourseAccess(ArgumentMatchers.any(CourseAccessRequest.class));
    }

    @Test
    void giveModuleAccess_delegatesToRedirectService_returnsOk() throws Exception {
        Mockito.when(redirectAccessService.giveModuleAccess(ArgumentMatchers.any(ModuleAccessRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(MockMvcRequestBuilders.post("/access/module/get-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 2,
                                  "courseId": 10,
                                  "moduleId": 20
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(redirectAccessService).giveModuleAccess(ArgumentMatchers.any(ModuleAccessRequest.class));
    }

    @Test
    void revokeModuleAccess_delegatesToRedirectService_returnsOk() throws Exception {
        Mockito.when(redirectAccessService.revokeModuleAccess(ArgumentMatchers.any(ModuleAccessRequest.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(MockMvcRequestBuilders.post("/access/module/delete-access")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 2,
                                  "courseId": 10,
                                  "moduleId": 20
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(redirectAccessService).revokeModuleAccess(ArgumentMatchers.any(ModuleAccessRequest.class));
    }
}
