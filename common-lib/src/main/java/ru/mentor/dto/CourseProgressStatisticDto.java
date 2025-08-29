package ru.mentor.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * DTO для передачи статистики прогресса курса.
 * Содержит данные о прогрессе всех учеников по курсу.
 */
@Data
@Builder
public class CourseProgressStatisticDto {

    /**
     * Общее количество учеников, записанных на курс.
     */
    private Integer totalMenteeCount;

    /**
     * Распределение учеников по количеству завершенных модулей.
     */
    private Map<Integer, Integer> moduleDistribution;

}
