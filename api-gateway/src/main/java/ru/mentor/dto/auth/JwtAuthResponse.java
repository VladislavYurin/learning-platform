package ru.mentor.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ДТО, передающее токен клиенту. Содержит пару токенов.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ c токеном доступа")
public class JwtAuthResponse {

    /**
     * Короткоживущий токен доступа
     */
    @Schema(description = "Access Token", example = "eyJhbGci...")
    private String accessToken;

    /**
     * Долгоживущий токен доступа
     */
    @Schema(description = "Refresh Token", example = "eyJhbGci...")
    private String refreshToken;

}
