package ru.mentor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InnerCreateCourseRequest {

    private Long authorId;

    private String courseName;

    private String courseDescription;

}
