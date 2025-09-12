package ru.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.constant.BookingStatus;
import ru.mentor.entity.BookedTimeSlotEntity;

import java.util.Collection;

/**
 * Репозиторий для работы с забронированными временными слотами наставника.
 * Предоставляет методы для выполнения CRUD-операций и дополнительный метод для подсчетв количества броней для слота.
 */
@Repository
public interface BookedTimeSlotRepository extends JpaRepository<BookedTimeSlotEntity, Long> {

    /**
     * Подсчитывает количество броней (BookedTimeSlotEntity)
     * для слота с id = slotId и со статусом из переданного набора.
     * @param slotId id слота
     * @param statuses статусы слота
     * @return количество броней
     */
    long countBySlotIdAndStatusIn(Long slotId, Collection<BookingStatus> statuses);
}
