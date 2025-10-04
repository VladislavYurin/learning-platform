package ru.mentor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о слоте для ученика")
public class MentorTimeSlotInfoForUserDto {

    @Schema(description = "Общая информация о слоте")
    private MentorTimeSlotDto mentorTimeSlotDto;

    @Schema(description = "Признак заполнения слота")
    private boolean isSlotFull;
}
