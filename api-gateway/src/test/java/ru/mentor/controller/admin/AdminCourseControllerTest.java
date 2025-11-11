package ru.mentor.controller.admin;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.mentor.gateway.model.CourseDto;
import ru.mentor.gateway.model.PageCourseDto;
import ru.mentor.mapper.CourseDtoMapper;
import ru.mentor.mapper.PageCourseDtoMapper;
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
    private PageCourseDtoMapper pageCourseDtoMapper;

    @MockBean
    private CourseDtoMapper courseDtoMapper;

    @MockBean
    private RedirectAdminCourseService redirectAdminCourseService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Test
    void getAllCourses_success() throws Exception {

        CourseDto dto = new CourseDto();
        dto.setId(1L);
        dto.setCourseTitle("Java Basics");
        dto.setIsActive(true);

        Page<CourseDto> page = new PageImpl<>(
                List.of(dto),
                PageRequest.of(TestConstantHolder.pageNumber, TestConstantHolder.pageSize),
                TestConstantHolder.totalElementsCount
        );

        PageCourseDto pageCourseDto = new PageCourseDto();
        pageCourseDto.setContent(List.of(dto));
        pageCourseDto.setTotalElements((long) TestConstantHolder.totalElementsCount);
        pageCourseDto.setTotalPages(TestConstantHolder.totalPagesCount);
        pageCourseDto.setNumber(TestConstantHolder.pageNumber);
        pageCourseDto.setSize(TestConstantHolder.pageSize);

        Mockito.when(redirectAdminCourseService.getAllCourses(
                ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())
        ).thenReturn(page);

        Mockito.when(pageCourseDtoMapper.toDto(page))
                .thenReturn(pageCourseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/course/all")
                        .param("pageNumber", String.valueOf(TestConstantHolder.pageNumber))
                        .param("pageSize", String.valueOf(TestConstantHolder.pageSize)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(TestConstantHolder.totalElementsCount));
    }

    @Test
    void getCourseById_success() throws Exception {
        ru.mentor.dto.CourseDto commonDto = TestEntityStubGenerator.constructCourseDto();

        CourseDto apiDto = courseDtoMapper.toApi(commonDto);

        Mockito.when(redirectAdminCourseService.getCourseById(TestConstantHolder.courseId))
                .thenReturn(apiDto);

        mockMvc.perform(MockMvcRequestBuilders.get(
                        "/admin/course/{courseId}",
                        TestConstantHolder.courseId
                ))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}