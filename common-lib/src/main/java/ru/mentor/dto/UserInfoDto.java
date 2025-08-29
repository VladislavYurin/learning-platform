package ru.mentor.dto;

import lombok.Builder;
import lombok.Data;
import ru.mentor.constant.Role;

/**
 * DTO для передачи информации о пользователе.
 * Содержит основные данные о пользователе системы.
 */
@Data
@Builder
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

}
