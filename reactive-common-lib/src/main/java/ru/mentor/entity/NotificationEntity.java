package ru.mentor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.mentor.constant.NotificationDestination;
import ru.mentor.constant.NotificationStatus;
import ru.mentor.constant.NotificationTypeEnum;

/**
 * Сущность, представляющая описание отправленных уведомлений.
 * <p>
 *     Предназначена для ведения истории отправленных уведомлений.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sent_notification")
public class NotificationEntity {
    /**
     * Уникальный идентификатор уведомления.
     */
    @Id
    @Column("notification_id")
    private Long id;

    /**
     * Тип уведомления.
     */
    @Column("notification_type")
    private NotificationTypeEnum notificationType;

    /**
     * Получатель уведомления.
     */
    @Column("recipient_id")
    private Long recipientId;

    /**
     * Статус уведомления.
     */
    @Column("notification_status")
    private NotificationStatus notificationStatus;

    @Column("notification_destination")
    private NotificationDestination notificationDestination;

    /**
     * Текст ошибки в случае, когда уведомление не было отправлено.
     */
    @Column("error_text")
    private String errorText;
}
