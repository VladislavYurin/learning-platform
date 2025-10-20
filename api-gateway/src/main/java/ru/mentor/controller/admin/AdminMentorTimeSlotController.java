package ru.mentor.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.services.RedirectAdminCalendarService;

/**
 * Контроллер для управления слотами для администратора.
 */
@RestController
@RequestMapping("/admin/slot")
@RequiredArgsConstructor
@Tag(name = "Admin Mentor Time Slot Controller", description = "Управление слотами менторов.")
public class AdminMentorTimeSlotController {

    private final RedirectAdminCalendarService redirectCalendarService;

    /**
     * Возвращает информацию о слотах ментора с информацией об участниках.
     *
     * @param pageNumber
     *         номер страницы
     *
     * @param pageSize
     *         размер страницы
     *
     * @return список объектов {@link MentorSlotInfoDto} Список слотов со списками участников.
     */
    @Operation(
            summary = "Получить информацию обо всех слотах ментора",
            description = "Получить информацию о слотах и участниках. Требуются права администратора.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Выдан список слотов",
                            content = @Content(schema = @Schema(implementation = MentorTimeSlotDto.class))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            }
    )
    @GetMapping("/all")
    public ResponseEntity<Page<MentorSlotInfoDto>> getMentorSlotsInfo(
            @RequestParam int pageNumber,
            @RequestParam int pageSize
    ) {
        return ResponseEntity.ok().body(redirectCalendarService
                                                .getAllMentorTimeSlots(pageNumber, pageSize));
    }

}
