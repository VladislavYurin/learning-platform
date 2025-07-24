package ru.mentor.services;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Сервис для работы с JWT-токенами.
 * Предоставляет методы для извлечения данных, генерации и валидации токенов.
 */
public interface JwtService {

    /**
     * Извлечение имя пользователя из JWT-токена.
     *
     * @param token
     *         JWT-токен
     *
     * @return имя пользователя
     */
    String extractUserName(String token);

    /**
     * Генерация JWT-токена на основе данных пользователя.
     *
     * @param userDetails
     *         объект с деталями пользователя
     *
     * @return сгенерированный JWT-токен
     */
    String generateToken(UserDetails userDetails);

    /**
     * Проверка валидности JWT-токена.
     *
     * @param token
     *         JWT-токен
     * @param userDetails
     *         объект с деталями пользователя
     *
     * @return true, если токен действителен
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

}
