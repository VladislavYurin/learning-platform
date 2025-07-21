package ru.mentor.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmoduleDto {

    private Long id;

    private String submoduleTitle;

    private String submoduleContent;

    private Integer submoduleOrderNumber;

    private LocalDateTime createdAt;

}
