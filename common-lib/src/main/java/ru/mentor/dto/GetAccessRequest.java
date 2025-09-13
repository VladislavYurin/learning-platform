package ru.mentor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAccessRequest {

    private Long mentorId;

    private Long userId;

    private Long courseId;

    private Long moduleId;

}
