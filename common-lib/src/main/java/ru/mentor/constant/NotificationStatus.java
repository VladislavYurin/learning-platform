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
    SENT_BOTH("Отправлено на почту и в телеграм"),

    SENT_TG("Отправлено в телеграм"),

    SENT_MAIL("Отправлено на почту"),

    REPLY_RECEIVED("Пользователь ответил на уведомление"),

    ERROR_SENT("Ошибка отправки уведомления");
    /**
     * Описание типа слота календаря.
     */
    private final String description;
}
