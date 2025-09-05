package ru.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.exception.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MentorTimeSlotRepository
        extends JpaRepository<MentorTimeSlotEntity, Long> {

    default MentorTimeSlotEntity findByIdOrThrow(Long slotId) {
        return this.findById(slotId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(
                                "Слот с ID = %d не найден",
                                slotId
                        )
                ));
    }

    @Query(value = """
            select case when count(ts) > 0 then true else false end
            from MentorTimeSlotEntity ts
            join ts.mentor_time_slot__users mu
            where mu.id_user = :userId
            and (ts.start_time, ts.end_time) overlaps (:start, :end)
            """)
    default boolean existsOverlappingSlots(Long userId, LocalDateTime start, LocalDateTime end) {
        return false;
    }

    @Query(value = """
        SELECT slot FROM MentorTimeSlotEntity slot JOIN FETCH slot.meetingParticipants
        WHERE slot.mentor.id = :mentorId
        ORDER BY slot.startTime
    """)
    List<MentorTimeSlotEntity> findByMentorIdWithParticipants(Long mentorId);
}
