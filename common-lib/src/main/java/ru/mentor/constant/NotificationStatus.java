package ru.mentor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление статусов уведомлений.
 *
 */
@Getter
@RequiredArgsConstructor
public enum NotificationStatus {
    OK("Уведомление отправлено"),

    ERROR("Ошибка отправки уведомления");

    /**
     * Описание статуса уведомления.
     */
    private final String description;
}
