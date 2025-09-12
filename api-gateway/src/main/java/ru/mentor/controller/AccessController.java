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
import ru.mentor.dto.front.CourseAccessRequest;
import ru.mentor.dto.front.ModuleAccessRequest;
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
    public ResponseEntity<?> giveCourseAccess(@RequestBody CourseAccessRequest request) {
        return redirectAccessService.giveCourseAccess(request);
    }

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
    public ResponseEntity<?> revokeCourseAccess(@RequestBody CourseAccessRequest request) {
        return redirectAccessService.revokeCourseAccess(request);
    }

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
    public ResponseEntity<?> giveModuleAccess(@RequestBody ModuleAccessRequest request) {
        return redirectAccessService.giveModuleAccess(request);
    }

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
    public ResponseEntity<?> revokeModuleAccess(@RequestBody ModuleAccessRequest request) {
        return redirectAccessService.revokeModuleAccess(request);
    }

}
