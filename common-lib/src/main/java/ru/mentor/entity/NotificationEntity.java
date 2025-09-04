package ru.mentor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import ru.mentor.constant.NotificationStatus;
import ru.mentor.constant.NotificationTypeEnum;

/**
 * Сущность, представляющая описание отправленных уведомлений.
 * <p>
 *     Предназначена для ведения истории отправленных уведомлений.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sended_notification")
public class NotificationEntity {
    /**
     * Уникальный идентификатор уведомления.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    /**
     * Тип уведомления.
     */
    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum notificationType;

    /**
     * Получатель уведомления.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserEntity recipient;

    /**
     * Статус уведомления.
     */
    @Column(name = "notification_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;

    /**
     * Текст ошибки в случае, когда уведомление не было отправлено.
     */
    @Column(name = "error_text")
    private String errorText;
}
