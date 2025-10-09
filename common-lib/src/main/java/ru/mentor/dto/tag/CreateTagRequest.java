package ru.mentor.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTagRequest {

    @Schema(description = "Название тэга", example = "backend")
    @NotBlank
    private String tagName;
}
