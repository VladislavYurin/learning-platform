package ru.mentor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;

@Data
@Builder
@Schema(description = "Информация о слоте")
public class MentorTimeSlotDto {

    @Schema(description = "ID слота", example = "1")
    private long id;

    @Schema(description = "ID ментора", example = "1")
    private long mentorId;

    @Schema(description = "UUID запроса", example = "6e8f4e02-c91c-465f-b22d-7f102fca381b")
    private String rqUId;

    @Schema(description = "Начало открытого слота", example = "2025-01-15T13:00:00")
    private LocalDateTime startTime;

    @Schema(description = "Окончание открытого слота", example = "2025-01-15T13:00:00")
    private LocalDateTime endTime;

    @Schema(description = "Тип слота", example = "INDIVIDUAL")
    private CalendarSlotType slotType;

    @Schema(description = "Тип планируемой встречи", example = "COMMUNICATION")
    private CalendarSlotMeetingType slotMeetingType;

    @Schema(description = "Максимальное число участников", example = "10")
    private int maxParticipants;

    @Schema(description = "Признак активности слота", example = "true")
    @JsonProperty("isActive")
    private boolean isActive;

    @Schema(description = "Ссылка на встречу", example = "https://www.meet.ru/<uuid>")
    private String meetingLink;

    @Schema(description = "Описание слота", example = "Знакомство и обсуждение плана дальнейшего взаимодействия")
    private String description;

    @Schema(description = "Дата и время создания создания слота", example = "2025-01-15T13:00:00")
    private LocalDateTime createdAt;

}
