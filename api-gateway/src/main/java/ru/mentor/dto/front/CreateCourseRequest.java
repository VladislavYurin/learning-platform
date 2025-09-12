package ru.mentor.dto.front;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateCourseRequest {

    private String courseName;

    private String courseDescription;

}
