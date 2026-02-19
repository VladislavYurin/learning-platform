package ru.mentor.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO для запроса предоставления доступа.
 * Содержит информацию, необходимую для предоставления доступа пользователю к курсу или модулю.
 */
@Data
@Builder
public class ModuleAccessRequest {

    /**
     * Идентификатор ментора, который предоставляет доступ.
     */
    private Long mentorId;

    /**
     * Идентификатор пользователя, которому предоставляется доступ.
     */
    private Long userId;

    /**
     * Идентификатор модуля, к которому предоставляется доступ.
     */
    private Long moduleId;

}
