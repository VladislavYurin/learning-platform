package ru.mentor.dto.mentorTag;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "DTO ответа для запроса привязки тэгов к ментору")
public class MentorTagAttachResponseDto {

    @Schema(description = "Уникальный идентификатор запроса", example = "213")
    private String rqUid;

    @Schema(description = "Список ID привязанных тэгов для привязки", example = "[1,2,3]")
    private List<Long> tagsIds;

    @Schema(description = "Список ID которые не были привязаны", example = "[1,2,3]")
    private List<Long> didntAttached;
}
