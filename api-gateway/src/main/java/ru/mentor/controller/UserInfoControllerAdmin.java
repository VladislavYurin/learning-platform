package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.services.impl.RedirectUserInfoService;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin User Management", description = "Получение информации о пользователе c gRPC")
public class UserInfoControllerAdmin {

    private final RedirectUserInfoService redirectUserInfoService;

    @Operation(
            summary = "Получить информацию о пользователе по id",
            description = "Позволяет асинхронно получить информацию о пользователе по его id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Выдана информация о пользователе"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoDto> getUserInfoById(@PathVariable Long userId) {
        UserInfoDto response = redirectUserInfoService.getOtherUserInfo(userId).join(); // TODO: Пока сделал контроллер синхронным
        return ResponseEntity.ok().body(response);
    }
}