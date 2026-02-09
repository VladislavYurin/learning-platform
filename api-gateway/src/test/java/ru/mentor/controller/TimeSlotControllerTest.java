package ru.mentor.controller;

import lombok.extern.slf4j.Slf4j;
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
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.dto.MentorTimeSlotInfoForUserDto;
import ru.mentor.services.JwtService;
import ru.mentor.services.RedirectCalendarService;
import ru.mentor.services.UserService;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Тест для проверки работы эндпоинта создания слота ментором.
 */
@Slf4j
@WebMvcTest(TimeSlotController.class)
@AutoConfigureMockMvc(addFilters = false)
class TimeSlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectCalendarService redirectCalendarService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Test
    void createSlot() throws Exception {

        Long timeSlotId = TestConstantHolder.timeSlotId;
        Long mentorId = TestConstantHolder.mentorId;
        String testRequestUUID = TestConstantHolder.requestId;
        String testLink = TestConstantHolder.meetingLink;
        String testDescription = TestConstantHolder.slotDescription;
        LocalDateTime startTime = TestConstantHolder.startTime;
        LocalDateTime endTime = TestConstantHolder.endTime;
        LocalDateTime createdAt = TestConstantHolder.createdAt;
        CalendarSlotType slotTypeGroup = CalendarSlotType.GROUP;
        CalendarSlotMeetingType slotMeetingTypeCommunication = CalendarSlotMeetingType.COMMUNICATION;
        int maxParticipants = TestConstantHolder.maxParticipants;
        boolean timeSlotIsActive = TestConstantHolder.isActiveFalse;

        MentorTimeSlotDto timeSlotDto = MentorTimeSlotDto.builder()
                .id(timeSlotId)
                .mentorId(mentorId)
                .requestId(testRequestUUID)
                .startTime(startTime)
                .endTime(endTime)
                .slotType(slotTypeGroup)
                .slotMeetingType(slotMeetingTypeCommunication)
                .maxParticipants(maxParticipants)
                .isActive(timeSlotIsActive)
                .meetingLink(testLink)
                .description(testDescription)
                .createdAt(createdAt)
                .build();

        Mockito.when(redirectCalendarService.createTimeSlot(ArgumentMatchers.any(
                        MentorTimeSlotCreateRequest.class)))
                .thenReturn(timeSlotDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/slot/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "startTime": "%s",
                                  "endTime": "%s",
                                  "slotType": "%s",
                                  "slotMeetingType": "%s",
                                  "maxParticipants": %s,
                                  "meetingLink": "%s",
                                  "description": "%s"
                                }
                                """.formatted(
                                startTime,
                                endTime,
                                slotTypeGroup,
                                slotMeetingTypeCommunication,
                                maxParticipants,
                                testLink,
                                testDescription
                        )))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(timeSlotId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mentorId").value(mentorId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId").value(testRequestUUID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.startTime")
                        .value(startTime.format(TestConstantHolder.formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.endTime")
                        .value(endTime.format(TestConstantHolder.formatter)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.slotType").value(slotTypeGroup.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.slotMeetingType")
                        .value(slotMeetingTypeCommunication.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maxParticipants")
                        .value(maxParticipants))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(timeSlotIsActive))
                .andExpect(MockMvcResultMatchers.jsonPath("$.meetingLink").value(testLink))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(testDescription))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt")
                        .value(createdAt.format(TestConstantHolder.formatter)));

    }

    @Test
    void getMentorSlotsInfoForUser_Success() throws Exception {

        MentorTimeSlotInfoForUserDto dto =
                TestEntityStubGenerator.constructMentorTimeSlotInfoForUserDto();

        Mockito.when(redirectCalendarService.getMentorSlotsInfoForUser(TestConstantHolder.mentorId))
                .thenReturn(List.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/slot")
                        .param(
                                TestConstantHolder.mentorIdRequestParameter,
                                String.valueOf(TestConstantHolder.mentorId)
                        )
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].slotFull").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.id")
                        .value(TestConstantHolder.timeSlotId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.mentorId")
                        .value(TestConstantHolder.mentorId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.slotType")
                        .value(TestConstantHolder.slotType.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.slotMeetingType")
                        .value(TestConstantHolder.slotMeetingType.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.isActive")
                        .value(TestConstantHolder.isActiveFalse));

        log.info("Тест отработал");
    }

}