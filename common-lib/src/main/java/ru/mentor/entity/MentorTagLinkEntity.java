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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Сущность связующей таблицы ментор-тег.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mentor_tag_link",
        uniqueConstraints = @UniqueConstraint(name = "uk_mentor_tags_mentor_tag", columnNames = {
                "id_mentor",
                "id_tag"
        }))
@Builder
@EqualsAndHashCode(of = "id")
public class MentorTagLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mentor_tag")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mentor", referencedColumnName = "id_user")
    private UserEntity mentor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tag", referencedColumnName = "id_tag")
    private MentorTagEntity tag;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
