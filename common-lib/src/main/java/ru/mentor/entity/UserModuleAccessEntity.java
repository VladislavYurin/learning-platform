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

/**
 * Сущность доступа пользователя к модулю.
 * Представляет собой запись о предоставленном доступе пользователя к определенному модулю курса.
 */
@Entity
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_access")
    private Long id;

    /**
     * Пользователь, которому предоставлен доступ к модулю.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Курс, к которому относится модуль.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;

    /**
     * Модуль, к которому предоставлен доступ.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleEntity module;

    /**
     * Дата и время предоставления доступа к модулю.
     * Автоматически устанавливается при создании записи о доступе.
     */
    @Column(name = "access_granted_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime accessGrantedAt;

    /**
     * Пользователь, который предоставил доступ к модулю.
     * Содержит информацию о том, кто дал право доступа к модулю.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "access_granted_by", nullable = false)
    private UserEntity accessGrantedBy;

}
