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

/**
 * Сущность доступа пользователя к модулю.
 * Представляет собой запись о предоставленном доступе пользователя к определенному модулю курса.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_module_access")
public class UserModuleAccessEntity {

    /**
     * Уникальный идентификатор записи доступа к модулю.
     */
    @Id
    @Column("id_access")
    private Long id;

    /**
     * Пользователь, которому предоставлен доступ к модулю.
     */
    @Column("user_id")
    private Long userId;

    /**
     * Курс, к которому относится модуль.
     */
    @Column("course_id")
    private Long courseId;

    /**
     * Модуль, к которому предоставлен доступ.
     */
    @Column("module_id")
    private Long moduleId;

    /**
     * Дата и время предоставления доступа к модулю.
     * Автоматически устанавливается при создании записи о доступе.
     */
    @Column("access_granted_at")
    private LocalDateTime accessGrantedAt;

    /**
     * Пользователь, который предоставил доступ к модулю.
     * Содержит информацию о том, кто дал право доступа к модулю.
     */
    @Column("access_granted_by")
    private Long accessGrantedById;

}
