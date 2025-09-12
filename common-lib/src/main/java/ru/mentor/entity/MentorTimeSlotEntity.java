package ru.mentor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность временного слота ментора.
 * Представляет собой доступное время ментора для проведения встреч или занятий.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "mentor_time_slots")
public class MentorTimeSlotEntity {

    /**
     * Уникальный идентификатор временного слота.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_slot")
    private Long id;

    /**
     * Ментор, которому принадлежит данный временной слот.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private UserEntity mentor;

    /**
     * Время начала слота.
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * Время окончания слота.
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * Тип слота календаря.
     * Определяет категорию слота.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "slot_type", nullable = false, length = 50)
    private CalendarSlotType slotType;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_meeting_type", nullable = false, length = 50)
    private CalendarSlotMeetingType slotMeetingType;

    /**
     * Максимальное количество участников слота.
     */
    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants = 1;

    /**
     * Флаг активности слота.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Ссылка на встречу.
     */
    @Column(name = "meeting_link", length = 255)
    private String meetingLink;

    /**
     * Описание слота.
     */
    @Column(name = "description")
    private String description;

    /**
     * Дата и время создания слота.
     * Автоматически устанавливается при создании записи.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления слота.
     * Автоматически обновляется при каждом изменении записи.
     */
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(name = "mentor_time_slot__users",
            joinColumns = @JoinColumn(name = "time_slot_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserEntity> meetingParticipants = new HashSet<>();
}