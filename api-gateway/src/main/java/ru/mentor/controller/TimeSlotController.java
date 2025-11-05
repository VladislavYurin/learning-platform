package ru.mentor.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.api.TimeSlotControllerApi;
import ru.mentor.gateway.model.MentorSlotInfoDto;
import ru.mentor.gateway.model.MentorTimeSlotCreateRequest;
import ru.mentor.gateway.model.MentorTimeSlotDto;
import ru.mentor.gateway.model.MentorTimeSlotInfoForUserDto;
import ru.mentor.mapper.MentorSlotInfoDtoMapper;
import ru.mentor.mapper.MentorTimeSlotDtoMapper;
import ru.mentor.mapper.MentorTimeSlotInfoForUserDtoMapper;
import ru.mentor.services.RedirectCalendarService;

import java.util.List;

@RestController
@RequestMapping("/slot")
@RequiredArgsConstructor
@Tag(name = "Time Slot Controller", description = "Управление слотами менторов")
public class TimeSlotController implements TimeSlotControllerApi {

    private final RedirectCalendarService redirectCalendarService;
    private final MentorTimeSlotDtoMapper mentorTimeSlotDtoMapper;
    private final MentorSlotInfoDtoMapper mentorSlotInfoDtoMapper;
    private final MentorTimeSlotInfoForUserDtoMapper mentorTimeSlotInfoForUserDtoMapper;

    /**
     * Реализация ручки POST /slot/book
     */
    @Override
    public ResponseEntity<MentorTimeSlotDto> bookSlot(Long timeSlotId) {
        ru.mentor.dto.MentorTimeSlotDto commonMentorTimeSlotDto = redirectCalendarService.bookTimeSlot(timeSlotId);
        MentorTimeSlotDto apiMentorTimeSlotDto = mentorTimeSlotDtoMapper.toApiDto(commonMentorTimeSlotDto);
        return ResponseEntity.ok(apiMentorTimeSlotDto);
    }

    /**
     * Реализация ручки POST /slot/create
     */
    @Override
    public ResponseEntity<MentorTimeSlotDto> createSlot(MentorTimeSlotCreateRequest mentorTimeSlotCreateRequest) {
        ru.mentor.dto.MentorTimeSlotDto commonMentorTimeSlotDto = redirectCalendarService.createTimeSlot(mentorTimeSlotCreateRequest);
        MentorTimeSlotDto apiMentorTimeSlotDto =  mentorTimeSlotDtoMapper.toApiDto(commonMentorTimeSlotDto);
        return ResponseEntity.ok(apiMentorTimeSlotDto);
    }

    /**
     * Реализация ручки GET /slot/my
     */
    @Override
    public ResponseEntity<List<MentorSlotInfoDto>> getMentorSlotsInfo() {
        List<ru.mentor.dto.MentorSlotInfoDto> listCommonMentorSlotInfoDto =
                redirectCalendarService.getMentorSlotsInfoForMentor();
        return ResponseEntity.ok().body(mentorSlotInfoDtoMapper.toListApiDto(listCommonMentorSlotInfoDto));
    }

    /**
     * Реализация ручки GET /slot
     */
    @Override
    public ResponseEntity<List<MentorTimeSlotInfoForUserDto>> getMentorSlotsInfoForUser(Long mentorId) {
        List<ru.mentor.dto.MentorTimeSlotInfoForUserDto> listCommonMentorTimeSlotInfoForUserDto =
                redirectCalendarService.getMentorSlotsInfoForUser(mentorId);
        return ResponseEntity.ok().body(mentorTimeSlotInfoForUserDtoMapper.toListApiDto(listCommonMentorTimeSlotInfoForUserDto));
    }
}
