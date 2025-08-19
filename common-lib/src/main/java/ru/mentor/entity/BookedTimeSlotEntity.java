package ru.mentor.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Сущность забронированного временного слота.
 * Представляет собой запись на встречу, связанную с определенным временным слотом ментора.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "booked_time_slots")
public class BookedTimeSlotEntity {

    /**
     * Уникальный идентификатор бронирования.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_booking")
    private Long id;

    /**
     * Связанный временной слот ментора.
     * Представляет собой слот времени, который был забронирован для встречи.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false, unique = true)
    private MentorTimeSlotEntity slot;

    /**
     * Заметки ментора по встрече.
     */
    @Column(name = "mentor_notes")
    private String mentorNotes;

    /**
     * Результаты встречи.
     */
    @Column(name = "meeting_outcome")
    private String meetingOutcome;

    /**
     * Дата и время создания записи о бронировании.
     * Автоматически устанавливается при создании записи.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления записи о бронировании.
     * Автоматически обновляется при каждом изменении записи.
     */
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Список участников встречи.
     * Содержит информацию о всех пользователях, участвующих в забронированной встрече.
     */
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CallParticipantEntity> participants;

}
