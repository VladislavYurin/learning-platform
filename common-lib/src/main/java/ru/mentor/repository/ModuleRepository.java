package ru.mentor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.exception.EntityNotFoundException;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {

    default ModuleEntity findByIdOrThrow(Long moduleId) {
        return this.findById(moduleId)
                   .orElseThrow(() -> new EntityNotFoundException(
                           String.format(
                                   "Модуль с ID = %d не найден",
                                   moduleId
                           )
                   ));
    }

    List<ModuleEntity> findAllByCourseIdOrderByModuleOrderNumberAsc(Long courseId);

}
