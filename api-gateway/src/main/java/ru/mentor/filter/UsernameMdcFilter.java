package ru.mentor.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mentor.constant.MdcKeys;

/**
 * Фильтр для добавления имени пользователя в MDC после успешной аутентификации.
 *
 * <p>Извлекает текущую аутентификацию из {@link SecurityContextHolder} и,
 * если пользователь аутентифицирован и не является anonymousUser,
 * сохраняет его username в MDC по ключу {@link MdcKeys#USERNAME}.
 *
 * <p>После завершения обработки запроса очищает значение username из MDC.
 */
@Component
public class UsernameMdcFilter extends OncePerRequestFilter {

    private static final String ANONYMOUS_USER = "anonymousUser";

    /**
     * Добавляет username в MDC на время обработки запроса.
     *
     * @param request
     *         входящий HTTP-запрос
     * @param response
     *         HTTP-ответ
     * @param filterChain
     *         цепочка фильтров
     *
     * @throws ServletException
     *         при ошибке обработки фильтра
     * @throws IOException
     *         при ошибке ввода-вывода
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null
                    && authentication.isAuthenticated()
                    && authentication.getName() != null
                    && !ANONYMOUS_USER.equals(authentication.getName())) {
                MDC.put(MdcKeys.USERNAME, authentication.getName());
            }

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MdcKeys.USERNAME);
        }
    }
}