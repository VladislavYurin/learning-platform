package ru.mentor.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.mentor.validation.PasswordMatches;

@Setter
@Getter
@PasswordMatches
@Schema(description = "Запрос на регистрацию нового пользователя")
public class RegRequest extends UserCredentials {

    public RegRequest(String username, String password) {
        super(username, password);
    }

    /**
     * Никнейм пользователя в Telegram для отправки уведомлений.
     */
    @Schema(description = "Никнейм в Telegram", example = "@john_doe")
    @Pattern(regexp = "^@[a-zA-Z0-9_]+", message = "Никнейм должен начинаться с символа @")
    @Size(min = 5, max = 32, message = "Длина никнейма от 5 до 32 символов")
    private String tgNickname;

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
    private String confirmPassword;

}
