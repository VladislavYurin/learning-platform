package ru.mentor.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentor.constant.Role;
import ru.mentor.dto.mentorTag.MentorTagDto;

/**
 * DTO для передачи информации о пользователе.
 * Содержит основные данные о пользователе системы.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {

    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Имя пользователя (логин).
     */
    private String username;

    /**
     * Роль пользователя в системе.
     * Определяет уровень доступа пользователя.
     */
    private Role role;

    /**
     * Имя пользователя.
     */
    private String firstName;

    /**
     * Фамилия пользователя.
     */
    private String lastName;

    /**
     * Telegram никнейм пользователя.
     */
    private String tgNickname;

    /**
     * Идентификатор чата Telegram пользователя.
     */
    private Long tgChatId;

    /**
     * Тэги ментора.
     */
    private List<MentorTagDto> mentorTags;

}
