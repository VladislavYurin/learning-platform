package ru.mentor.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.mentor.dto.auth.RegRequest;

/**
 * Валидатор для проверки совпадения паролей
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, RegRequest> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        // Инициализация не требуется
    }

    @Override
    public boolean isValid(RegRequest regRequest, ConstraintValidatorContext context) {
        if (regRequest == null) {
            return true;
        }
        
        String password = regRequest.getPassword();
        String confirmPassword = regRequest.getConfirmPassword();
        
        if (password == null || confirmPassword == null) {
            return false;
        }
        
        return password.equals(confirmPassword);
    }
}
