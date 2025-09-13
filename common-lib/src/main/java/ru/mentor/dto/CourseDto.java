package ru.mentor.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseDto {

    private Long id;

    private String courseTitle;

    private String courseDescription;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private Long authorId;

    private List<ModuleDto> modules;

}
