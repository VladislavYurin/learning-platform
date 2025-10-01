package ru.mentor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Подробная информация о слоте ментора и об участниках в нем")
public class MentorSlotInfoDto {

    @Schema(description = "Информация о слоте")
    private MentorTimeSlotDto slotDto;

    @Schema(description = "Информация об участниках слота")
    private List<UserInfoDto> participants;

}
