package ru.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса предоставления доступа.
 * Содержит информацию, необходимую для предоставления доступа пользователю к курсу или модулю.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAccessRequest {

    /**
     * Идентификатор ментора, который предоставляет доступ.
     */
    private Long mentorId;

    /**
     * Идентификатор пользователя, которому предоставляется доступ.
     */
    private Long userId;

    /**
     * Идентификатор курса, к которому предоставляется доступ.
     */
    private Long courseId;

    /**
     * Идентификатор модуля, к которому предоставляется доступ.
     */
    private Long moduleId;

}
