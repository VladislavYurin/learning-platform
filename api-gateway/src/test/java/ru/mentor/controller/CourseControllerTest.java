package ru.mentor.controller;

import org.junit.jupiter.api.Test;
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
import ru.mentor.dto.CourseDtoWithoutModules;
import ru.mentor.security.JwtAuthenticationFilter;
import ru.mentor.services.impl.RedirectCourseServiceImpl;
import ru.mentor.testUtil.TestEntityStubGenerator;

import java.time.temporal.ChronoUnit;
import java.util.List;


@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RedirectCourseServiceImpl redirectCourseService;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getAllActiveCoursesPreview_success_returnsStatusOkAndListOfCourseDto() throws Exception {
        CourseDtoWithoutModules courseDto = TestEntityStubGenerator.constructCourseDtoWithoutModules();
        courseDto.setAuthor(TestEntityStubGenerator.constructUserInfoDtoWithRole(Role.MENTOR));

        Mockito.when(redirectCourseService.getAllActiveCoursesPreview())
                .thenReturn(List.of(courseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/course/all/active/preview")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].id").value(courseDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].courseTitle").value(courseDto.getCourseTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].courseDescription").value(courseDto.getCourseDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].isActive").value(courseDto.getIsActive()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].createdAt").value(courseDto.getCreatedAt().truncatedTo(ChronoUnit.MILLIS).toString()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].author.id").value(courseDto.getAuthor().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].author.username").value(courseDto.getAuthor().getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].author.role").value(courseDto.getAuthor().getRole().name()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].author.firstName").value(courseDto.getAuthor().getFirstName()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].author.lastName").value(courseDto.getAuthor().getLastName()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].author.tgNickname").value(courseDto.getAuthor().getTgNickname()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].author.tgChatId").value(courseDto.getAuthor().getTgChatId().doubleValue()))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$[0].tags").exists());

        Mockito.verify(redirectCourseService).getAllActiveCoursesPreview();
        Mockito.verifyNoMoreInteractions(redirectCourseService);
    }

    @Test
    void getAllActiveCoursesPreview_redirectServiceReturnsNull_returnsStatusIsOk200WithNullBody() throws Exception {
        Mockito.when(redirectCourseService.getAllActiveCoursesPreview())
                .thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/course/all/active/preview")
                        .accept(MediaType.APPLICATION_JSON))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.content().string(""));
    }
}