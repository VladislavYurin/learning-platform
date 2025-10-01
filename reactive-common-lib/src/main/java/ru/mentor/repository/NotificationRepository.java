package ru.mentor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.NotificationEntity;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<NotificationEntity, Long> {
}
