package ru.mentor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.mentor.gateway.api.AuthControllerApi;
import ru.mentor.gateway.model.AuthRequest;
import ru.mentor.gateway.model.JwtAuthResponse;
import ru.mentor.gateway.model.RegRequest;
import ru.mentor.services.AuthenticationService;

/**
 * Контроллер для регистрации, авторизации пользователя и обновления токена.
 */
@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

    private final AuthenticationService authenticationService;

    /**
     * Реализация ручки POST /auth/login
     */
    @Override
    public ResponseEntity<JwtAuthResponse> login(AuthRequest authRequest) {

        return ResponseEntity.ok(authenticationService.authentication(authRequest));
    }

    /**
     * Реализация ручки POST /auth/token/refresh
     */
    @Override
    public ResponseEntity<JwtAuthResponse> refreshToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Отсутствует токен");
        }
        return ResponseEntity.ok(authenticationService.refreshToken(authorization.substring(7)));
    }

    /**
     * Реализация ручки POST /auth/reg
     */
    @Override
    public ResponseEntity<JwtAuthResponse> registration(RegRequest regRequest) {
        return ResponseEntity.ok(authenticationService.registration(regRequest));
    }
}
