package ru.mentor.controller.admin;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.mentor.dto.CourseDto;
import ru.mentor.services.JwtService;
import ru.mentor.services.RedirectAdminCourseService;
import ru.mentor.services.UserService;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;

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

    @Test
    void getAllCourses_success() throws Exception {
        CourseDto dto = TestEntityStubGenerator.constructCourseDto();
        Mockito.when(redirectAdminCourseService.getAllCourses(
                       ArgumentMatchers.anyInt(),
                       ArgumentMatchers.anyInt()
               ))
               .thenReturn(new PageImpl<>(
                       List.of(dto),
                       PageRequest.of(TestConstantHolder.pageNumber, TestConstantHolder.pageSize),
                       TestConstantHolder.totalElementsCount
               ));

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/course/all")
                                              .param(
                                                      "pageNumber",
                                                      String.valueOf(TestConstantHolder.pageNumber)
                                              )
                                              .param(
                                                      "pageSize",
                                                      String.valueOf(TestConstantHolder.pageSize)
                                              )
                                              .contentType(MediaType.APPLICATION_JSON))

               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
               .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1));
    }

    @Test
    void getCourseById_success() throws Exception {
        CourseDto dto = TestEntityStubGenerator.constructCourseDto();
        Mockito.when(redirectAdminCourseService.getCourseById(TestConstantHolder.courseId))
               .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get(
                       "/admin/course/{courseId}",
                       TestConstantHolder.courseId
               ))
               .andExpect(MockMvcResultMatchers.status().isOk());
    }

}