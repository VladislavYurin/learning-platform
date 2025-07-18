package ru.mentor.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Course {

    private Long id;

    private String courseName;

    private String courseDescription;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private Long authorId;

    private List<Module> modules;

}
