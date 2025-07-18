package ru.mentor.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Обработчик ошибок, связанных с отсутствием аутентификации
 * (не передан токен, токен просрочен, подпись неверная и т.п.)
 */
@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Обработка AuthenticationException и отправка ответа клиенту в виде json
     *
     * @param request
     *         HTTP-запрос от клиента. Из него можно получить заголовки, параметры, путь и т.д
     * @param response
     *         - HTTP-ответ, в который помещается статус, тело, заголовки и т.п
     * @param authException
     *         - объект класса AuthenticationException
     */
    @Override
    public void commence(
            HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        log.warn("Unauthorized access attempt", authException);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Unauthorized\"}");
    }

}
