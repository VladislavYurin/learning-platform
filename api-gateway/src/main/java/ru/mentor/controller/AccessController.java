package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.front.AccessRequest;
import ru.mentor.services.RedirectAccessService;

/**
 * Контроллер для управления доступами пользователей к курсам и модулям.
 * Предоставляет endpoints для выдачи и отзыва доступов.
 */
@RestController
@RequestMapping("/access")
@RequiredArgsConstructor
@Tag(name = "Access Management", description = "Управление доступами к курсам и модулям")
public class AccessController {

    private final RedirectAccessService redirectAccessService;

    /**
     * Выдает доступ пользователю к курсу
     *
     * @param request
     *         Запрос на выдачу доступа
     *
     * @return ResponseEntity с HTTP статусом
     */
    @Operation(
            summary = "Выдать доступ к курсу",
            description = "Позволяет выдать доступ пользователю к указанному курсу",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Доступ успешно выдан"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Курс или пользователь не найден")
            }
    )
    @PostMapping("/course/get-access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> getCourseAccessToUser(@RequestBody AccessRequest request) {
        return redirectAccessService.getCourseAccessToUser(request);
    }

    /**
     * Отзывает доступ пользователя к курсу
     *
     * @param request
     *         Запрос на отзыв доступа
     *
     * @return ResponseEntity с HTTP статусом
     */
    @Operation(
            summary = "Отозвать доступ к курсу",
            description = "Позволяет отозвать доступ пользователя к указанному курсу",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Доступ успешно отозван"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Доступ не найден")
            }
    )
    @PostMapping("/course/delete-access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> deleteCourseAccessToUser(@RequestBody AccessRequest request) {
        return redirectAccessService.deleteCourseAccessToUser(request);
    }

    /**
     * Выдает доступ пользователю к модулю курса
     *
     * @param request
     *         Запрос на выдачу доступа
     *
     * @return ResponseEntity с HTTP статусом
     */
    @Operation(
            summary = "Выдать доступ к модулю",
            description = "Позволяет выдать доступ пользователю к указанному модулю курса",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Доступ успешно выдан"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Модуль или пользователь не найден")
            }
    )
    @PostMapping("/module/get-access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> getModuleAccessToUser(@RequestBody AccessRequest request) {
        return redirectAccessService.getModuleAccessToUser(request);
    }

    /**
     * Отзывает доступ пользователя к модулю курса
     *
     * @param request
     *         Запрос на отзыв доступа
     *
     * @return ResponseEntity с HTTP статусом
     */
    @Operation(
            summary = "Отозвать доступ к модулю",
            description = "Позволяет отозвать доступ пользователя к указанному модулю курса",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Доступ успешно отозван"),
                    @ApiResponse(responseCode = "401", description = "Не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Доступ не найден")
            }
    )
    @PostMapping("/module/delete-access")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MENTOR')")
    public ResponseEntity<?> deleteModuleAccessToUser(@RequestBody AccessRequest request) {
        return redirectAccessService.deleteModuleAccessToUser(request);
    }

}
