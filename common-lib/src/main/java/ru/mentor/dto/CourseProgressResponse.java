package ru.mentor.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseProgressResponse {

    private Long courseId;

    private String courseTitle;

    private List<MenteeProgressDto> mentee;

    private CourseProgressStatisticDto statistic;

}
