package ru.mentor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CalendarSlotStatus {

    ACQUAINTANCE("Знакомство"),
    COMMUNICATION("Общий формат"),
    ACCEPTING("Прием задач");

    private final String description;
}
