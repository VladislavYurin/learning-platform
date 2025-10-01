package ru.mentor.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.entity.NotificationTemplateEntity;

/**
 * Репозиторий для работы с сущностью {@link NotificationTemplateEntity}.
 * <p>
 * Предоставляет стандартные CRUD-операции через {@link JpaRepository},
 * а также кастомные методы для поиска шаблонов уведомлений по типу.
 * </p>
 */
@Repository
public interface NotificationTemplateRepository extends
        ReactiveCrudRepository<NotificationTemplateEntity, Long> {

    /**
     * Находит шаблон уведомления по указанному типу.
     *
     * @param type
     *         тип уведомления ({@link NotificationTypeEnum})
     *
     * @return {@link Optional}, содержащий найденный шаблон уведомления,
     * или пустой {@link Optional}, если шаблон не найден
     */
    Mono<NotificationTemplateEntity> findByTemplateType(NotificationTypeEnum type);

}