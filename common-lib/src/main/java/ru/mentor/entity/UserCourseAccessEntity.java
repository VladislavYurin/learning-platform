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
 * Сущность доступа пользователя к курсу.
 * Представляет собой запись о предоставленном доступе пользователя к определенному курсу.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_course_access")
public class UserCourseAccessEntity {

    /**
     * Уникальный идентификатор записи доступа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_access")
    private Long id;

    /**
     * Пользователь, которому предоставлен доступ к курсу.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Курс, к которому предоставлен доступ.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;

    /**
     * Пользователь, который предоставил доступ.
     * Содержит информацию о том, кто дал право доступа к курсу.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "access_granted_by", nullable = false)
    private UserEntity accessGrantedBy;

    /**
     * Дата и время предоставления доступа к курсу.
     * Автоматически устанавливается при создании записи о доступе.
     */
    @Column(name = "access_granted_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime accessGrantedAt;

}
