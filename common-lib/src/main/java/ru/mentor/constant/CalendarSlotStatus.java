package ru.mentor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Перечисление статусов слотов календаря.
 * Представляет различные типы мероприятий, которые могут быть запланированы в календаре.
 */
@Getter
@RequiredArgsConstructor
public enum CalendarSlotStatus {

    ACQUAINTANCE("Знакомство"),
    COMMUNICATION("Общий формат"),
    ACCEPTING("Прием задач");


    private final String description;
}
