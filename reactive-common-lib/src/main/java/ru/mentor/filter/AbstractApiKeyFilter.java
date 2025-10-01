//package ru.mentor.filter;
//
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
///**
// * Абстрактный фильтр для проверки API ключа в заголовках HTTP запросов.
// * Проверяет наличие и корректность ключа авторизации в заголовке "X-Service-Auth".
// * Если ключ отсутствует или неверен, возвращает HTTP статус 401 (UNAUTHORIZED).
// */
//@Component
//@Order(1)
//public abstract class AbstractApiKeyFilter implements Filter {
//
//    /**
//     * Действительный API ключ, загружаемый из конфигурационных свойств.
//     */
//    @Value("${microservice.auth-key}")
//    private String validApiKey;
//
//    /**
//     * Метод фильтрации запросов, проверяющий API ключ.
//     * Извлекает заголовок "X-Service-Auth" из HTTP запроса и сравнивает его с действительным ключом.
//     * Если ключ отсутствует или неверен, отправляет ошибку 401 (UNAUTHORIZED).
//     * В противном случае передает запрос дальше по цепочке фильтров.
//     *
//     * @param request  входящий сервлет запрос
//     * @param response исходящий сервлет ответ
//     * @param chain    цепочка фильтров для передачи запроса дальше
//     * @throws IOException      если происходит ошибка ввода-вывода
//     * @throws ServletException если происходит ошибка сервлета
//     */
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        String apiKey = httpRequest.getHeader("X-Service-Auth");
//        if (apiKey == null || !apiKey.equals(validApiKey)) {
//            HttpServletResponse httpResponse = (HttpServletResponse) response;
//            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
//            return;
//        }
//
//        chain.doFilter(request, response);
//    }
//
//}
