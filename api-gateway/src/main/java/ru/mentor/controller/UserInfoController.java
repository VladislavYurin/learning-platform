package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.services.UserInfoService;

/**
 * Контроллер для управления информацией пользователя.
 * Предоставляет эндпоинты для получения данных о текущем пользователе,
 * просмотра профиля другого пользователя по идентификатору и назначения
 * роли наставника текущему пользователю.
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Получение или изменение информации о пользователе")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @Operation(
            summary = "Получить свою информацию",
            description = "Позволяет получить пользовательскую информацию от пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользовательская информация выдана"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @GetMapping("/me")
    public ResponseEntity<?> getMyUserInfo() {
        UserInfoDto response = userInfoService.getMyUserInfo();
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Получить информацию о другом пользователе",
            description = "Позволяет получить пользовательскую информацию о другом пользователе",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользовательская информация выдана"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<?> getOtherUserInfo(@PathVariable Long userId) {
        UserInfoDto response = userInfoService.getOtherUserInfo(userId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Выдает пользователю роль МЕНТОР",
            description = "Позволяет выдать пользователю роль МЕНТОР",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Роль МЕНТОР выдана"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @PostMapping("/mentor/register")
    public ResponseEntity<?> assignMentorRole() {
        UserInfoDto response = userInfoService.assignMentorRole();
        return ResponseEntity.ok().body(response);
    }

}