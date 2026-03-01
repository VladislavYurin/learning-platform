package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.services.UserInfoService;

import java.util.List;

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

    @Operation(
            summary = "Изменить свою информацию",
            description = "Позволяет изменить пользовательскую информацию от пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Пользовательская информация изменена"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @PutMapping("/me")
    public ResponseEntity<?> updateMyUserInfo(@RequestBody UserInfoDto updateDto) {
        UserInfoDto response = userInfoService.updateMyUserInfo(updateDto);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Поиск пользователей",
            description = "Универсальный поиск по всем полям пользователя. Ищет совпадения в email, Telegram никнейме, имени и фамилии",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешный поиск (возвращает список, может быть пустым)"),
                    @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса")
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<UserInfoDto>> searchUsers(
            @Parameter(description = "Текст для поиска. Поиск работает по email, Telegram, имени и фамилии")
            @RequestParam(required = false) String query) {
            List<UserInfoDto> response = userInfoService.searchUsers(query);
            return ResponseEntity.ok().body(response);
        }

}