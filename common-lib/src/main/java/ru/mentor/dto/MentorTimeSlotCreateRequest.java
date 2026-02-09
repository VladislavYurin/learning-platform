package ru.mentor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
public class MentorTimeSlotCreateRequest {

    @NotNull(message = "Время начала слота не может быть пустым")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull(message = "Время окончания слота не может быть пустым")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

    @NotNull(message = "Тип слота календаря не может быть пустым")
    private CalendarSlotType slotType;

    @NotNull(message = "Статус слота не может быть пустым")
    private CalendarSlotMeetingType slotMeetingType;

    @NotNull(message = "Максимальное количество участников слота не может быть пустым")
    @Min(value = 1, message = "Максимальное количество участников слота должно быть не меньше 1")
    @Max(value = 50, message = "Максимальное количество участников слота должно быть не больше 50")
    private Integer maxParticipants;

    @NotBlank(message = "Ссылка на встречу не может быть пустой")
    private String meetingLink;

    private String description;

}