package ru.mentor.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.UserModuleAccessEntity;

@Repository
public interface UserModuleAccessRepository extends JpaRepository<UserModuleAccessEntity, Long> {

    boolean existsByUserIdAndModuleId(Long userId, Long moduleId);

    void deleteAllByUserIdAndCourseId(Long userId, Long courseId);

    void deleteByUserIdAndModuleId(Long userId, Long moduleId);

    List<UserModuleAccessEntity> findAllByCourseId(Long courseId);

    Optional<UserModuleAccessEntity> findTopByUserIdAndCourseIdOrderByAccessGrantedAtDesc(Long id, Long courseId);

    List<UserModuleAccessEntity> findAllByUserIdAndCourseId(Long id, Long courseId);

}