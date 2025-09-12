package ru.mentor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.EnumType;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import ru.mentor.constant.BookingStatus;

import java.time.LocalDateTime;

/**
 * Сущность для бронирования временного слота наставника.
 * Хранит «снимок» времени слота на момент бронирования и статус брони.
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
     * Идентификатор слота.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_slot")
    private Long id;

    /**
     * Наставник, за которым закреплен данный временной слот
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentor_id", nullable = false)
    private UserEntity mentor;

    /**
     * Ученик, который збронировал временной слот
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentee_id", nullable = false)
    private UserEntity mentee;

    /**
     * Время начала слота на момент бронирования.
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * Время окончания слота на момент бронирования.
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * Слот для бронирования (доступное время наставника для проведения встреч).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "slot_id", nullable = false)
    private MentorTimeSlotEntity slot;

    /**
     * Статус бронирования.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status_type", nullable = false, length = 50)
    private BookingStatus status;

    /**
     * Вызывается JPA/Hibernate ПЕРЕД вставкой новой записи (INSERT).
     * Если статус не указан, устанавливает по умолчанию {@link BookingStatus#REQUESTED}.
     * В случае, если слот связан, метод «подтягивает» недостающие данные
     * (наставника и время начала/окончания) из сущности MentorTimeSlotEntity.
     */
    @PrePersist
    void prePersist() {
        if (status == null) {
            status = BookingStatus.REQUESTED;
        }
        if (slot != null) {
            if (mentor == null) mentor = slot.getMentor();
            if (startTime == null) startTime = slot.getStartTime();
            if (endTime == null) endTime = slot.getEndTime();
        }
    }
}
