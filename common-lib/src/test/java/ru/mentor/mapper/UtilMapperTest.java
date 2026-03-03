package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.mentor.common.Role;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.common.UserInfo;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.entity.UserEntity;

import java.time.LocalDateTime;

public class UtilMapperTest {
    @Test
    void userEntityRoleToUserInfoRole_mentorRole_returnsProtoMentorRole() {
        UserEntity userEntity = UserEntity.builder()
                .role(ru.mentor.constant.Role.MENTOR)
                .build();

        ru.mentor.common.Role result = UtilMapper.userEntityRoleToUserInfoRole(userEntity);
        Assertions.assertEquals(ru.mentor.common.Role.MENTOR,result);
    }

    @Test
    void userInfoRoleToUserInfoDtoRole_adminRole_returnsConstantAdminRole() {
        UserInfo userInfo = UserInfo.newBuilder()
                .setId(1L)
                .setUsername("user@test")
                .setRole(Role.ADMIN)
                .setFirstName("First")
                .setLastName("Last")
                .setTgNickname("@nick")
                .build();

        ru.mentor.constant.Role result = UtilMapper.userInfoRoleToUserInfoDtoRole(userInfo);

        Assertions.assertEquals(ru.mentor.constant.Role.ADMIN, result);
    }

    @Test
    void timestampToLocalDateTime_timestampNotNull_convertsCorrectly() {
        LocalDateTime source = LocalDateTime.of(2026, 4, 2, 18, 0, 0);
        Timestamp timestamp = UtilMapper.buildTimestamp(source);
        LocalDateTime result = UtilMapper.timestampToLocalDateTime(timestamp);
        Assertions.assertEquals(source, result);
    }

    @Test
    void calendarSlotTypeToSlotType_individual_matchesProtoEnum() {
        Assertions.assertEquals(
                SlotType.INDIVIDUAL,
                UtilMapper.calendarSlotTypeToSlotType(CalendarSlotType.INDIVIDUAL));
    }

    @Test
    void slotTypeToCalendarSlotType_roundTrip() {
        Assertions.assertEquals(
                CalendarSlotType.GROUP,
                UtilMapper.slotTypeToCalendarSlotType(SlotType.GROUP));
    }

    @Test
    void calendarSlotMeetingTypeToSlotMeetingType_communication_matchesProto() {
        Assertions.assertEquals(
                SlotMeetingType.COMMUNICATION,
                UtilMapper.calendarSlotMeetingTypeToSlotMeetingType(CalendarSlotMeetingType.COMMUNICATION));
    }

    @Test
    void slotMeetingTypeToCalendarSlotMeetingType_roundTrip() {
        Assertions.assertEquals(
                CalendarSlotMeetingType.COMMUNICATION,
                UtilMapper.slotMeetingTypeToCalendarSlotMeetingType(SlotMeetingType.COMMUNICATION));
    }
}
