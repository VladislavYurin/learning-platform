package ru.mentor.filter;

import org.springframework.stereotype.Component;

/**
 * Фильтр для проверки API-ключа доступа к сервису.
 */
@Component
public class AccessServiceApiKeyFilter extends AbstractApiKeyFilter {

}
