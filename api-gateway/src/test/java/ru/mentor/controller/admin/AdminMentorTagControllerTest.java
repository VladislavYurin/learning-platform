package ru.mentor.controller.admin;

import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.mentor.testUtil.TestConstantHolder.mentorTagId;
import static ru.mentor.testUtil.TestConstantHolder.mentorTagNameDirection;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructListMentorTagDto;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagAttachResponseDto;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagDetachRequestDto;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagDetachResponseDto;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagDto;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagDtoCreateRequest;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagsAttachRequestDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.mentor.dto.mentorTag.MentorTagDetachRequestDto;
import ru.mentor.dto.mentorTag.MentorTagDtoCreateRequest;
import ru.mentor.dto.mentorTag.MentorTagsAttachRequestDto;
import ru.mentor.services.JwtService;
import ru.mentor.services.RedirectAdminMentorTagService;
import ru.mentor.services.UserService;

@WebMvcTest(AdminMentorTagController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminMentorTagControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private RedirectAdminMentorTagService adminService;

    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void getAllMentorTags_success() {
        Mockito.when(adminService.allMentorTags())
               .thenReturn(constructListMentorTagDto());

        mockMvc.perform(MockMvcRequestBuilders.get(
                "/admin/mentor-tags/all"
        )).andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void createCustomMentorTag_success() {
        MentorTagDtoCreateRequest request = constructMentorTagDtoCreateRequest();

        Mockito.when(adminService.createCustomMentorTag(Mockito.any(MentorTagDtoCreateRequest.class)))
               .thenReturn(constructMentorTagDto());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/mentor-tags/add")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.tagName").value(mentorTagNameDirection));
    }

    @Test
    @SneakyThrows
    void attachMentorTags_success() {
        MentorTagsAttachRequestDto request = constructMentorTagsAttachRequestDto();

        Mockito.when(adminService.attachMentorTags(Mockito.any(MentorTagsAttachRequestDto.class)))
               .thenReturn(constructMentorTagAttachResponseDto());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/mentor-tags/attach")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.tagsIds", contains(1, 2, 3, 4)));
    }

    @Test
    @SneakyThrows
    void detachMentorTags_success() {
        MentorTagDetachRequestDto request = constructMentorTagDetachRequestDto();

        Mockito.when(adminService.detachMentorTag(Mockito.any(MentorTagDetachRequestDto.class)))
               .thenReturn(constructMentorTagDetachResponseDto());

        mockMvc.perform(MockMvcRequestBuilders.post("/admin/mentor-tags/detach")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.tagIds").value(mentorTagId));
    }

}
