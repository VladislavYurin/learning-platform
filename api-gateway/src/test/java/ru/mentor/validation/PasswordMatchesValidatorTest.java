package ru.mentor.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.dto.auth.RegRequest;

import jakarta.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты валидатора совпадения паролей")
class PasswordMatchesValidatorTest {

    private PasswordMatchesValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private PasswordMatches passwordMatches;

    @BeforeEach
    void setUp() {
        validator = new PasswordMatchesValidator();
        validator.initialize(passwordMatches);
    }

    @Test
    @DisplayName("Должен вернуть true при совпадающих паролях")
    void registration_passwordAndConfirmMatch_true() {
        // Given
        RegRequest regRequest = createRegRequest("user@test.com", "password123", "password123");

        // When
        boolean result = validator.isValid(regRequest, context);

        // Then
        assertTrue(result, "Валидация должна пройти при совпадающих паролях");
    }

    @Test
    @DisplayName("Должен вернуть false при несовпадающих паролях")
    void registration_passwordAndConfirmMismatch_False() {
        // Given
        RegRequest regRequest = createRegRequest("user@test.com", "password123", "differentPassword");

        // When
        boolean result = validator.isValid(regRequest, context);

        // Then
        assertFalse(result, "Валидация должна провалиться при несовпадающих паролях");
    }

    @Test
    @DisplayName("Должен вернуть false когда основной пароль null")
    void registration_nullPassword_false() {
        // Given
        RegRequest regRequest = createRegRequest("user@test.com", null, "password123");

        // When
        boolean result = validator.isValid(regRequest, context);

        // Then
        assertFalse(result, "Валидация должна провалиться когда основной пароль null");
    }

    @Test
    @DisplayName("Должен вернуть false когда подтверждение пароля null")
    void registration_confirmPasswordIsNull_false() {
        // Given
        RegRequest regRequest = createRegRequest("user@test.com", "password123", null);

        // When
        boolean result = validator.isValid(regRequest, context);

        // Then
        assertFalse(result, "Валидация должна провалиться когда подтверждение пароля null");
    }

    @Test
    @DisplayName("Должен вернуть false когда оба пароля null")
    void registration_passwordAndConfirmAreNulls_false() {
        // Given
        RegRequest regRequest = createRegRequest("user@test.com", null, null);

        // When
        boolean result = validator.isValid(regRequest, context);

        // Then
        assertFalse(result, "Валидация должна провалиться когда оба пароля null");
    }

    @Test
    @DisplayName("Должен вернуть true когда RegRequest null")
    void registration_emptyRegRequestDTO_true() {
        // Given
        RegRequest regRequest = null;

        // When
        boolean result = validator.isValid(regRequest, context);

        // Then
        assertTrue(result, "Валидация должна пройти для null объекта (по спецификации Bean Validation)");
    }

    @Test
    @DisplayName("Должен вернуть false при несовпадающих пустых строках")
    void registration_passwordIsBlankAndConfirmIsNotBlank_false() {
        // Given
        RegRequest regRequest = createRegRequest("user@test.com", "", "password123");

        // When
        boolean result = validator.isValid(regRequest, context);

        // Then
        assertFalse(result, "Валидация должна провалиться когда один пароль пустой, а другой нет");
    }

    /**
     * Вспомогательный метод для создания объекта RegRequest для тестов.
     * 
     * @param username имя пользователя
     * @param password основной пароль
     * @param confirmPassword подтверждение пароля
     * @return настроенный объект RegRequest
     */
    private RegRequest createRegRequest(String username, String password, String confirmPassword) {
        RegRequest regRequest = new RegRequest(username, password);
        regRequest.setTgNickname("@testuser");
        regRequest.setFirstName("Test");
        regRequest.setLastName("User");
        regRequest.setConfirmPassword(confirmPassword);
        return regRequest;
    }
}
