package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;
import ru.mentor.calendar.Role;
import ru.mentor.calendar.SlotMeetingType;
import ru.mentor.calendar.SlotType;
import ru.mentor.calendar.UserInfo;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.entity.UserEntity;

@Component
public class UtilMapper {

    public static Role userEntityRoleToUserInfoRole(UserEntity userEntity) {
        return Role.valueOf(userEntity.getRole().name());
    }

    public static ru.mentor.constant.Role userInfoRoleToUserInfoDtoRole(UserInfo userInfo) {
        return ru.mentor.constant.Role.valueOf(userInfo.getRole().name());
    }

    public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos(), ZoneOffset.UTC);
    }

    public static Timestamp buildTimestamp(LocalDateTime localDateTime) {
        return Timestamp.newBuilder().setSeconds(localDateTime.toEpochSecond(ZoneOffset.UTC)).build();
    }

    public static SlotType calendarSlotTypeToSlotType(CalendarSlotType calendarSlotType) {
        return SlotType.valueOf(calendarSlotType.toString());
    }

    public static SlotMeetingType calendarSlotMeetingTypeToSlotMeetingType(CalendarSlotMeetingType calendarSlotMeetingType) {
        return SlotMeetingType.valueOf(calendarSlotMeetingType.toString());
    }

    public static CalendarSlotType slotTypeToCalendarSlotType(SlotType slotType) {
        return CalendarSlotType.valueOf(slotType.toString());
    }

    public static CalendarSlotMeetingType slotMeetingTypeToCalendarSlotMeetingType(SlotMeetingType slotMeetingType) {
        return CalendarSlotMeetingType.valueOf(slotMeetingType.toString());
    }
}
