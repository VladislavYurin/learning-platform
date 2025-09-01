package ru.mentor.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Учетные данные пользователя")
public class UserCredentials {
    /**
     * Адрес электронной почты пользователя. Обязательное поле.
     */
    @Schema(description = "Email пользователя", example = "pipa@popa.com")
    @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
    @Email(message = "Некорректный формат email")
    private String username;

    /**
     * Пароль пользователя. Обязательное поле.
     */
    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min = 6, max = 255, message = "Длина пароля должна быть от 6 до 255 символов")
    @Pattern(regexp = "\\S+", message = "Пароль не должен содержать пробелов")
    private String password;
}
