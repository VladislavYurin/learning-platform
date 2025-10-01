package ru.mentor.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;

/**
 * Сущность временного слота ментора.
 * Представляет собой доступное время ментора для проведения встреч или занятий.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mentor_time_slots")
public class MentorTimeSlotEntity {

    @Id
    @Column("id_slot")
    private Long id;

    @Column("mentor_id")
    private Long mentorId;

    @Column("start_time")
    private LocalDateTime startTime;

    @Column("end_time")
    private LocalDateTime endTime;

    @Column("slot_type")
    private CalendarSlotType slotType;

    @Column("slot_meeting_type")
    private CalendarSlotMeetingType slotMeetingType;

    @Column("max_participants")
    private Integer maxParticipants;

    @Column("is_active")
    private Boolean isActive;

    @Column("meeting_link")
    private String meetingLink;

    @Column("description")
    private String description;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

}
