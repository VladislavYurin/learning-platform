package ru.mentor.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseProgressStatisticDto {

    private Integer totalMenteeCount;

    private Map<Integer, Integer> moduleDistribution;

}
