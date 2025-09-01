package ru.mentor.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на авторизацию")
public class AuthRequest extends UserCredentials {

    public AuthRequest(String username, String password) {
        super(username, password);
    }
}
