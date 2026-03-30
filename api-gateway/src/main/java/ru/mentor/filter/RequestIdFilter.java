package ru.mentor.filter;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mentor.constant.HeaderNames;
import ru.mentor.constant.MdcKeys;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

/**
 * Фильтр, обеспечивающий наличие идентификатора запроса
 * для корреляции логов в пределах HTTP-запроса.
 *
 * <p> Фильтр пытается прочитать {@code requestId} из входящего заголовка.
 * Если заголовок отсутствует, генерируется новый идентификатор.
 * Полученное значение записывается в MDC и добавляется в HTTP-ответ.</p>
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    /**
     * Обрабатывает HTTP-запрос, определяет идентификатор запросаб
     * сохраняетс его в MDC и добавляет в заголовок ответа.
     *
     * @param request входящий HTTP-запрос
     * @param response исходящий HTTP-ответ
     * @param filterChain цепочка фильтров
     * @throws ServletException при ошибке сервлет-обработки
     * @throws IOException при ошибке ввода-вывода
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = extractRequestId(request);

        MDC.put(MdcKeys.REQUEST_ID, requestId);
        MDC.put(MdcKeys.SERVICE_NAME, "api_gateway");

        response.setHeader(HeaderNames.REQUEST_ID, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MdcKeys.REQUEST_ID);
            MDC.remove(MdcKeys.SERVICE_NAME);
            MDC.remove(MdcKeys.USERNAME);
        }
    }

    /**
     * Извлекает идентификатор запроса из заголовка.
     * Если заголовок отсутствует или пуст, генерирует новый идентификатор.
     *
     * @param request входящий HTTP-запрос
     * @return идентификатор запроса
     */
    private String extractRequestId(HttpServletRequest request) {
        String requestID = request.getHeader(HeaderNames.REQUEST_ID);
        if (StringUtils.isBlank(requestID)) {
            return UUID.randomUUID().toString();
        }
        return requestID;
    }
}
