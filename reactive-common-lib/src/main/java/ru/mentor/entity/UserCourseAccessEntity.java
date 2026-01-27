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
 * Сущность доступа пользователя к курсу.
 * Представляет собой запись о предоставленном доступе пользователя к определенному курсу.
 */

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
    @Column("id_access")
    private Long id;

    /**
     * Пользователь, которому предоставлен доступ к курсу.
     */
    private Long userId;

    /**
     * Курс, к которому предоставлен доступ.
     */
    private Long courseId;

    /**
     * Пользователь, который предоставил доступ.
     * Содержит Id того, кто дал право доступа к курсу.
     */
    private Long accessGrantedBy;

    /**
     * Дата и время предоставления доступа к курсу.
     * Автоматически устанавливается при создании записи о доступе.
     */
    @Column("access_granted_at")
    private LocalDateTime accessGrantedAt;

}
