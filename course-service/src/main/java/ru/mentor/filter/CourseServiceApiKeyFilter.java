package ru.mentor.filter;

import org.springframework.stereotype.Component;

/**
 * Фильтр для проверки API-ключа, используемый в сервисе курсов.
 * Этот класс расширяет функциональность абстрактного фильтра API-ключа
 * и предназначен для проверки доступа к эндпоинтам, связанным с курсами.
 */
@Component
public class CourseServiceApiKeyFilter extends AbstractApiKeyFilter {

}