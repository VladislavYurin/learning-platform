package ru.mentor.util;

import java.util.UUID;

/**
 * Утилитный класс для генерации корреляционного идентификатора запроса (RqId).
 * Идентификатор используется для трассировки запросов между сервисами.
 */
public class RqGenerator {

    /**
     * Генерирует новый RqId в формате UUID v4.
     * @return строка UUID
     */
    public static String generateRqId() {
        return UUID.randomUUID().toString();
    }

}
