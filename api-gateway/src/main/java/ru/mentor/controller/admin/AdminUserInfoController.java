package ru.mentor.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.services.UserInfoService;

/**
 * Контроллер для управления учетными записями пользователей для администраторов.
 */
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Получение или изменение информации о пользователе")
public class AdminUserInfoController {

    private final UserInfoService userInfoService;

    /**
     * Возвращает данные учетной записи отправителя запроса
     *
     * @return {@link UserInfoDto}
     */
    @Operation(
            summary = "Получить информацию о своем профиле.",
            description = "Позволяет получить информацию о своем профиле.",
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

    /**
     * Возвращает данные учетной записи пользователя по ID
     *
     * @param userId
     *         ID учетной записи
     *
     * @return {@link UserInfoDto}
     */
    @Operation(
            summary = "Получить информацию о другом профиле.",
            description = "Позволяет получить информацию о профиле другого пользователя.",
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

}
