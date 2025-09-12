package ru.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для внутреннего запроса на создание курса.
 * Содержит основную информацию, необходимую для создания нового курса.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InnerCreateCourseRequest {

    /**
     * Идентификатор автора курса.
     */
    private Long authorId;

    /**
     * Название курса.
     */
    private String courseName;

    /**
     * Описание курса.
     */
    private String courseDescription;

}
