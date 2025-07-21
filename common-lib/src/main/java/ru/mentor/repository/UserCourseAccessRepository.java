package ru.mentor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.UserCourseAccessEntity;

@Repository
public interface UserCourseAccessRepository extends JpaRepository<UserCourseAccessEntity, Long> {

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    List<UserCourseAccessEntity> findAllByUserId(Long userId);

}
