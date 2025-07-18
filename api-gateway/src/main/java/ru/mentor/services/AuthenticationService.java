package ru.mentor.services;

import ru.mentor.dto.auth.AuthRequest;
import ru.mentor.dto.auth.JwtAuthResponse;
import ru.mentor.dto.auth.RegRequest;

/**
 * Сервис для работы с аутентификацией и регистрацией пользователей.
 */
public interface AuthenticationService {

    /**
     * Регистрация нового пользователя.
     *
     * @param request
     *         данные для регистрации
     *
     * @return JWT-ответ с токеном
     */
    JwtAuthResponse registration(RegRequest request);

    /**
     * Аутентификация пользователя.
     *
     * @param request
     *         данные для входа
     *
     * @return JWT-ответ с токеном
     */
    JwtAuthResponse authentication(AuthRequest request);

    /**
     * Обновление JWT-токена.
     *
     * @param token
     *         текущий токен
     *
     * @return новый JWT-токен
     */
    JwtAuthResponse refreshToken(String token);

}
