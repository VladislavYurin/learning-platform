package ru.mentor.services;

import org.springframework.web.multipart.MultipartFile;
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
     * @param avatar
     *         файл аватара
     * @return JWT-ответ с токеном
     */
    JwtAuthResponse registration(RegRequest request, MultipartFile avatar);

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
