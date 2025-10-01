package ru.mentor.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.mentor.entity.MentorTimeSlotEntity;

@Repository
public interface MentorTimeSlotRepository
        extends ReactiveCrudRepository<MentorTimeSlotEntity, Long>,
                        ReactiveSortingRepository<MentorTimeSlotEntity, Long> {

    Flux<MentorTimeSlotEntity> findAllBy(Pageable pageable);
}