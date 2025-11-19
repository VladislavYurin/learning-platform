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
import ru.mentor.gateway.model.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.dto.MentorTimeSlotInfoForUserDto;
import ru.mentor.mapper.MentorSlotInfoDtoMapper;
import ru.mentor.mapper.MentorTimeSlotDtoMapper;
import ru.mentor.mapper.MentorTimeSlotInfoForUserDtoMapper;
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

    @MockBean
    private MentorTimeSlotDtoMapper mentorTimeSlotDtoMapper;

    @MockBean
    private MentorSlotInfoDtoMapper mentorSlotInfoDtoMapper;

    @MockBean
    private MentorTimeSlotInfoForUserDtoMapper mentorTimeSlotInfoForUserDtoMapper;

//    @Test
//    void createSlot() throws Exception {
//
//        String testRequestUUID = "6e8f4e02-c91c-465f-b22d-7f102fca381b";
//        String testLink = "testLink";
//        String testDescription = "testDescription";
//        String startTime = "2025-01-15T13:00:00";
//        String endTime = "2025-01-15T14:00:00";
//        String slotTypeGroup = "GROUP";
//        String slotMeetingTypeCommunication = "COMMUNICATION";
//        int maxParticipants = 10;
//        boolean timeSlotIsActive = true;
//
//        MentorTimeSlotDto timeSlotDto = MentorTimeSlotDto.builder()
//                                                         .id(1L)
//                                                         .mentorId(1L)
//                                                         .rqUId(testRequestUUID)
//                                                         .startTime(LocalDateTime.parse(startTime))
//                                                         .endTime(LocalDateTime.parse(endTime))
//                                                         .slotType(CalendarSlotType.GROUP)
//                                                         .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
//                                                         .maxParticipants(maxParticipants)
//                                                         .isActive(timeSlotIsActive)
//                                                         .meetingLink(testLink)
//                                                         .description(testDescription)
//                                                         .createdAt(LocalDateTime.parse(startTime))
//                                                         .build();
//
//        Mockito.when(redirectCalendarService.createTimeSlot(ArgumentMatchers.any(
//                       MentorTimeSlotCreateRequest.class)))
//               .thenReturn(timeSlotDto);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/slot/create")
//                                              .contentType(MediaType.APPLICATION_JSON)
//                                              .content("""
//                                                               {
//                                                                 "startTime": "%s",
//                                                                 "endTime": "%s",
//                                                                 "slotType": "%s",
//                                                                 "slotMeetingType": "%s",
//                                                                 "maxParticipants": %s,
//                                                                 "meetingLink": "%s",
//                                                                 "description": "%s"
//                                                               }
//                                                               """.formatted(
//                                                      startTime,
//                                                      endTime,
//                                                      slotTypeGroup,
//                                                      slotMeetingTypeCommunication,
//                                                      maxParticipants,
//                                                      testLink,
//                                                      testDescription
//                                              )))
//
//               .andExpect(MockMvcResultMatchers.status().isOk())
//               .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
//               .andExpect(MockMvcResultMatchers.jsonPath("$.mentorId").exists())
//               .andExpect(MockMvcResultMatchers.jsonPath("$.rqUId").exists())
//               .andExpect(MockMvcResultMatchers.jsonPath("$.startTime").value(startTime))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.endTime").value(endTime))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.slotType").value(slotTypeGroup))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.slotMeetingType")
//                                               .value(slotMeetingTypeCommunication))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.maxParticipants")
//                                               .value(maxParticipants))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(timeSlotIsActive))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.meetingLink").value(testLink))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(testDescription))
//               .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists());
//
//    }

    @Test
    void createSlot() throws Exception {

        String testRequestUUID = "6e8f4e02-c91c-465f-b22d-7f102fca381b";
        String testLink = "testLink";
        String testDescription = "testDescription";
        String startTime = "2025-01-15T13:00:00";
        String endTime   = "2025-01-15T14:00:00";
        int maxParticipants = 10;
        boolean timeSlotIsActive = true;

        MentorTimeSlotDto domain = MentorTimeSlotDto.builder()
                .id(1L).mentorId(1L).rqUId(testRequestUUID)
                .startTime(LocalDateTime.parse(startTime))
                .endTime(LocalDateTime.parse(endTime))
                .slotType(CalendarSlotType.GROUP)
                .slotMeetingType(CalendarSlotMeetingType.COMMUNICATION)
                .maxParticipants(maxParticipants)
                .isActive(timeSlotIsActive)
                .meetingLink(testLink)
                .description(testDescription)
                .createdAt(LocalDateTime.parse(startTime))
                .build();

        Mockito.when(redirectCalendarService.createTimeSlot(ArgumentMatchers.any(MentorTimeSlotCreateRequest.class)))
                .thenReturn(domain);

        ru.mentor.gateway.model.MentorTimeSlotDto gateway =
                new ru.mentor.gateway.model.MentorTimeSlotDto()
                        .id(1L)
                        .mentorId(1L)
                        .rqUId(testRequestUUID)
                        .startTime(LocalDateTime.parse(startTime))
                        .endTime(LocalDateTime.parse(endTime))
                        .slotType(ru.mentor.gateway.model.MentorTimeSlotDto.SlotTypeEnum.GROUP)
                        .slotMeetingType(ru.mentor.gateway.model.MentorTimeSlotDto.SlotMeetingTypeEnum.COMMUNICATION)
                        .maxParticipants(maxParticipants)
                        .isActive(timeSlotIsActive)
                        .meetingLink(testLink)
                        .description(testDescription)
                        .createdAt(LocalDateTime.parse(startTime));

        Mockito.when(mentorTimeSlotDtoMapper.toApiDto(domain)).thenReturn(gateway);

        mockMvc.perform(MockMvcRequestBuilders.post("/slot/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "startTime": "%s",
                          "endTime": "%s",
                          "slotType": "GROUP",
                          "slotMeetingType": "COMMUNICATION",
                          "maxParticipants": %s,
                          "meetingLink": "%s",
                          "description": "%s"
                        }
                        """.formatted(startTime, endTime, maxParticipants, testLink, testDescription)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mentorId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rqUId").value(testRequestUUID))
                .andExpect(MockMvcResultMatchers.jsonPath("$.startTime").value(startTime))
                .andExpect(MockMvcResultMatchers.jsonPath("$.endTime").value(endTime))
                .andExpect(MockMvcResultMatchers.jsonPath("$.slotType").value("GROUP"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.slotMeetingType").value("COMMUNICATION"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.maxParticipants").value(maxParticipants))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(timeSlotIsActive))
                .andExpect(MockMvcResultMatchers.jsonPath("$.meetingLink").value(testLink))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(testDescription))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists());

    }

//    @Test
//    void getMentorSlotsInfoForUser_Success() throws Exception {
//
//        MentorTimeSlotInfoForUserDto dto =
//                TestEntityStubGenerator.constructMentorTimeSlotInfoForUserDto();
//
//        Mockito.when(redirectCalendarService.getMentorSlotsInfoForUser(TestConstantHolder.mentorId))
//                .thenReturn(List.of(dto));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/slot")
//                        .param("mentorId", String.valueOf(TestConstantHolder.mentorId))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].slotFull").value(false))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.id").value(TestConstantHolder.timeSlotId))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.mentorId").value(TestConstantHolder.mentorId))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.slotType").value(TestConstantHolder.slotType.toString()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.slotMeetingType").value(TestConstantHolder.slotMeetingType.toString()))
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.isActive").value(false));
//
//        log.info("Тест отработал");
//    }


    @Test
    void getMentorSlotsInfoForUser_Success() throws Exception {

        LocalDateTime start = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);
        LocalDateTime end   = start.plusHours(1);

        MentorTimeSlotDto domainSlot = MentorTimeSlotDto.builder()
                .id(TestConstantHolder.timeSlotId)
                .mentorId(TestConstantHolder.mentorId)
                .startTime(start)
                .endTime(end)
                .slotType(TestConstantHolder.slotType)                 // например, GROUP
                .slotMeetingType(TestConstantHolder.slotMeetingType)   // например, COMMUNICATION
                .isActive(false)
                .build();

        MentorTimeSlotInfoForUserDto domainInfo = MentorTimeSlotInfoForUserDto.builder()
//                .slotFull(false)
                .mentorTimeSlotDto(domainSlot)
                .build();

        // 2) Стаб сервиса по anyLong(), чтобы не промахнуться аргументом
        Mockito.when(redirectCalendarService.getMentorSlotsInfoForUser(Mockito.anyLong()))
                .thenReturn(List.of(domainInfo));

        // 3) Готовим GATEWAY-модель (то, что сериализует контроллер наружу)
        ru.mentor.gateway.model.MentorTimeSlotDto gatewaySlot =
                new ru.mentor.gateway.model.MentorTimeSlotDto()
                        .id(TestConstantHolder.timeSlotId)
                        .mentorId(TestConstantHolder.mentorId)
                        .startTime(start)
                        .endTime(end)
                        .slotType(ru.mentor.gateway.model.MentorTimeSlotDto.SlotTypeEnum
                                .valueOf(TestConstantHolder.slotType.toString()))
                        .slotMeetingType(ru.mentor.gateway.model.MentorTimeSlotDto.SlotMeetingTypeEnum
                                .valueOf(TestConstantHolder.slotMeetingType.toString()))
                        .isActive(false);

        ru.mentor.gateway.model.MentorTimeSlotInfoForUserDto gatewayInfo =
                new ru.mentor.gateway.model.MentorTimeSlotInfoForUserDto()
                        .slotFull(false)
                        .mentorTimeSlotDto(gatewaySlot);

        // 4) Замокать и одиночный, и пакетный маппинг (на случай, что контроллер вызывает toModels)
        Mockito.when(mentorTimeSlotInfoForUserDtoMapper.toApiDto(Mockito.any(MentorTimeSlotInfoForUserDto.class)))
                .thenReturn(gatewayInfo);
        Mockito.when(mentorTimeSlotInfoForUserDtoMapper.toListApiDto(Mockito.anyList()))
                .thenReturn(List.of(gatewayInfo));

        mockMvc.perform(MockMvcRequestBuilders.get("/slot")
                        .param("mentorId", String.valueOf(TestConstantHolder.mentorId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].slotFull").value(false))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.id").value(TestConstantHolder.timeSlotId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.mentorId").value(TestConstantHolder.mentorId))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.slotType").value(TestConstantHolder.slotType.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.slotMeetingType").value(TestConstantHolder.slotMeetingType.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].mentorTimeSlotDto.isActive").value(false));
    }
}