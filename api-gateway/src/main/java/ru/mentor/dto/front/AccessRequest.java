package ru.mentor.dto.front;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessRequest {

    private Long userId;

    private Long courseId;

    private Long moduleId;

}
