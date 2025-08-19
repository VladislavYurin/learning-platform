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

    INDIVIDUAL("Индивидуальный слот"),
    GROUP("Групповой слот");

    private final String description;

}
