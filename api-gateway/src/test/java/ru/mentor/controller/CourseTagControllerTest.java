package ru.mentor.controller;

import java.time.LocalDateTime;
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
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.dto.tag.CreateCourseTagRequest;
import ru.mentor.security.JwtAuthenticationFilter;
import ru.mentor.services.RedirectCourseTagService;

@WebMvcTest(CourseTagController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseTagControllerTest {

    private static final Long TAG_ID = 7L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectCourseTagService redirectCourseTagService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void createTag_delegatesToRedirectService_returnsOkAndBody() throws Exception {
        CourseTagDto created = CourseTagDto.builder()
                .id(TAG_ID)
                .tagName("backend")
                .createdAt(LocalDateTime.of(2026, 4, 1, 12, 0))
                .isActive(true)
                .build();

        Mockito.when(redirectCourseTagService.createCourseTag(ArgumentMatchers.any(CreateCourseTagRequest.class)))
                .thenReturn(created);

        mockMvc.perform(MockMvcRequestBuilders.post("/course-tag/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "tagName": "backend"
                                }
                                """))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(TAG_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tagName").value("backend"));

        Mockito.verify(redirectCourseTagService).createCourseTag(ArgumentMatchers.any(CreateCourseTagRequest.class));
    }

    @Test
    void deleteTag_delegatesToRedirectService_returnsNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/course-tag/{tagId}", TAG_ID))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        Mockito.verify(redirectCourseTagService).deleteCourseTag(TAG_ID);
    }

    @Test
    void getTagById_delegatesToRedirectService_returnsOkAndBody() throws Exception {
        CourseTagDto dto = CourseTagDto.builder()
                .id(TAG_ID)
                .tagName("frontend")
                .createdAt(LocalDateTime.of(2026, 3, 15, 10, 30))
                .isActive(true)
                .build();

        Mockito.when(redirectCourseTagService.getTagById(ArgumentMatchers.eq(TAG_ID)))
                .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/course-tag/{tagId}", TAG_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(TAG_ID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tagName").value("frontend"));

        Mockito.verify(redirectCourseTagService).getTagById(TAG_ID);
    }

    @Test
    void getAllTags_delegatesToRedirectService_returnsOkAndList() throws Exception {
        CourseTagDto first = CourseTagDto.builder()
                .id(1L)
                .tagName("java")
                .createdAt(LocalDateTime.of(2026, 1, 1, 0, 0))
                .isActive(true)
                .build();

        Mockito.when(redirectCourseTagService.getAllTags()).thenReturn(List.of(first));

        mockMvc.perform(MockMvcRequestBuilders.get("/course-tag/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].tagName").value("java"));

        Mockito.verify(redirectCourseTagService).getAllTags();
    }
}
