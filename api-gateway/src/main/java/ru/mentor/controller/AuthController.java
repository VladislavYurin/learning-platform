package ru.mentor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.dto.auth.AuthRequest;
import ru.mentor.dto.auth.JwtAuthResponse;
import ru.mentor.dto.auth.RegRequest;
import ru.mentor.services.AuthenticationService;

/**
 * Контроллер для регистрации, авторизации пользователя и обновления токена.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth Management", description = "Регистрация и авторизация")
public class AuthController {

    private final AuthenticationService authenticationService;

    /**
     * Регистрация нового пользователя
     *
     * @param request
     *         содержит нужные данные для регистрации
     *
     * @return JwtAuthenticationResponse, хранящий токен в виде строки
     */
    @Operation(
            summary = "Регистрация пользователя",
            description = "Позволяет зарегистрировать пользователя с ролью USER",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Регистрация успешна"),
                    @ApiResponse(responseCode = "400", description = "Пользователь уже существует"),
            }
    )
    @PostMapping("/reg")
    @PermitAll
    public JwtAuthResponse registration(@RequestBody @Valid RegRequest request) {
        return authenticationService.registration(request);
    }

    /**
     * Авторизация пользователя
     *
     * @param request
     *         вмещает необходимые данные для авторизации
     *
     * @return JwtAuthenticationResponse, хранящий токен в виде строки
     */
    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/login")
    @PermitAll
    public JwtAuthResponse login(@RequestBody @Valid AuthRequest request) {
        return authenticationService.authentication(request);
    }

    /**
     * Обновление токена
     *
     * @param refreshToken
     *         хранить данные из перехваченного заголовка Authorization
     *
     * @return JwtAuthenticationResponse, хранящий токен в виде строки
     */
    @Operation(summary = "Обновление токена")
    @PostMapping("/token/refresh")
    public JwtAuthResponse refreshToken(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String refreshToken) {
        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            throw new RuntimeException("Отсутствует токен");
        }
        return authenticationService.refreshToken(refreshToken.substring(7));
    }

}
