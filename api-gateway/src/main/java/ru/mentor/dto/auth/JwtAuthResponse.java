package ru.mentor.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentor.constant.Role;

/**
 * DTO для ответа с токенами аутентификации.
 * Содержит access и refresh токены, а также роль пользователя
 * для определения прав доступа на фронтенде.
 * 
 * @author API Gateway Team
 * @version 1.0
 * @since 1.0
 * @see Role
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с токенами доступа и ролью пользователя")
public class JwtAuthResponse {

    /**
     * JWT токен доступа для аутентификации запросов.
     */
    @Schema(description = "JWT токен доступа", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    /**
     * JWT токен обновления для получения новых access токенов.
     */
    @Schema(description = "JWT токен обновления", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    /**
     * Роль пользователя в системе.
     * Используется фронтендом для определения доступных функций.
     */
    @Schema(description = "Роль пользователя в системе", example = "USER", allowableValues = {"USER", "MENTOR", "ADMIN"})
    private Role role;

}
