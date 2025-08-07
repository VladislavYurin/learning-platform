package ru.mentor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CalendarSlotType {

    INDIVIDUAL("Индивидуальный слот"),
    GROUP("Групповой слот");

    private final String description;

}
