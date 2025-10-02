package ru.mentor.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.mentor.constant.MentorTagType;

/**
 * Справочник менторских тегов.
 * <p>
 * Хранит только теги, применимые к менторам (направления преподавания и кастомные награды).
 * Отдельно от возможных "курсных" тегов, чтобы избежать пересечений и зависимостей.
 * </p>
 *
 * <p><b>Примеры:</b></p>
 * <ul>
 *   <li>"Java" (тип DIRECTION)</li>
 *   <li>"Spring" (тип DIRECTION)</li>
 *   <li>"Лучший учитель года 1998" (тип BADGE)</li>
 * </ul>
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "mentor_tags")
public class MentorTagEntity {
    /**
     * Уникальный идентификатор тега ментора.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tag")
    private Long id;

    /**
     * Название тега для отображения пользователям.
     * Может содержать кириллицу, пробелы и специальные символы.
     */
    @Column(name = "tag_name",nullable = false,length = 50)
    private String tagName;

    /**
     * Тип тега: {@link MentorTagType#DIRECTION} или {@link MentorTagType#BADGE}.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MentorTagType type;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
    private List<MentorTagLinkEntity> mentorTagLinks;
}
