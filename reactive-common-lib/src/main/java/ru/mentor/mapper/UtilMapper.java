package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Named;
import ru.mentor.common.Role;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.entity.UserEntity;

public final class UtilMapper {

    @Named("userEntityRoleToUserInfoRole")
    static Role userEntityRoleToUserInfoRole(UserEntity userEntity) {
        return Role.valueOf(userEntity.getRole().name());
    }

    @Named("buildTimestamp")
    static Timestamp buildTimestamp(LocalDateTime localDateTime) {
        return Timestamp.newBuilder()
                         .setSeconds(localDateTime.toEpochSecond(ZoneOffset.UTC))
                         .build();
    }

    @Named("calendarSlotTypeToSlotType")
    static SlotType calendarSlotTypeToSlotType(CalendarSlotType calendarSlotType) {
        return SlotType.valueOf(calendarSlotType.toString());
    }

    @Named("calendarSlotMeetingTypeToSlotMeetingType")
    static SlotMeetingType calendarSlotMeetingTypeToSlotMeetingType(
            CalendarSlotMeetingType calendarSlotMeetingType) {
        return SlotMeetingType.valueOf(calendarSlotMeetingType.toString());
    }
}
