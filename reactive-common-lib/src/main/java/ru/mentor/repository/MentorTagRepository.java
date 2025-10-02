package ru.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.mentor.entity.MentorTagEntity;

/**
 * Репозиторий для работы со справочником тегов менторов.
 */
@Repository
public interface MentorTagRepository extends R2dbcRepository<MentorTagEntity,Long> {

    Mono<Boolean> existsByTagName(String tagName);

}
