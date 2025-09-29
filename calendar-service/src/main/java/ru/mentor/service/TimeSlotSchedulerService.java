package ru.mentor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.mentor.config.ReminderProperties;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.repository.MentorTimeSlotRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TimeSlotSchedulerService {

    private final ReminderProperties reminderProperties;
    private final MentorTimeSlotRepository mentorTimeSlotRepository;
    private final CalendarNotificationService calendarNotificationService;

    @Scheduled(fixedRateString = "#{@reminderProperties.schedulerIntervalMs}")
    public void checkTimeSlotsAndSendNotifications() {
        log.info("Поиск слотов для напоминаний...");

        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime endTime = currentTime.plusMinutes(reminderProperties.getRemindBeforeMinutes());

        List<MentorTimeSlotEntity> timeSlots =
                mentorTimeSlotRepository.findUpcomingTimeSlotsWithParticipants(currentTime, endTime);

        timeSlots.forEach(slot -> {
            slot.getMeetingParticipants().forEach(student ->
                    calendarNotificationService.sendStudentReminder(slot, student));
            calendarNotificationService.sendMentorReminder(slot);
        });

        log.info("Найдено и обработано {} слотов", timeSlots.size());
    }
}
