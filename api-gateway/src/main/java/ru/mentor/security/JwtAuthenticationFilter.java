package ru.mentor.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mentor.services.JwtService;
import ru.mentor.services.UserService;

/**
 * Класс проверяет наличие и валидность токена
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Извлечение и проверка токена
     *
     * @param request
     *         содержит данные о входящем запросе
     * @param response
     *         хранит в себе ответ сервера
     * @param filterChain
     *         передает запрос по цепочке фильтров
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {

            // Получаем токен из заголовка
            var authHeader = request.getHeader(HEADER_NAME);
            if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(
                    authHeader,
                    BEARER_PREFIX
            )) {
                filterChain.doFilter(request, response);
                return;
            }

            // Обрезаем префикс и получаем имя пользователя из токена
            var jwt = authHeader.substring(BEARER_PREFIX.length());
            var username = jwtService.extractUserName(jwt);

            if (StringUtils.isNotEmpty(username)
                    && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService
                        .userDetailsService()
                        .loadUserByUsername(username);

                // Если токен валиден, то аутентифицируем пользователя
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authToken);
                    SecurityContextHolder.setContext(context);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            handleJwtException(response, "JWT token expired");
        } catch (JwtException | IllegalArgumentException ex) {
            handleJwtException(response, "Invalid JWT token");
        }
    }

    /**
     * Определяет, для каких запросов фильтр не должен применяться.
     * @param request текущий HTTP-запрос
     * @return true, если фильтр не должен выполняться для этого запроса, иначе false
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/reg") ||
                path.startsWith("/auth/login") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars");
    }

    /**
     * Формирует и отправляет JSON-ответ об ошибке аутентификации (JWT) со статусом {@code 401 Unauthorized}.
     * @param response HTTP-ответ, в который будет записано сообщение об ошибке
     * @param message текст ошибки для клиента (короткий и безопасный)
     * @throws IOException при ошибке записи в поток ответа
     */
    private void handleJwtException(HttpServletResponse response, String message)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(
                String.format("{\"error\": \"%s\"}", message)
        );
    }

}
