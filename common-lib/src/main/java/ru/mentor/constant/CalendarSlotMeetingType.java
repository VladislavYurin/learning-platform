package ru.mentor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CalendarSlotMeetingType {

    ACQUAINTANCE("Знакомство"),
    COMMUNICATION("Общий формат"),
    ACCEPTING("Прием задач");

    private final String description;
}
