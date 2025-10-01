package ru.mentor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.mentor.entity.MentorTimeSlotParticipantEntity;

@Repository
public interface MentorTimeSlotParticipantRepository
        extends ReactiveCrudRepository<MentorTimeSlotParticipantEntity, Long> {

    Flux<MentorTimeSlotParticipantEntity> findAllBySlotId(Long slotId);
}
