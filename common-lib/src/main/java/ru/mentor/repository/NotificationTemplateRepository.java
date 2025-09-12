package ru.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.entity.NotificationTemplateEntity;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью {@link ru.mentor.entity.NotificationTemplateEntity}.
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository},
 * а также кастомные методы для поиска шаблонов уведомлений по типу.
 * </p>
 */
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplateEntity, Long> {

    /**
     * Находит шаблон уведомления по указанному типу.
     *
     * @param type тип уведомления ({@link ru.mentor.constant.NotificationTypeEnum})
     * @return {@link Optional}, содержащий найденный шаблон уведомления,
     * или пустой {@link Optional}, если шаблон не найден
     */
    Optional<NotificationTemplateEntity> findByTemplateType(NotificationTypeEnum type);
}