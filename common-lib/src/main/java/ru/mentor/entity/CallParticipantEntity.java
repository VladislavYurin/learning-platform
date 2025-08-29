package ru.mentor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Сущность участника встречи.
 * Представляет собой запись об участии пользователя в забронированной встрече.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "call_participants")
public class CallParticipantEntity {

    /**
     * Уникальный идентификатор записи об участии.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_participation")
    private Long id;

    /**
     * Забронированный временной слот, в котором участвует пользователь.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private BookedTimeSlotEntity booking;

    /**
     * Участник встречи.
     * Пользователь, который участвует в забронированной встрече.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id", nullable = false)
    private UserEntity mentee;

    /**
     * Флаг участия во встрече.
     */
    @Column(name = "attended")
    private Boolean attended = false;

    /**
     * Дата и время создания записи об участии.
     * Автоматически устанавливается при создании записи.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления записи об участии.
     * Автоматически обновляется при каждом изменении записи.
     */
    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
