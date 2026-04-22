package ru.mentor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;





/**
 * Связь между ментором и его тегами.
 * <p>
 * Таблица "mentor_tag" хранит информацию о том,
 * какие теги закреплены за каким ментором.
 * Используется для отображения направлений преподавания
 * и индивидуальных наград ментора.
 * </p>
 */
@Entity
@Table(name = "mentor_tag_link",
        uniqueConstraints = @UniqueConstraint(
                name = "uc_mentor_tag_user_tag",
                columnNames = {"id_user", "id_tag"}
        ))
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class MentorTagLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mentor_tag")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    private UserEntity mentor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tag", referencedColumnName = "id_tag")
    private MentorTagEntity tag;

    @Column(name = "attached_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime attachedAt;
}
