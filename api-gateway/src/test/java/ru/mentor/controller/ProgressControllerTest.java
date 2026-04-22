package ru.mentor.controller;

import java.util.List;
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
import ru.mentor.dto.CourseProgressResponse;
import ru.mentor.dto.MenteeProgressDto;
import ru.mentor.security.JwtAuthenticationFilter;
import ru.mentor.services.RedirectProgressService;

@WebMvcTest(ProgressController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProgressControllerTest {

    private static final Long COURSE_ID = 10L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectProgressService redirectProgressService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getCourseProgressByMentor_delegatesToRedirectService_returnsOkAndBody() throws Exception {
        CourseProgressResponse body = CourseProgressResponse.builder()
                .courseId(COURSE_ID)
                .courseTitle("Test course")
                .mentee(List.of())
                .statistic(null)
                .build();

        Mockito.when(redirectProgressService.getCourseProgressByMentor(ArgumentMatchers.eq(COURSE_ID)))
                .thenReturn(body);

        mockMvc.perform(MockMvcRequestBuilders.get("/progress/course/{courseId}/statistics", COURSE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.courseId").value(COURSE_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.courseTitle").value("Test course"));

        Mockito.verify(redirectProgressService).getCourseProgressByMentor(COURSE_ID);
    }

    @Test
    void getAllUsersAtCourse_delegatesToRedirectService_returnsOkAndList() throws Exception {
        MenteeProgressDto mentee = MenteeProgressDto.builder()
                .userId(2L)
                .firstName("Ivan")
                .lastName("Ivanov")
                .currentModuleId(3L)
                .tgNickname("@ivan")
                .build();

        Mockito.when(redirectProgressService.getAllUsersAtCourse(ArgumentMatchers.eq(COURSE_ID)))
                .thenReturn(List.of(mentee));

        mockMvc.perform(MockMvcRequestBuilders.get("/progress/course/{courseId}/users", COURSE_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Ivan"));

        Mockito.verify(redirectProgressService).getAllUsersAtCourse(COURSE_ID);
    }
}
