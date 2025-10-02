package ru.mentor.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Связь между ментором и его тегами.
 * <p>
 * Таблица "mentor_tag" хранит информацию о том,
 * какие теги закреплены за каким ментором.
 * Используется для отображения направлений преподавания
 * и индивидуальных наград ментора.
 * </p>
 */
@Table(name = "mentor_tag_link")
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class MentorTagLinkEntity {

    @Id
    @Column("id_mentor_tag")
    private Long id;

    @Column("id_user")
    private Long mentorId;

    @Column("id_tag")
    private Long tagId;

    @Column("attached_at")
    private LocalDateTime attachedAt;
}
