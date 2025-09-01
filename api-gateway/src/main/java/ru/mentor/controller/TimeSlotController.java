package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.services.RedirectCalendarService;

@RestController
@RequestMapping("/slot")
@RequiredArgsConstructor
@Tag(name = "Slot Management", description = "Управление слотами менторов")
public class TimeSlotController {

    private final RedirectCalendarService redirectCalendarService;

    /**
     * Создает новый слот
     *
     * @param request
     *         Данные для создания слота
     *
     * @return Созданный слот
     */
    @Operation(
            summary = "Создать слот",
            description = "Позволяет создать новый слот. Требуются права ADMIN или MENTOR",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Слот успешно создан",
                            content = @Content(schema = @Schema(implementation = MentorTimeSlotDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<MentorTimeSlotDto> createSlot(@RequestBody MentorTimeSlotCreateRequest request) {
        return ResponseEntity.ok(redirectCalendarService.createTimeSlot(request));
    }

    /**
     * Бронирует слот
     *
     * @param timeSlotId
     *         ID слота
     *
     * @return Забронированный слот
     */
    @Operation(
            summary = "Забронировать слот",
            description = "Позволяет забронировать слот. Требуются права USER",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Слот успешно забронирован",
                            content = @Content(schema = @Schema(implementation = MentorTimeSlotDto.class))),
                    @ApiResponse(responseCode = "400", description = "Невалидные входные данные"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @PostMapping("/book")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MentorTimeSlotDto> bookSlot(@RequestParam long timeSlotId) {
        return ResponseEntity.ok(redirectCalendarService.bookTimeSlot(timeSlotId));
    }

}
