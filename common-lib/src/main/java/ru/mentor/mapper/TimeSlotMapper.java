package ru.mentor.mapper;

import static ru.mentor.mapper.UtilMapper.buildTimestamp;
import static ru.mentor.mapper.UtilMapper.calendarSlotMeetingTypeToSlotMeetingType;
import static ru.mentor.mapper.UtilMapper.calendarSlotTypeToSlotType;
import static ru.mentor.mapper.UtilMapper.slotMeetingTypeToCalendarSlotMeetingType;
import static ru.mentor.mapper.UtilMapper.slotTypeToCalendarSlotType;
import static ru.mentor.mapper.UtilMapper.timestampToLocalDateTime;
import static ru.mentor.mapper.UtilMapper.userEntityRoleToUserInfoRole;
import static ru.mentor.mapper.UtilMapper.userInfoRoleToUserInfoDtoRole;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.BookTimeSlotRequest;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.Header;
import ru.mentor.common.MentorSlotInfo;
import ru.mentor.common.MentorSlotsInfoRequest;
import ru.mentor.common.MentorSlotsInfoResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.common.SlotMeetingType;
import ru.mentor.common.SlotType;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.common.UserInfo;
import ru.mentor.constant.CalendarSlotMeetingType;
import ru.mentor.constant.CalendarSlotType;
import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.dto.MentorTimeSlotInfoForUserDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;

/**
 * Маппер для {@link MentorTimeSlotEntity}
 */
@Component
@RequiredArgsConstructor
public class TimeSlotMapper {

    /**
     * Преобразовывает объект, полученный через gRPC в DTO для отправки клиенту.
     *
     * @param grpcResponse
     *         {@link TimeSlotResponse}
     *
     * @return {@link MentorTimeSlotDto}
     */
    public MentorTimeSlotDto grpcResponseToDto(TimeSlotResponse grpcResponse) {

        Timestamp grpcStartTime = grpcResponse.getStartTime();
        LocalDateTime startTime = timestampToLocalDateTime(grpcStartTime);

        Timestamp grpcEndTime = grpcResponse.getEndTime();
        LocalDateTime endTime = timestampToLocalDateTime(grpcEndTime);

        CalendarSlotType slotType = slotTypeToCalendarSlotType(grpcResponse.getSlotType());
        CalendarSlotMeetingType slotMeetingType = slotMeetingTypeToCalendarSlotMeetingType(
                grpcResponse.getSlotMeetingType());

        Timestamp grpcCreatedAt = grpcResponse.getCreatedAt();
        LocalDateTime createdAt = timestampToLocalDateTime(grpcCreatedAt);

        return MentorTimeSlotDto.builder()
                                .requestId(grpcResponse.getRequestId())
                                .id(grpcResponse.getSlotId())
                                .mentorId(grpcResponse.getMentorId())
                                .startTime(startTime)
                                .endTime(endTime)
                                .slotType(slotType)
                                .slotMeetingType(slotMeetingType)
                                .maxParticipants(grpcResponse.getMaxParticipants())
                                .meetingLink(grpcResponse.getMeetingLink())
                                .description(grpcResponse.getDescription())
                                .createdAt(createdAt)
                                .build();
    }

    /**
     * Преобразовывает объект, полученный от клиента в DTO для отправки по gRPC.
     *
     * @param createRequest
     *         {@link MentorTimeSlotCreateRequest}
     * @param header
     *         заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param user
     *         объект {@link UserEntity}
     *
     * @return {@link CreateTimeSlotRequest}
     */
    public CreateTimeSlotRequest requestCreateToGrpcDto(
            MentorTimeSlotCreateRequest createRequest,
            Header header, UserEntity user) {

        Timestamp startTime = buildTimestamp(createRequest.getStartTime());

        Timestamp endTime = buildTimestamp(createRequest.getEndTime());

        SlotType slotType = calendarSlotTypeToSlotType(createRequest.getSlotType());

        SlotMeetingType slotMeetingType = calendarSlotMeetingTypeToSlotMeetingType(createRequest.getSlotMeetingType());

        return CreateTimeSlotRequest.newBuilder()
                                    .setHeader(header)
                                    .setMentorId(user.getId())
                                    .setStartTime(startTime)
                                    .setEndTime(endTime)
                                    .setSlotType(slotType)
                                    .setSlotMeetingType(slotMeetingType)
                                    .setMaxParticipants(createRequest.getMaxParticipants())
                                    .setMeetingLink(createRequest.getMeetingLink())
                                    .setDescription(createRequest.getDescription())
                                    .build();
    }

    /**
     * Преобразовывает DTO, полученный по rRPC в доменную сущность.
     *
     * @param request
     *         {@link CreateTimeSlotRequest}
     * @param mentor
     *         {@link UserEntity}
     *
     * @return {@link MentorTimeSlotEntity}
     */
    public MentorTimeSlotEntity grpcCreateRequestToEntity(
            CreateTimeSlotRequest request,
            UserEntity mentor) {

        Timestamp startTimestamp = request.getStartTime();
        LocalDateTime startTime = timestampToLocalDateTime(startTimestamp);

        Timestamp endTimestamp = request.getEndTime();
        LocalDateTime endTime = timestampToLocalDateTime(endTimestamp);

        CalendarSlotType slotType = slotTypeToCalendarSlotType(request.getSlotType());

        CalendarSlotMeetingType slotMeetingType = slotMeetingTypeToCalendarSlotMeetingType(request.getSlotMeetingType());

        return MentorTimeSlotEntity.builder()
                                   .mentor(mentor)
                                   .startTime(startTime)
                                   .endTime(endTime)
                                   .description(request.getDescription())
                                   .slotType(slotType)
                                   .slotMeetingType(slotMeetingType)
                                   .maxParticipants(request.getMaxParticipants())
                                   .meetingLink(request.getMeetingLink())
                                   .isActive(true)
                                   .build();
    }

    /**
     * Преобразовывает доменную сущность в DTO для ответа по gRPC.
     *
     * @param timeSlotEntity
     *         {@link MentorTimeSlotEntity}
     * @param requestId
     *         UUID запроса
     *
     * @return {@link TimeSlotResponse}
     */
    public TimeSlotResponse entityToGrpcResponse(
            MentorTimeSlotEntity timeSlotEntity,
            String requestId) {

        Timestamp startTime = buildTimestamp(timeSlotEntity.getStartTime());

        Timestamp endTime = buildTimestamp(timeSlotEntity.getEndTime());

        SlotType slotType = calendarSlotTypeToSlotType(timeSlotEntity.getSlotType());

        SlotMeetingType slotMeetingType = calendarSlotMeetingTypeToSlotMeetingType(timeSlotEntity.getSlotMeetingType());

        Timestamp createdAt = buildTimestamp(timeSlotEntity.getCreatedAt());

        return TimeSlotResponse.newBuilder()
                               .setRequestId(requestId)
                               .setSlotId(timeSlotEntity.getId())
                               .setMentorId(timeSlotEntity.getMentor().getId())
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
     * Маппит уникальный ID запроса, слота и пользователя в объект запроса на бронирование сбота для
     * отправки по gRPC.
     *
     * @param header
     *         заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param slotId
     *         ID слота, который бронируется
     * @param userId
     *         ID пользователя, который бронирует
     *
     * @return {@link BookTimeSlotRequest}
     */
    public BookTimeSlotRequest toGrpcBookTimeSlotRequest(Header header, long slotId, long userId) {
        return BookTimeSlotRequest.newBuilder()
                                  .setHeader(header)
                                  .setSlotId(slotId)
                                  .setUserId(userId)
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
     * @param mentorTimeSlotEntity
     *         - сущность слота {@link MentorTimeSlotEntity}
     * @param requestId
     *         - уникальный идентификатор запроса
     *
     * @return {@link MentorSlotInfo} - ДТО с данными слота и данными участников встречи
     */
    public MentorSlotInfo entityMentorTimeSlotToMentorSlotInfo(
            MentorTimeSlotEntity mentorTimeSlotEntity,
            String requestId) {

        TimeSlotResponse slotInfo = entityToGrpcResponse(mentorTimeSlotEntity, requestId);

        List<UserInfo> participants = mentorTimeSlotEntity.getMeetingParticipants().stream()
                                                          .map(this::toUserInfoGrpcResponse)
                                                          .toList();

        return MentorSlotInfo.newBuilder()
                             .setSlotInfo(slotInfo)
                             .addAllParticipants(participants)
                             .build();
    }

    /**
     * Преобразует gRPC ответ {@link MentorSlotInfo} в ДТО {@link MentorSlotInfoDto} для выдачи в
     * контроллер
     *
     * @param mentorSlotInfo
     *         - gRPC-сущность с данными о слоте и о пользователях
     *
     * @return - ДТО для контроллера
     */
    public MentorSlotInfoDto toDtoMentorSlotInfo(MentorSlotInfo mentorSlotInfo) {

        MentorTimeSlotDto mentorTimeSlotDto =
                grpcResponseToDto(mentorSlotInfo.getSlotInfo());

        List<UserInfoDto> listOfParticipantsDto = mentorSlotInfo.getParticipantsList().stream()
                                                                .map(this::grpcToUserInfoDto)
                                                                .toList();

        return new MentorSlotInfoDto(mentorTimeSlotDto, listOfParticipantsDto);
    }

    /**
     * Формирует gRPC запрос для получения всех слотов ментора
     *
     * @param mentorId
     *         - ID ментора
     * @param header
     *         заголовок gRPC-запроса (requestId/nodeId/apiKey)
     *
     * @return - объект запроса {@link MentorSlotsInfoRequest}
     */
    public MentorSlotsInfoRequest toMentorSlotsInfoGrpcRequest(Long mentorId, Header header) {
        return MentorSlotsInfoRequest.newBuilder()
                                     .setHeader(header)
                                     .setMentorId(mentorId)
                                     .build();
    }

    /**
     * Преобразует список из gRPC-сущностей слотов менторов в список ДТО слотов
     *
     * @param mentorSlotsList
     *         - список слотов
     *
     * @return - список ДТО {@link MentorSlotInfoDto}
     */
    public List<MentorSlotInfoDto> toSlotInfoDtoList(List<MentorSlotInfo> mentorSlotsList) {
        return mentorSlotsList.stream()
                              .map(this::toDtoMentorSlotInfo)
                              .toList();
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
     * Конвертирует список сущностей {@link MentorTimeSlotEntity} в gRPC-ответ
     * {@link MentorSlotsInfoResponse}
     *
     * @param mentorSlots
     *         - список слотов ментора
     * @param requestId
     *         - идентификатор запроса
     *
     * @return готовый gRPC-ответ
     */
    public MentorSlotsInfoResponse convertToMentorSlotsInfoResponse(
            List<MentorTimeSlotEntity> mentorSlots,
            String requestId) {
        List<MentorSlotInfo> mentorSlotsWithUsersInfo = mentorSlots.stream()
                                                                   .map(slot -> entityMentorTimeSlotToMentorSlotInfo(
                                                                           slot,
                                                                           requestId
                                                                   ))
                                                                   .toList();

        return MentorSlotsInfoResponse.newBuilder()
                                      .addAllSlots(mentorSlotsWithUsersInfo)
                                      .build();
    }

    /**
     * Преобразует страницу сущностей слотов в gRPC-объект, содержащий слоты
     *
     * @param mentorTimeSlotEntities
     *         объект {@link Page} со списком {@link MentorTimeSlotEntity}
     * @param requestId
     *         сквозной UUID запроса
     *
     * @return gRPC-объект {@link AllTimeSlotsResponse}
     */
    public AllTimeSlotsResponse mapMentorTimeSlotEntityPageToAllTimeSlotsResponse
    (Page<MentorTimeSlotEntity> mentorTimeSlotEntities, String requestId) {

        return AllTimeSlotsResponse.newBuilder()
                                   .setPageDetails(extractPageDetailsFromMentorTimeSlotEntityPage(
                                           mentorTimeSlotEntities))
                                   .addAllTimeSlots(mapMentorTimeSlotEntityPageToMentorSlotInfoList(
                                           mentorTimeSlotEntities,
                                           requestId
                                   ))
                                   .build();
    }

    /**
     * Преобразует gRPC-объект, содержащий список слотов в список DTO слотов.
     *
     * @param allTimeSlots
     *         gRPC-объект со списком слотов
     *
     * @return список {@link MentorSlotInfoDto}
     */
    public List<MentorSlotInfoDto> mapGrpcAllTimeSlotsResponseToMentorSlotInfoDtoList
    (AllTimeSlotsResponse allTimeSlots) {
        return allTimeSlots.getTimeSlotsList().stream()
                           .map(this::toDtoMentorSlotInfo)
                           .toList();
    }

    private List<MentorSlotInfo> mapMentorTimeSlotEntityPageToMentorSlotInfoList
            (Page<MentorTimeSlotEntity> mentorTimeSlotEntities, String requestId) {

        return mentorTimeSlotEntities.stream()
                                     .map(entity -> entityMentorTimeSlotToMentorSlotInfo(
                                             entity,
                                             requestId
                                     ))
                                     .toList();
    }

    private PageDetails extractPageDetailsFromMentorTimeSlotEntityPage
            (Page<MentorTimeSlotEntity> mentorTimeSlotEntities) {

        return PageDetails.newBuilder()
                          .setPage(mentorTimeSlotEntities.getNumber())
                          .setSize(mentorTimeSlotEntities.getSize())
                          .setTotalPages(mentorTimeSlotEntities.getTotalPages())
                          .setTotalElements(mentorTimeSlotEntities.getTotalElements())
                          .build();
    }

    /**
     * Преобразует gRPC - ответ {@link List<MentorSlotInfo>} с информацией о слотах ментора
     * в ДТО {@link MentorTimeSlotInfoForUserDto} для возврата ученику
     *
     * @param slotsInfoList
     *         gRPC ответ с данными о слотах ментора
     *
     * @return {@link List<MentorTimeSlotInfoForUserDto>} - список ДТО с информацией о слотах для ученика
     */
    public List<MentorTimeSlotInfoForUserDto> toSlotInfoForUserList(List<MentorSlotInfo> slotsInfoList) {
        return slotsInfoList.stream()
                            .map(slotInfo -> {
                                return MentorTimeSlotInfoForUserDto.builder()
                                                                   .isSlotFull(
                                                                           slotInfo.getParticipantsList()
                                                                                   .size()
                                                                                   == slotInfo.getSlotInfo()
                                                                                              .getMaxParticipants()
                                                                   )
                                                                   .mentorTimeSlotDto(this.grpcResponseToDto(
                                                                           slotInfo.getSlotInfo()))
                                                                   .build();
                            })
                            .toList();
    }

}