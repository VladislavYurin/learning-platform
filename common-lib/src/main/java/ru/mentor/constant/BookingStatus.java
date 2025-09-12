package ru.mentor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление статусов бронирования слота.
 * Представляет различные статусы для контроля за бронированием слота.
 */
@Getter
@RequiredArgsConstructor
public enum BookingStatus {

    /**
     * Заявка на бронирование создана и ожидает подтверждения наставником.
     */
    REQUESTED("Заявка создана"),

    /**
     * Бронь подтверждена и активна, слот занят.
     */
    CONFIRMED ("Бронь активна");

    /**
     * Описание статуса бронирования слота.
     */
    private final String description;
}
