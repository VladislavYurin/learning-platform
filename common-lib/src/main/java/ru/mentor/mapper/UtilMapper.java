package ru.mentor.mapper;

import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import ru.mentor.common.AuthorResponse;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.common.UserInfo;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.UserEntity;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UtilMapper {

    // ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒→ USER CONVERSIONS▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒

    /**
     * Преобразует сущность пользователя в DTO информацию о пользователе
     *
     * @param entity сущность пользователя для преобразования
     * @return DTO информация о пользователе
     */
    @Named("userEntityToUserInfoDto")
    UserInfoDto userEntityToUserInfoDto(UserEntity entity);

    /**
     * Преобразует DTO информацию о пользователе в сущность пользователя
     *
     * @param userInfoDto DTO информация о пользователе для преобразования
     * @return сущность пользователя
     */
    @Named("userInfoDtoToUserEntity")
    UserEntity userInfoDtoToUserEntity(UserInfoDto userInfoDto);

    /**
     * Маппит сущность пользователя {@link UserEntity} в DTO {@link UserInfo} для gRPC-ответа
     *
     * @param userEntity - сущность пользователя
     * @return - ДТО {@link UserInfo} участник встречи
     */
    @Named("userEntityToUserInfo")
    @Mapping(target = "role",
            expression = "java(userEntityToRole(userEntity))")
    UserInfo userEntityToUserInfo(UserEntity userEntity);

    /**
     * Преобразует gRPC - сущность {@link UserInfo} в ДТО {@link UserInfoDto}
     *
     * @param userInfo - информация о пользователе из gRPC-ответа
     * @return - {@link UserInfoDto}
     */
    @Named("userInfoToUserInfoDto")
    @Mapping(target = "role",
            expression = "java(userInfoToRole(userInfo))")
    UserInfoDto userInfoToUserInfoDto(UserInfo userInfo);

    @Named("authorResponseToUserInfoDto")
    @Mapping(target = "id", source = "userId")
    @Mapping(target = "role", expression = "java(ru.mentor.constant.Role.MENTOR)")
    @Mapping(target = "tgChatId", expression = "java(author.getTgChatId() != 0 ? author.getTgChatId() : null)")
    UserInfoDto authorResponseToUserInfoDto(AuthorResponse author);

    @Named("userEntityToAuthorResponse")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "tgChatId", expression = "java(userEntity.getTgChatId() != null ? userEntity.getTgChatId() : 0L)")
    AuthorResponse userEntityToAuthorResponse(UserEntity userEntity);

    @Named("userEntitySetToUserInfoList")
    default List<UserInfo> userEntitySetToUserInfoList(Set<UserEntity> participants) {
        if (participants == null) return new ArrayList<>();
        return participants.stream()
                .map(this::userEntityToUserInfo)
                .collect(Collectors.toList());
    }

    @Named("userInfoListToUserInfoDtoList")
    default List<UserInfoDto> userInfoListToUserInfoDtoList(List<UserInfo> participants) {
        if (participants == null) return new ArrayList<>();
        return participants.stream()
                .map(this::userInfoToUserInfoDto)
                .collect(Collectors.toList());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "userAvatarKey", ignore = true)
    void updateUserFromDto(UserInfoDto infoDto, @MappingTarget UserEntity entity);

    // ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒→ Role CONVERSIONS▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒

    /**
     * Преобразует роль из {@link UserEntity} в {@link ru.mentor.common.Role} для gRPC.
     * Используется при маппинге пользовательских данных для gRPC-ответов.
     */
    @Named("userEntityToRole")
    default ru.mentor.common.Role userEntityToRole(UserEntity userEntity) {
        return ru.mentor.common.Role.valueOf(userEntity.getRole().name());
    }

    /**
     * Преобразует роль из {@link UserInfo} (gRPC) в {@link ru.mentor.constant.Role} для внутреннего DTO.
     * Используется при конвертации gRPC-данных обратно во внутреннюю модель.
     */
    @Named("userInfoToRole")
    default ru.mentor.constant.Role userInfoToRole(UserInfo userInfo) {
        return ru.mentor.constant.Role.valueOf(userInfo.getRole().name());
    }

    // ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒→ DateTime CONVERSIONS▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒

    /**
     * Преобразует {@link Timestamp} в {@link LocalDateTime} (UTC).
     */
    @Named("timestampToLocalDateTime")
    default LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) return null;
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    /**
     * Преобразует {@link LocalDateTime} (UTC) в {@link Timestamp}.
     */
    @Named("localDateTimeToTimestamp")
    default Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    // ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒→ SlotType CONVERSIONS▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒

    /**
     * Преобразует {@link CalendarSlotType} (внутренний) в {@link SlotType} для gRPC.
     * Используется при отправке данных о типах слотов в gRPC-сервисы.
     */
    @Named("calendarSlotTypeToSlotType")
    default ru.mentor.common.SlotType calendarSlotTypeToSlotType(CalendarSlotType calendarSlotType) {
        return SlotType.valueOf(calendarSlotType.toString());
    }

    /**
     * Преобразует {@link CalendarSlotMeetingType} (внутренний) в {@link SlotMeetingType} для gRPC.
     * Используется при отправке данных о типах встреч в gRPC-сервисы.
     */
    @Named("calendarSlotMeetingTypeToSlotMeetingType")
    default ru.mentor.common.SlotMeetingType calendarSlotMeetingTypeToSlotMeetingType(
            CalendarSlotMeetingType calendarSlotMeetingType) {
        return SlotMeetingType.valueOf(calendarSlotMeetingType.toString());
    }

    /**
     * Преобразует {@link SlotType} (gRPC) в {@link CalendarSlotType} (внутренний).
     * Используется при получении данных о типах слотов из gRPC-сервисов.
     */
    @Named("slotTypeToCalendarSlotType")
    default ru.mentor.constant.CalendarSlotType slotTypeToCalendarSlotType(SlotType slotType) {
        return CalendarSlotType.valueOf(slotType.toString());
    }

    /**
     * Преобразует {@link SlotMeetingType} (gRPC) в {@link CalendarSlotMeetingType} (внутренний).
     * Используется при получении данных о типах встреч из gRPC-сервисов.
     */
    @Named("slotMeetingTypeToCalendarSlotMeetingType")
    default ru.mentor.constant.CalendarSlotMeetingType slotMeetingTypeToCalendarSlotMeetingType(
            SlotMeetingType slotMeetingType) {
        return CalendarSlotMeetingType.valueOf(slotMeetingType.toString());
    }

}
