package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;
import ru.mentor.common.Role;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.entity.UserEntity;

@Component
public class UtilMapper {

    public static Role userEntityRoleToUserInfoRole(UserEntity userEntity) {
        return Role.valueOf(userEntity.getRole().name());
    }

    public static Timestamp buildTimestamp(LocalDateTime localDateTime) {
        return Timestamp.newBuilder()
                         .setSeconds(localDateTime.toEpochSecond(ZoneOffset.UTC))
                         .build();
    }

    public static SlotType calendarSlotTypeToSlotType(CalendarSlotType calendarSlotType) {
        return SlotType.valueOf(calendarSlotType.toString());
    }

    public static SlotMeetingType calendarSlotMeetingTypeToSlotMeetingType(
            CalendarSlotMeetingType calendarSlotMeetingType) {
        return SlotMeetingType.valueOf(calendarSlotMeetingType.toString());
    }
}
