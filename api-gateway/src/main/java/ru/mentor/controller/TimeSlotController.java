package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.api.TimeSlotControllerApi;
import ru.mentor.gateway.model.MentorSlotInfoDto;
import ru.mentor.gateway.model.MentorTimeSlotCreateRequest;
import ru.mentor.gateway.model.MentorTimeSlotDto;
import ru.mentor.gateway.model.MentorTimeSlotInfoForUserDto;
import ru.mentor.mapper.MentorTimeSlotDtoMapper;
import ru.mentor.services.RedirectCalendarService;

import java.util.List;

@RestController
@RequestMapping("/slot")
@RequiredArgsConstructor
@Tag(name = "Time Slot Controller", description = "Управление слотами менторов")
public class TimeSlotController implements TimeSlotControllerApi {

    private final RedirectCalendarService redirectCalendarService;
    private final MentorTimeSlotDtoMapper mentorTimeSlotDtoMapper;

//    /**
//     * Создает новый слот
//     *
//     * @param request
//     *         Данные для создания слота
//     *
//     * @return Созданный слот
//     */
//    @Operation(
//            summary = "Создать слот",
//            description = "Позволяет создать новый слот. Требуются права ADMIN или MENTOR",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Слот успешно создан",
//                            content = @Content(schema = @Schema(implementation = MentorTimeSlotDto.class))),
//                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
//                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
//                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
//            }
//    )
//    @PostMapping("/create")
//    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
//    public ResponseEntity<MentorTimeSlotDto> createSlot(@RequestBody MentorTimeSlotCreateRequest request) {
//        return ResponseEntity.ok(redirectCalendarService.createTimeSlot(request));
//    }
//
//    /**
//     * Бронирует слот
//     *
//     * @param timeSlotId
//     *         ID слота
//     *
//     * @return Забронированный слот
//     */
//    @Operation(
//            summary = "Забронировать слот",
//            description = "Позволяет забронировать слот. Требуются права USER",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Слот успешно забронирован",
//                            content = @Content(schema = @Schema(implementation = MentorTimeSlotDto.class))),
//                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
//                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
//                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
//            }
//    )
//    @PostMapping("/book")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<MentorTimeSlotDto> bookSlot(@RequestParam long timeSlotId) {
//        return ResponseEntity.ok(redirectCalendarService.bookTimeSlot(timeSlotId));
//    }
//
//    /**
//     * Выдает информацию о слотах ментора с информацией об участниках
//     *
//     * @return {@link List<MentorSlotInfoDto>} Список слотов со списками участников.
//     */
//    @Operation(
//            summary = "Выдать информацию обо всех слотах ментора",
//            description = "Выдать информацию о слотах и об участниках. Требуются права MENTOR",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Выдан список слотов",
//                            content = @Content(schema = @Schema(implementation = MentorTimeSlotDto.class))),
//                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
//                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
//            }
//    )
//    @GetMapping("/my")
//    @PreAuthorize("hasRole('MENTOR')")
//    public ResponseEntity<List<MentorSlotInfoDto>> getMentorSlotsInfo() {
//        return ResponseEntity.ok().body(redirectCalendarService.getMentorSlotsInfoForMentor());
//    }
//
//    /**
//     * Выдает информацию о слотах ментора для ученика с признаком заполненности
//     *
//     * @return {@link List<MentorTimeSlotInfoForUserDto>} Список слотов.
//     */
//    @Operation(
//            summary = "Выдать информацию обо всех слотах ментора",
//            description = "Выдать информацию о слотах. Требуются права USER",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Выдан список слотов",
//                            content = @Content(schema = @Schema(implementation = MentorTimeSlotDto.class))),
//                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
//                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
//            }
//    )
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR') or hasRole('USER')")
//    public ResponseEntity<List<MentorTimeSlotInfoForUserDto>> getMentorSlotsInfoForUser(@RequestParam Long mentorId) {
//        return ResponseEntity.ok().body(redirectCalendarService.getMentorSlotsInfoForUser(mentorId));
//    }

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
        return ResponseEntity.ok();
    }

    @Override
    public ResponseEntity<List<MentorSlotInfoDto>> getMentorSlotsInfo() {
        return null;
    }

    @Override
    public ResponseEntity<List<MentorTimeSlotInfoForUserDto>> getMentorSlotsInfoForUser(Long mentorId) {
        return null;
    }
}
