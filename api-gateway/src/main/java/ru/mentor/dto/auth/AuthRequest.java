package ru.mentor.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ДТО, обладающее необходимыми для авторизации данными
 */
@Data
@Schema(description = "Запрос на авторизацию")
public class AuthRequest {

    /**
     * Адрес электронной почты пользователя. Обязательное поле.
     */
    @Schema(description = "Email пользователя", example = "pipa@popa.com")
    @NotBlank(message = "Email пользователя не может быть пустым")
    private final String username;

    /**
     * Пароль пользователя. Обязательное поле.
     */
    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min = 6, max = 255, message = "Длина пароля должна быть от 6 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустым")
    private final String password;

}
