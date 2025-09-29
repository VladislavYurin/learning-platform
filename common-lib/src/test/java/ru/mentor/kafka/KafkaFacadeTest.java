package ru.mentor.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.constant.NotificationTypeEnum;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.dto.kafka.StudentReminderNotificationPayload;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.KafkaMapper;

@ExtendWith(MockitoExtension.class)
class KafkaFacadeTest {

    @Mock
    private KafkaProducerService mockKafkaProducerService;
    @Mock
    private BaseMapper mockBaseMapper;
    @Mock
    private KafkaMapper mockKafkaMapper;
    @Mock
    private UserEntity mockUser;
    @Mock
    private MentorTimeSlotEntity mockMentorTimeSlotEntity;
    @Mock
    private UserInfoDto mockUserInfoDto;
    @Mock
    private StudentReminderNotificationPayload  mockStudentReminderNotificationPayload;
    @Mock
    private MentorTimeSlotEntity mockSlot;
    @Mock
    private KafkaNotificationDto mockKafkaNotificationDto;

    @InjectMocks
    private KafkaFacade kafkaFacade;

    @Test
    void sendStudentCalendarSlotReminderMessage_validData_methodsCalled() {
        Mockito.when(mockBaseMapper.mapUserDto(mockUser)).thenReturn(mockUserInfoDto);
        Mockito.when(mockKafkaMapper.createStudentReminderNotificationPayload(mockSlot, mockUser))
                .thenReturn(mockStudentReminderNotificationPayload);
        Mockito.when(mockKafkaMapper.createKafkaNotificationDto(
                    NotificationTypeEnum.STUDENT_CALENDAR_SLOT_REMINDER,
                    mockUserInfoDto,
                    mockStudentReminderNotificationPayload))
                .thenReturn(mockKafkaNotificationDto);

        kafkaFacade.sendStudentCalendarSlotReminderMessage(mockSlot, mockUser);

        Mockito.verify(mockKafkaProducerService, Mockito.times(1))
                .send(mockKafkaNotificationDto);
        Mockito.verify(mockBaseMapper, Mockito.times(1)).mapUserDto(mockUser);
        Mockito.verify(mockKafkaMapper, Mockito.times(1))
                .createStudentReminderNotificationPayload(mockSlot, mockUser);
        Mockito.verify(mockKafkaMapper, Mockito.times(1)).createKafkaNotificationDto(
                NotificationTypeEnum.STUDENT_CALENDAR_SLOT_REMINDER,
                mockUserInfoDto,
                mockStudentReminderNotificationPayload
        );
    }
}