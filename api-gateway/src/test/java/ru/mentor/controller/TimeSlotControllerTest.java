package ru.mentor.controller;

import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.services.RedirectCalendarService;

/**
 * Тест для проверки работы эндпоинта создания слота ментором.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TimeSlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectCalendarService redirectCalendarService;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
                                                     .withDatabaseName("testdb")
                                                     .withUsername("test")
                                                     .withPassword("test");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.liquibase.enabled", () -> "false");
    }

    @Test
    @WithMockUser(username = "test", roles = {"MENTOR"})
    @Sql(scripts = "/init_test_user.sql")
    void createSlot() throws Exception {

        String testRequestUUID = "6e8f4e02-c91c-465f-b22d-7f102fca381b";
        String testLink = "testLink";
        String testDescription = "testDescription";
        String startTime = "2025-01-15T13:00:00";
        String endTime = "2025-01-15T14:00:00";
        String slotTypeGroup = "GROUP";
        String slotMeetingTypeCommunication = "COMMUNICATION";
        int maxParticipants = 10;
        boolean timeSlotIsActive = true;

        MentorTimeSlotDto timeSlotDto = MentorTimeSlotDto.builder()
                                                         .id(1L)
                                                         .mentorId(1L)
                                                         .rqUId(testRequestUUID)
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
               .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.mentorId").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.rqUId").exists())
               .andExpect(MockMvcResultMatchers.jsonPath("$.startTime").value(startTime))
               .andExpect(MockMvcResultMatchers.jsonPath("$.endTime").value(endTime))
               .andExpect(MockMvcResultMatchers.jsonPath("$.slotType").value(slotTypeGroup))
               .andExpect(MockMvcResultMatchers.jsonPath("$.slotMeetingType")
                                               .value(slotMeetingTypeCommunication))
               .andExpect(MockMvcResultMatchers.jsonPath("$.maxParticipants")
                                               .value(maxParticipants))
               .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(timeSlotIsActive))
               .andExpect(MockMvcResultMatchers.jsonPath("$.meetingLink").value(testLink))
               .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(testDescription))
               .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists());

    }

}