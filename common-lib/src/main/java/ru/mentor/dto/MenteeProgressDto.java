package ru.mentor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenteeProgressDto {

    private Long userId;

    private String firstName;

    private String lastName;

    private Long currentModuleId;

}
