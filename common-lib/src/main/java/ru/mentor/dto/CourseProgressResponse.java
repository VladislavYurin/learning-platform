package ru.mentor.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * DTO для передачи информации о прогрессе курса.
 * Содержит данные о прогрессе всех учеников по конкретному курсу и общую статистику.
 */
@Data
@Builder
public class CourseProgressResponse {

    /**
     * Уникальный идентификатор курса.
     */
    private Long courseId;

    /**
     * Название курса.
     */
    private String courseTitle;

    /**
     * Список прогресса учеников по курсу.
     */
    private List<MenteeProgressDto> mentee;

    /**
     * Общая статистика прогресса по курсу.
     */
    private CourseProgressStatisticDto statistic;

}
