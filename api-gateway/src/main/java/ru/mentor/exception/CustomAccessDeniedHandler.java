package ru.mentor.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Обработчик ошибок, связанных с отсутствием нужной роли
 */
@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Обработка AccessDeniedException и отправка ответа клиенту в виде json
     *
     * @param request
     *         HTTP-запрос от клиента. Из него можно получить заголовки, параметры, путь и т.д
     * @param response
     *         HTTP-ответ, в который помещается статус, тело, заголовки и т.п
     * @param accessDeniedException
     *         - объект класса AccessDeniedException
     */
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws
            IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Access denied\"}");
    }

}
