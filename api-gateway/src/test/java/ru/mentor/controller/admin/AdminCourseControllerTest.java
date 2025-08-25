package ru.mentor.controller.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.PageSettings;
import ru.mentor.services.JwtService;
import ru.mentor.services.RedirectAdminCourseService;
import ru.mentor.services.UserService;

@WebMvcTest(value = AdminCourseController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectAdminCourseService redirectAdminCourseService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private final int pageNumber = 0;
    private final int pageSize = 10;
    private final int totalElements = 1;
    private long courseId = 1L;

    @Test
    void getAllCourses_success() throws Exception {
        // Given
        CourseDto dto = constructCourseDto();
        Mockito.when(redirectAdminCourseService.getAllCourses(any(PageSettings.class)))
               .thenReturn(new PageImpl<>(
                       List.of(dto),
                       PageRequest.of(pageNumber, pageSize),
                       totalElements
               ));

        // When / Then
        mockMvc.perform(get("/admin/course/all")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"page\":0,\"size\":10}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content").isArray())
               .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void getCourseById_success() throws Exception {
        // Given
        CourseDto dto = constructCourseDto();
        Mockito.when(redirectAdminCourseService.getCourseById(courseId)).thenReturn(dto);

        // When
        mockMvc.perform(get("/admin/course/{courseId}", courseId))

               // Then
               .andExpect(status().isOk());
    }

    private CourseDto constructCourseDto() {
        return CourseDto.builder().build();
    }

}