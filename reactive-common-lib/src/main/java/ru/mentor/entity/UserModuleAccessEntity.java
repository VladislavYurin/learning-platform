package ru.mentor.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;


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
     * Уникальный идентификатор записи доступа.
     */
    @Id
    @Column("id_access")
    private Long id;

    /**
     * Пользователь, которому предоставлен доступ к курсу.
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
     * Пользователь, который предоставил доступ к модулю.
     * Содержит Id того, кто дал право доступа к модулю курса.
     */
    @Column("access_granted_by")
    private Long accessGrantedByUserId;

    /**
     * Дата и время предоставления доступа к модулю курса.
     * Автоматически устанавливается при создании записи о доступе.
     */
    @Column("access_granted_at")
    private LocalDateTime accessGrantedAt;

}