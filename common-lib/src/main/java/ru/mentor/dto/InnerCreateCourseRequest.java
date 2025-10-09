package ru.mentor.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * DTO для внутреннего запроса на создание курса.
 * Содержит основную информацию, необходимую для создания нового курса.
 */
@Data
@Builder
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

    private List<Long> tagIds;

}
