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
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.gateway.model.PageMentorSlotInfoDto;
import ru.mentor.mapper.PageMentorSlotInfoDtoMapper;
import ru.mentor.services.JwtService;
import ru.mentor.services.RedirectAdminCalendarService;
import ru.mentor.services.UserService;
import ru.mentor.testUtil.TestConstantHolder;

@WebMvcTest(value = AdminMentorTimeSlotController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminMentorTimeSlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @MockBean
    private RedirectAdminCalendarService redirectAdminCalendarService;

    @MockBean
    private PageMentorSlotInfoDtoMapper pageMentorSlotInfoDtoMapper;

    @Test
    void getMentorSlotsInfo_success() throws Exception {

        MentorSlotInfoDto dto = new MentorSlotInfoDto();

        PageImpl<MentorSlotInfoDto> page = new PageImpl<>(
                List.of(dto),
                PageRequest.of(TestConstantHolder.pageNumber, TestConstantHolder.pageSize),
                TestConstantHolder.totalElementsCount
        );

        Mockito.when(redirectAdminCalendarService.getAllMentorTimeSlots(
                        ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()))
                .thenReturn(page);

        PageMentorSlotInfoDto api = new PageMentorSlotInfoDto();
        api.setContent(List.of(new ru.mentor.gateway.model.MentorSlotInfoDto()));
        api.setTotalElements((long) TestConstantHolder.totalElementsCount);
        api.setTotalPages(TestConstantHolder.totalPagesCount);
        api.setNumber(TestConstantHolder.pageNumber);
        api.setSize(TestConstantHolder.pageSize);

        Mockito.when(pageMentorSlotInfoDtoMapper.toApiDto(page)).thenReturn(api);

        mockMvc.perform(MockMvcRequestBuilders.get("/admin/slot/all")
                        .param("pageNumber", String.valueOf(TestConstantHolder.pageNumber))
                        .param("pageSize", String.valueOf(TestConstantHolder.pageSize))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1));
    }

}