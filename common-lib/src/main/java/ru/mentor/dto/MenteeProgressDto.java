package ru.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи информации о прогрессе ученика.
 * Содержит данные о текущем прогрессе конкретного ученика по курсу.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenteeProgressDto {

    /**
     * Идентификатор пользователя (ученика).
     */
    private Long userId;

    /**
     * Имя ученика.
     */
    private String firstName;

    /**
     * Фамилия ученика.
     */
    private String lastName;

    /**
     * Идентификатор текущего модуля, который изучает ученик.
     */
    private Long currentModuleId;

    /**
     * Telegram никнейм ученика.
     */
    private String tgNickname;

}
