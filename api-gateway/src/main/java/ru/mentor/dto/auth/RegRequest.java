package ru.mentor.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.mentor.validation.PasswordMatches;

/**
 * DTO для запроса регистрации нового пользователя.
 * Расширяет {@link AuthRequest} добавляя дополнительные поля для регистрации.
 * Включает валидацию совпадения паролей через аннотацию {@link PasswordMatches}.
 * 
 * @author API Gateway Team
 * @version 1.0
 * @since 1.0
 * @see AuthRequest
 * @see PasswordMatches
 */

@Setter
@Getter
@PasswordMatches
@Schema(description = "Запрос на регистрацию нового пользователя")
public class RegRequest extends AuthRequest {

    public RegRequest(
            @Size(min = 5, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов") @NotBlank(message = "Имя пользователя не может быть пустым") String username,
            @Size(min = 8, max = 255, message = "Длина пароля должна быть от 8 до 255 символов") @NotBlank(message = "Пароль не может быть пустым") String password) {
        super(username, password);
    }

    /**
     * Никнейм пользователя в Telegram для отправки уведомлений.
     */
    @Schema(description = "Никнейм в Telegram", example = "@john_doe")
    @NotBlank(message = "Никнейм в телеграмме не может быть пустым")
    private String tgName;

    /**
     * Имя пользователя.
     */
    @Schema(description = "Имя пользователя", example = "Иван")
    @NotBlank(message = "Имя не может быть пустым")
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    @Schema(description = "Фамилия пользователя", example = "Иванов")
    @NotBlank(message = "Фамилия не может быть пустой")
    private String lastName;

    /**
     * Подтверждение пароля для валидации совпадения.
     * Должно точно совпадать с полем password из родительского класса.
     */
    @Schema(description = "Подтверждение пароля", example = "my_1secret1_password")
    @Size(min = 8, max = 255, message = "Длина подтверждения пароля должна быть от 8 до 255 символов")
    @NotBlank(message = "Подтверждение пароля не может быть пустым")
    private String confirmPassword;

}
