package ru.mentor.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.mentor.config.ReminderProperties;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.testUtil.TestDataGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@DataJpaTest
@ActiveProfiles("test")
public class MentorTimeSlotRepositoryTest {

    private LocalDateTime currentTime;
    private LocalDateTime endTime;
    private UserEntity mentor;
    private UserEntity student1;
    private UserEntity student2;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private MentorTimeSlotRepository mentorTimeSlotRepository;

    private ReminderProperties reminderProperties;

    @BeforeEach
    public void setupEach() {
        reminderProperties = new ReminderProperties();
        reminderProperties.setRemindBeforeMinutes(60);
        reminderProperties.setSchedulerIntervalMs(300000L);

        currentTime = LocalDateTime.of(2035, 1, 1, 1, 0);
        endTime = currentTime.plusMinutes(reminderProperties.getRemindBeforeMinutes());
        mentor = TestDataGenerator.getMentorEntity();
        mentor.setId(null);
        student1 = TestDataGenerator.getUserEntity();
        student1.setId(null);
        student2 = TestDataGenerator.getAnotherUserEntity();
        student2.setId(null);

        testEntityManager.persist(mentor);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();
        testEntityManager.clear();
    }

    @Test
    void findUpcomingTimeSlotsWithParticipants_noMatchSlots_emptyList() {
        MentorTimeSlotEntity slot1 = TestDataGenerator.createTestSlot(
                null, Set.of(student1, student2), true,
                currentTime.plusMinutes(reminderProperties.getRemindBeforeMinutes()).plusMinutes(10),
                currentTime.plusMinutes(reminderProperties.getRemindBeforeMinutes()).plusMinutes(40));
        slot1.setMentor(mentor);

        MentorTimeSlotEntity slot2 = TestDataGenerator.createTestSlot(
                null, Set.of(student1), true,
                currentTime.minusMinutes(30), currentTime.minusMinutes(10));
        slot2.setMentor(mentor);


        testEntityManager.persist(slot1);
        testEntityManager.persist(slot2);
        testEntityManager.flush();
        testEntityManager.clear();

        List<MentorTimeSlotEntity> result =
                mentorTimeSlotRepository.findUpcomingTimeSlotsWithParticipants(currentTime, endTime);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findUpcomingTimeSlotsWithParticipants_withMatchSlot_oneSlotList() {
        log.info("CURRENT TIME: {}", currentTime);
        log.info("END TIME: {}", endTime);
        MentorTimeSlotEntity slot1 = TestDataGenerator.createTestSlot(
                null, Set.of(student1, student2), true,
                currentTime.plusMinutes(10), currentTime.plusMinutes(40));

        testEntityManager.persist(slot1);

        MentorTimeSlotEntity slot2 = TestDataGenerator.createTestSlot(
                null, Set.of(student1), true, currentTime.minusMinutes(30), currentTime.minusMinutes(10));
        testEntityManager.persist(slot2);

        MentorTimeSlotEntity slot3 = TestDataGenerator.createTestSlot(
                null, Set.of(student2), true, currentTime.plusMinutes(70), currentTime.plusMinutes(100));
        testEntityManager.persist(slot3);

        testEntityManager.flush();
        testEntityManager.clear();

        List<MentorTimeSlotEntity> result =
                mentorTimeSlotRepository.findUpcomingTimeSlotsWithParticipants(currentTime, endTime);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(slot1.getId(), result.get(0).getId());
        Assertions.assertEquals(2, result.get(0).getMeetingParticipants().size());
    }
}