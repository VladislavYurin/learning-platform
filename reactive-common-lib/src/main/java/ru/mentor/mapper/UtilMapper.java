package ru.mentor.mapper;

import com.google.protobuf.Timestamp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import ru.mentor.common.Role;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.entity.UserEntity;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UtilMapper {

    @Named("userEntityRoleToUserInfoRole")
    default Role userEntityRoleToUserInfoRole(UserEntity userEntity) {
        return Role.valueOf(userEntity.getRole().name());
    }

    @Named("localDateTimeToTimestamp")
    default Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
        return Timestamp.newBuilder()
                .setSeconds(localDateTime.toEpochSecond(ZoneOffset.UTC))
                .build();
    }

    @Named("getLocalDateTimeNow")
    default LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now();
    }

    @Named("calendarSlotTypeToSlotType")
    default SlotType calendarSlotTypeToSlotType(CalendarSlotType calendarSlotType) {
        return SlotType.valueOf(calendarSlotType.toString());
    }

    @Named("calendarSlotMeetingTypeToSlotMeetingType")
    default SlotMeetingType calendarSlotMeetingTypeToSlotMeetingType(
            CalendarSlotMeetingType calendarSlotMeetingType) {
        return SlotMeetingType.valueOf(calendarSlotMeetingType.toString());
    }
}