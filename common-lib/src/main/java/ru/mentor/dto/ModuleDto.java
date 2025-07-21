package ru.mentor.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModuleDto {

    private Long id;

    private String moduleTitle;

    private Integer moduleOrderNumber;

    private String moduleDescription;

    private Boolean isActive;

    private LocalDateTime createdAt;

    private List<SubmoduleDto> submodules;

}
