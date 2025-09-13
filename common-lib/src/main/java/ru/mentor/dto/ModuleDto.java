package ru.mentor.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModuleDto {

    private Long id;

    private String moduleTitle;

    private Integer moduleOrderNumber;

    private String moduleContent;

    private Boolean isActive;

    private LocalDateTime createdAt;

}
