package ru.mentor.dto.mentorTag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для POST-запроса создания кастомного тэга
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO для запроса создания кастомного тэга")
public class MentorTagDtoCreateRequest {
    @Schema(description = "Имя-описание тэга", example = "Ментор года села Алупки")
    private String tagName;
}
