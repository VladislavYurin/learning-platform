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
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.dto.PageSettings;
import ru.mentor.services.JwtService;
import ru.mentor.services.RedirectAdminCalendarService;
import ru.mentor.services.UserService;

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

    private final int pageNumber = 0;
    private final int pageSize = 10;
    private final int totalElements = 1;

    @Test
    void getMentorSlotsInfo_success() throws Exception {

        // Given
        MentorSlotInfoDto dto = new MentorSlotInfoDto();
        Mockito.when(redirectAdminCalendarService.getAllMentorTimeSlots(any(PageSettings.class)))
               .thenReturn(new PageImpl<>(
                       List.of(dto),
                       PageRequest.of(pageNumber, pageSize),
                       totalElements
               ));

        // When
        mockMvc.perform(get("/admin/slot/all")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"page\":0,\"size\":10}"))

               // Then
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content").isArray())
               .andExpect(jsonPath("$.content.length()").value(1));
    }

}