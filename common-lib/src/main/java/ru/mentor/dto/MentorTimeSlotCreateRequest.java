package ru.mentor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;

import java.time.LocalDateTime;


@Getter @Setter
@Builder
public class MentorTimeSlotCreateRequest {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    @NotNull
    private CalendarSlotType slotType;

    @NotNull
    private CalendarSlotMeetingType slotMeetingType;

    @Min(1) @Max(50)
    private Integer maxParticipants;

    @NotBlank
    private String meetingLink;

    private String description;

}
