package ru.mentor.dto.front;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateModuleRequest {

    private Long courseId;

    private String moduleTitle;

    private Integer moduleOrderNumber;

    private String moduleContent;

}
