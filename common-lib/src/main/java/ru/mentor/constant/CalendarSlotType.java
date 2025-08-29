package ru.mentor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * Перечисление типов слотов календаря.
 * Представляет различные категории слотов, которые могут быть использованы для планирования.
 */
@Getter
@RequiredArgsConstructor
public enum CalendarSlotType {

    /**
     * Индивидуальный тип слота.
     * Используется для обозначения слотов, предназначенных для одиночных встреч.
     */
    INDIVIDUAL("Индивидуальный слот"),
    /**
     * Групповой тип слота.
     * Используется для обозначения слотов, предназначенных для групповых встреч.
     */
    GROUP("Групповой слот");

    /**
     * Описание типа слота календаря.
     */
    private final String description;

}
