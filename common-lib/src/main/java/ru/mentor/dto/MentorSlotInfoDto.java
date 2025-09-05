package ru.mentor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Подробная информация о слоте ментора и об участниках в нем")
public class MentorSlotInfoDto {

    @Schema(description = "Информация о слоте")
    private MentorTimeSlotDto slotDto;

    @Schema(description = "Информация об участниках слота")
    private List<UserInfoDto> participants;

}
