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
import ru.mentor.constant.NotificationTypeEnum;

/**
 * Entity-класс, представляющий шаблон уведомления.
 * <p>
 * Используется для хранения текстов шаблонов в базе данных.
 * Каждый шаблон связан с определённым типом уведомления ({@link NotificationTypeEnum}).
 * </p>
 *
 * Таблица: {@code notification_templates}
 */
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
    @Column("id_template")
    private Long id;

     /**
      * Тип уведомления, которому соответствует данный шаблон.
      * <p>
      * Хранится в колонке {@code enum_name} в виде строки.
      * </p>
      */
    @Column("template_type")
    private NotificationTypeEnum templateType;

    /**
     * Текст шаблона уведомления.
     * <p>
     * Хранится в колонке {@code template_message}.
     * </p>
     */
    @Column("template_text")
    private String templateText;

    /**
     * Дата и время создания записи.
     */
    @Column("created_at")
    private LocalDateTime createdAt;
}