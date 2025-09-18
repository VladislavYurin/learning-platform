package ru.mentor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.mentor.constant.NotificationTypeEnum;

import java.time.LocalDateTime;

/**
 * Entity-класс, представляющий шаблон уведомления.
 * <p>
 * Используется для хранения текстов шаблонов в базе данных.
 * Каждый шаблон связан с определённым типом уведомления ({@link ru.mentor.constant.NotificationTypeEnum}).
 * </p>
 *
 * Таблица: {@code notification_templates}
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notification_templates")
public class NotificationTemplateEntity {

    /**
     * Уникальный идентификатор шаблона.
     * <p>
     * Генерируется автоматически при сохранении в базу данных.
     * </p>
     * */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template")
    private Long id;

     /**
      * Тип уведомления, которому соответствует данный шаблон.
      * <p>
      * Хранится в колонке {@code enum_name} в виде строки.
      * </p>
      */
    @Column(name = "template_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum templateType;

    /**
     * Текст шаблона уведомления.
     * <p>
     * Хранится в колонке {@code template_message}.
     * </p>
     */
    @Column(name = "template_text", nullable = false)
    private String templateText;

    /**
     * Дата и время создания записи.
     * <p>
     * Заполняется автоматически при вставке записи в БД
     * с помощью аннотации {@link org.hibernate.annotations.CreationTimestamp}.
     * </p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}