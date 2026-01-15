package ru.mentor.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.exception.EntityNotFoundException;

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

    default MentorTimeSlotEntity findByIdWithParticipantsOrThrow(Long slotId) {
        return this.findByIdWithParticipants(slotId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(
                                "Слот с ID = %d не найден",
                                slotId
                        )
                ));
    }

    @Query(nativeQuery = true, value = """
            select exists(
                select 1
                from mentor_time_slot__users slots_users join mentor_time_slots slots
                    on slots.id_slot = slots_users.time_slot_id
                where slots_users.user_id = :userId
                    and (slots.start_time, slots.end_time) overlaps (:start, :end)
                ) as result
            """)
    boolean existsOverlappingSlots(Long userId, LocalDateTime start, LocalDateTime end);

    @Query(value = """
                SELECT slot FROM MentorTimeSlotEntity slot LEFT JOIN FETCH slot.meetingParticipants
                WHERE slot.mentor.id = :mentorId
                ORDER BY slot.startTime
            """)
    List<MentorTimeSlotEntity> findByMentorIdWithParticipants(Long mentorId);

    @Query(value = """
            SELECT slot FROM MentorTimeSlotEntity slot JOIN FETCH slot.meetingParticipants
            WHERE slot.startTime > :currentTime AND slot.startTime <= :endTime
    """)
    List<MentorTimeSlotEntity> findUpcomingTimeSlotsWithParticipants(@Param("currentTime") LocalDateTime currentTime,
                                                                     @Param("endTime") LocalDateTime endTime);
    @Query(value = """
            SELECT slot FROM MentorTimeSlotEntity slot LEFT JOIN FETCH slot.meetingParticipants
            WHERE slot.id = :slotId
    """)
    Optional<MentorTimeSlotEntity> findByIdWithParticipants(Long slotId);

    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM mentor_time_slot__users slotUsers WHERE slotUsers.user_id = :userId   
    """)
    int deleteParticipantById(@Param("userId") Long userId);
}
