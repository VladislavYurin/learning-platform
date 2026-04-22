package ru.mentor.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.MentorTagEntity;
import ru.mentor.exception.EntityAlreadyExistsException;

/**
 * Репозиторий для работы со справочником тегов менторов.
 */
@Repository
public interface MentorTagRepository extends JpaRepository<MentorTagEntity,Long> {

    boolean existsByTagName(String tagName);

}
