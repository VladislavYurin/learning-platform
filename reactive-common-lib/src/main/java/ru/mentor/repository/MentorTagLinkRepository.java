package ru.mentor.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
        R2dbcRepository<MentorTagLinkEntity, Long> {
    Flux<MentorTagLinkEntity> findByMentorId(Long mentorId);

    @Query("DELETE FROM mentor_tag_link WHERE id_user = :mentorId AND id_tag = :tagId")
    Mono<Void> deleteByMentorIdAndTagId(@Param("mentorId") Long mentorId,
            @Param("tagId") Long tagId);
}
