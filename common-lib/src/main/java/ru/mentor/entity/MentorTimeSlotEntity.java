package ru.mentor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "mentor_time_slots")
public class MentorTimeSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_slot")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private UserEntity mentor;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_type", nullable = false, length = 50)
    private CalendarSlotType slotType;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_meeting_type", nullable = false, length = 50)
    private CalendarSlotMeetingType slotMeetingType;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "meeting_link", length = 255)
    private String meetingLink;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active",  nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(name = "mentor_time_slot__users",
            joinColumns = @JoinColumn(name = "time_slot_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> meetingParticipants = new HashSet<>();

}
