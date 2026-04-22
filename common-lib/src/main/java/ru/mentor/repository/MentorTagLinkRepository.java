package ru.mentor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.MentorTagEntity;
import ru.mentor.entity.MentorTagLinkEntity;

/**
 * Репозиторий для работы со связями менторов и тегов.
 * <p>
 * Основная таблица: {@code mentor_tag}, которая хранит связи "ментор ↔ тег".
 * Через связь на {@code MentorTagEntity} позволяет получить
 * справочную информацию о самих тегах.
 */
@Repository
public interface MentorTagLinkRepository extends
        JpaRepository<MentorTagLinkEntity, Long> {

    /**
     * Возвращает список тегов, закреплённых за конкретным ментором.
     *
     * @param mentorId
     *         идентификатор ментора
     *
     * @return список тегов (может быть пустым, если у ментора нет тегов)
     * <p>
     * JPQL-запрос выбирает связанные объекты {@code MentorTagEntity}
     * по условию {@code mentorId = :mentorId}, сортируя по имени тега.
     */
    @Query(
            """
                    select mt.tag
                    from MentorTagLinkEntity mt
                    where mt.mentor.id = :mentorId
                    order by mt.tag.tagName
                    
                    """
    )
    List<MentorTagEntity> findTagsByMentorId(@Param("mentorId") Long mentorId);

    /**
     * Удаляет тэги принадлежащие ментору по идентификатору ментора и тэга.
     *
     * @param mentorId
     *         идентификатор ментора
     * @param tagId
     *         идентификатор тэга
     *         JPQL-запрос выбирает связанные объекты {@code MentorTagEntity}
     *         по условию {@code mentorId = :mentorId} и {@code tagId = :tagId}, сортируя по имени
     *         тега.
     */
    @Modifying
    @Query("""
                delete
                from MentorTagLinkEntity mt
                where mt.mentor.id = :mentorId
                  and mt.tag.id = :tagId
            """)
    void deleteByMentorIdAndTagId(
            @Param("mentorId") Long mentorId,
            @Param("tagId") Long tagId);

}
