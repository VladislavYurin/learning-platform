package ru.mentor.mapper;

import static ru.mentor.mapper.UtilMapper.buildTimestamp;
import static ru.mentor.mapper.UtilMapper.calendarSlotMeetingTypeToSlotMeetingType;
import static ru.mentor.mapper.UtilMapper.calendarSlotTypeToSlotType;
import static ru.mentor.mapper.UtilMapper.userEntityRoleToUserInfoRole;
import static ru.mentor.mapper.UtilMapper.userInfoRoleToUserInfoDtoRole;

import com.google.protobuf.Timestamp;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.mentor.admin.AllTimeSlotsResponse;
import ru.mentor.admin.PageDetails;
import ru.mentor.calendar.MentorSlotInfo;
import ru.mentor.calendar.SlotMeetingType;
import ru.mentor.calendar.SlotType;
import ru.mentor.calendar.TimeSlotResponse;
import ru.mentor.calendar.UserInfo;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;

/**
 * Маппер для {@link MentorTimeSlotEntity}
 */
@Component
public class TimeSlotMapper {

    /**
     * Преобразовывает доменную сущность в DTO для ответа по gRPC.
     *
     * @param timeSlotEntity
     *         {@link MentorTimeSlotEntity}
     * @param rqUId
     *         UUID запроса
     *
     * @return {@link TimeSlotResponse}
     */
    public TimeSlotResponse entityToGrpcResponse(
            MentorTimeSlotEntity timeSlotEntity,
            String rqUId) {

        Timestamp startTime = buildTimestamp(timeSlotEntity.getStartTime());
        Timestamp endTime = buildTimestamp(timeSlotEntity.getEndTime());
        SlotType slotType = calendarSlotTypeToSlotType(timeSlotEntity.getSlotType());
        SlotMeetingType slotMeetingType = calendarSlotMeetingTypeToSlotMeetingType(timeSlotEntity.getSlotMeetingType());
        Timestamp createdAt = buildTimestamp(timeSlotEntity.getCreatedAt());

        return TimeSlotResponse.newBuilder()
                               .setRqUid(rqUId)
                               .setSlotId(timeSlotEntity.getId())
                               .setMentorId(timeSlotEntity.getMentorId())
                               .setStartTime(startTime)
                               .setEndTime(endTime)
                               .setSlotType(slotType)
                               .setSlotMeetingType(slotMeetingType)
                               .setMaxParticipants(timeSlotEntity.getMaxParticipants())
                               .setMeetingLink(timeSlotEntity.getMeetingLink())
                               .setDescription(timeSlotEntity.getDescription())
                               .setCreatedAt(createdAt)
                               .build();
    }

    /**
     * Маппит сущность пользователя {@link UserEntity} в DTO {@link UserInfo} для gRPC-ответа
     *
     * @param userEntity
     *         - сущность пользователя
     *
     * @return - ДТО {@link UserInfo} участник встречи
     */
    public UserInfo toUserInfoGrpcResponse(UserEntity userEntity) {

        UserInfo.Builder builder = UserInfo.newBuilder()
                                           .setId(userEntity.getId())
                                           .setUsername(userEntity.getUsername())
                                           .setRole(userEntityRoleToUserInfoRole(userEntity))
                                           .setFirstName(userEntity.getFirstName())
                                           .setLastName(userEntity.getLastName())
                                           .setTgNickname(userEntity.getTgNickname());

        if (userEntity.getTgChatId() != null) {
            builder.setTgChatId(userEntity.getTgChatId());
        }

        return builder.build();
    }

    /**
     * Преобразует сущность {@link MentorTimeSlotEntity} в ДТО {@link MentorSlotInfo} для
     * gRPC-ответа
     *
     * @param mentorTimeSlotInfo
     *         - сущность слота {@link MentorTimeSlotEntity}
     *
     * @return {@link MentorSlotInfo} - ДТО с данными слота и данными участников встречи
     */
    public MentorSlotInfo mentorTimeSlotEntityToMentorSlotInfo
    (TimeSlotResponse mentorTimeSlotInfo, List<UserInfo> timeSlotParticipants) {

        return MentorSlotInfo.newBuilder()
                             .setSlotInfo(mentorTimeSlotInfo)
                             .addAllParticipants(timeSlotParticipants)
                             .build();
    }

    /**
     * Преобразует gRPC - сущность {@link UserInfo} в ДТО {@link UserInfoDto}
     *
     * @param userInfo
     *         - информация о пользователе из gRPC-ответа
     *
     * @return - {@link UserInfoDto}
     */
    public UserInfoDto grpcToUserInfoDto(UserInfo userInfo) {
        return UserInfoDto.builder()
                          .id(userInfo.getId())
                          .username(userInfo.getUsername())
                          .role(userInfoRoleToUserInfoDtoRole(userInfo))
                          .firstName(userInfo.getFirstName())
                          .lastName(userInfo.getLastName())
                          .tgNickname(userInfo.getTgNickname())
                          .tgChatId(userInfo.hasTgChatId() ? userInfo.getTgChatId() : null)
                          .build();
    }

    /**
     * Преобразует страницу сущностей слотов в gRPC-объект, содержащий слоты
     *
     * @param mentorTimeSlotEntityPage
     *         объект {@link Page} со списком {@link MentorTimeSlotEntity}
     *
     * @return gRPC-объект {@link AllTimeSlotsResponse}
     */
    public AllTimeSlotsResponse mapMentorTimeSlotEntityPageToAllTimeSlotsResponse
    (Page<MentorSlotInfo> mentorTimeSlotEntityPage) {

        return AllTimeSlotsResponse.newBuilder()
                                   .setPageDetails(extractPageDetailsFromMentorTimeSlotEntityPage(
                                           mentorTimeSlotEntityPage))
                                   .addAllTimeSlots(mapMentorTimeSlotEntityPageToMentorSlotInfoList(
                                           mentorTimeSlotEntityPage))
                                   .build();
    }

    private List<MentorSlotInfo> mapMentorTimeSlotEntityPageToMentorSlotInfoList
            (Page<MentorSlotInfo> mentorTimeSlotEntityPage) {

        return mentorTimeSlotEntityPage.stream().toList();
    }

    private PageDetails extractPageDetailsFromMentorTimeSlotEntityPage
            (Page<MentorSlotInfo> mentorTimeSlotEntities) {

        return PageDetails.newBuilder()
                          .setPage(mentorTimeSlotEntities.getNumber())
                          .setSize(mentorTimeSlotEntities.getSize())
                          .setTotalPages(mentorTimeSlotEntities.getTotalPages())
                          .setTotalElements(mentorTimeSlotEntities.getTotalElements())
                          .build();
    }

}