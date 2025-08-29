package ru.mentor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.exception.EntityNotFoundException;

/**
 * Репозиторий для работы с сущностями модулей.
 * Предоставляет методы для выполнения CRUD операций и дополнительные методы
 * для поиска модулей по различным критериям.
 */
@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {

    /**
     * Находит модуль по его идентификатору или выбрасывает исключение, если модуль не найден.
     *
     * @param moduleId идентификатор модуля
     * @return сущность модуля
     * @throws EntityNotFoundException если модуль с указанным идентификатором не найден
     */
    default ModuleEntity findByIdOrThrow(Long moduleId) {
        return this.findById(moduleId)
                   .orElseThrow(() -> new EntityNotFoundException(
                           String.format(
                                   "Модуль с ID = %d не найден",
                                   moduleId
                           )
                   ));
    }

    /**
     * Находит все модули указанного курса, отсортированные по порядковому номеру.
     *
     * @param courseId идентификатор курса
     * @return список модулей курса, отсортированный по порядковому номеру
     */
    List<ModuleEntity> findAllByCourseIdOrderByModuleOrderNumberAsc(Long courseId);

}
