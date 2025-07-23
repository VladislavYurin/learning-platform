package ru.mentor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InnerCreateModuleRequest {

    private Long userId;

    private Long courseId;

    private String moduleTitle;

    private Integer moduleOrderNumber;

    private String moduleContent;

}
