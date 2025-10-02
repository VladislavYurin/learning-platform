package ru.mentor.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mentor_tags")
public class MentorTagEntity {
    /**
     * Уникальный идентификатор тега ментора.
     */
    @Id
    @Column("id_tag")
    private Long id;

    /**
     * Название тега для отображения пользователям.
     * Может содержать кириллицу, пробелы и специальные символы.
     */
    @Column("tag_name")
    private String tagName;

    /**
     * Тип тега: {@link MentorTagType#DIRECTION} или {@link MentorTagType#BADGE}.
     */
    @Column("type")
    private MentorTagType type;
}
