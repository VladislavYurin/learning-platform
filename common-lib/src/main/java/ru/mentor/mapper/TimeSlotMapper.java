package ru.mentor.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import ru.mentor.common.AllTimeSlotsResponse;
import ru.mentor.common.BookTimeSlotRequest;
import ru.mentor.common.CancelTimeSlotRequest;
import ru.mentor.common.CancelTimeSlotResponse;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.Header;
import ru.mentor.common.MentorSlotInfo;
import ru.mentor.common.MentorSlotsInfoRequest;
import ru.mentor.common.MentorSlotsInfoResponse;
import ru.mentor.common.PageDetails;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.common.UserInfo;
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
@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TimeSlotMapper {

    /**
     * Преобразовывает объект, полученный через gRPC в DTO для отправки клиенту.
     *
     * @param grpcResponse
     *         {@link TimeSlotResponse}
     *
     * @return {@link MentorTimeSlotDto}
     */
    @Named("grpcResponseToDto")
    @Mapping(target = "id", source = "slotId")
    @Mapping(target = "startTime",
            qualifiedByName = "timestampToLocalDateTime")
    @Mapping(target = "endTime",
            qualifiedByName = "timestampToLocalDateTime")
    @Mapping(target = "slotType",
            qualifiedByName = "slotTypeToCalendarSlotType")
    @Mapping(target = "slotMeetingType",
            qualifiedByName = "slotMeetingTypeToCalendarSlotMeetingType")
    @Mapping(target = "createdAt",
            qualifiedByName = "timestampToLocalDateTime")
    MentorTimeSlotDto grpcResponseToDto(TimeSlotResponse grpcResponse);

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
    @Mapping(target = "header", source = "header")
    @Mapping(target = "mentorId", source = "user.id")
    @Mapping(target = "startTime", source = "createRequest.startTime",
            qualifiedByName = "buildTimestamp")
    @Mapping(target = "endTime", source = "createRequest.endTime",
            qualifiedByName = "buildTimestamp")
    @Mapping(target = "slotType", source = "createRequest.slotType",
            qualifiedByName = "calendarSlotTypeToSlotType")
    @Mapping(target = "slotMeetingType", source = "createRequest.slotMeetingType",
            qualifiedByName = "calendarSlotMeetingTypeToSlotMeetingType")
    @Mapping(target = "maxParticipants", source = "createRequest.maxParticipants")
    @Mapping(target = "meetingLink", source = "createRequest.meetingLink")
    @Mapping(target = "description", source = "createRequest.description")
    CreateTimeSlotRequest requestCreateToGrpcDto(
            MentorTimeSlotCreateRequest createRequest,
            Header header, UserEntity user);

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
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "mentor", source = "mentor")
    @Mapping(target = "startTime", source = "request.startTime",
            qualifiedByName = "timestampToLocalDateTime")
    @Mapping(target = "endTime", source = "request.endTime",
            qualifiedByName = "timestampToLocalDateTime")
    @Mapping(target = "slotType", source = "request.slotType",
            qualifiedByName = "slotTypeToCalendarSlotType")
    @Mapping(target = "slotMeetingType", source = "request.slotMeetingType",
            qualifiedByName = "slotMeetingTypeToCalendarSlotMeetingType")
    @Mapping(target = "maxParticipants", source = "request.maxParticipants")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "meetingLink", source = "request.meetingLink")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "meetingParticipants", ignore = true)
    MentorTimeSlotEntity grpcCreateRequestToEntity(
            CreateTimeSlotRequest request,
            UserEntity mentor);

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
    @Named("entityToGrpcResponse")
    @Mapping(target = "requestId", source = "requestId")
    @Mapping(target = "slotId", source = "timeSlotEntity.id")
    @Mapping(target = "mentorId", source = "timeSlotEntity.mentor.id")
    @Mapping(target = "startTime", source = "timeSlotEntity.startTime",
            qualifiedByName = "buildTimestamp")
    @Mapping(target = "endTime", source = "timeSlotEntity.endTime",
            qualifiedByName = "buildTimestamp")
    @Mapping(target = "slotType", source = "timeSlotEntity.slotType",
            qualifiedByName = "calendarSlotTypeToSlotType")
    @Mapping(target = "slotMeetingType", source = "timeSlotEntity.slotMeetingType",
            qualifiedByName = "calendarSlotMeetingTypeToSlotMeetingType")
    @Mapping(target = "maxParticipants", source = "timeSlotEntity.maxParticipants")
    @Mapping(target = "meetingLink", source = "timeSlotEntity.meetingLink")
    @Mapping(target = "description", source = "timeSlotEntity.description")
    @Mapping(target = "createdAt", source = "timeSlotEntity.createdAt",
            qualifiedByName = "buildTimestamp")
    @Mapping(target = "isActive", source = "timeSlotEntity.isActive")
    TimeSlotResponse entityToGrpcResponse(
            MentorTimeSlotEntity timeSlotEntity,
            String requestId);

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
    @Mapping(target = "header", source = "header")
    @Mapping(target = "slotId", source = "slotId")
    @Mapping(target = "userId", source = "userId")
    BookTimeSlotRequest toGrpcBookTimeSlotRequest(Header header, long slotId, long userId);

    /**
     * Маппит уникальный ID запроса, слота и пользователя в объект запроса на отмену слота для отправки по gRPC.
     *
     * @param header заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param slotId ID слота, который бронируется
     * @param userId ID пользователя, который бронирует
     * @return {@link CancelTimeSlotRequest}
     */
    @Mapping(target = "header", source = "header")
    @Mapping(target = "slotId", source = "slotId")
    @Mapping(target = "userId", source = "userId")
    CancelTimeSlotRequest toGrpcCancelTimeSlotRequest(Header header , Long slotId, Long userId);

    /**
     * Маппит уникальный ID запроса, в gRPC-ответ.
     *
     * @param rqUid UUID запроса на отмену слота
     * @return {@link CancelTimeSlotResponse}
     */
    CancelTimeSlotResponse toGrpcCancelTimeSlotResponse(String rqUid);


    default String grpcCancelTimeSlotResponseToDto(CancelTimeSlotResponse cancelTimeSlotResponse) {
        return cancelTimeSlotResponse.getRqUid();
    }

    /**
     * Маппит сущность пользователя {@link UserEntity} в DTO {@link UserInfo} для gRPC-ответа
     *
     * @param userEntity
     *         - сущность пользователя
     *
     * @return - ДТО {@link UserInfo} участник встречи
     */
    @Named("toUserInfoGrpcResponse")
    @Mapping(target = "role", source = "userEntity",
            qualifiedByName = "userEntityRoleToUserInfoRole")
    UserInfo toUserInfoGrpcResponse(UserEntity userEntity);

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
    default MentorSlotInfo entityMentorTimeSlotToMentorSlotInfo(
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
    default MentorSlotInfoDto toDtoMentorSlotInfo(MentorSlotInfo mentorSlotInfo) {

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
    @Mapping(target = "header", source = "header")
    @Mapping(target = "mentorId", source = "mentorId")
    MentorSlotsInfoRequest toMentorSlotsInfoGrpcRequest(Long mentorId, Header header);

    /**
     * Преобразует список из gRPC-сущностей слотов менторов в список ДТО слотов
     *
     * @param mentorSlotsList
     *         - список слотов
     *
     * @return - список ДТО {@link MentorSlotInfoDto}
     */
    default List<MentorSlotInfoDto> toSlotInfoDtoList(List<MentorSlotInfo> mentorSlotsList) {
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
    @Named("grpcToUserInfoDto")
    @Mapping(target = "role", source = "userInfo",
            qualifiedByName = "userInfoRoleToUserInfoDtoRole")
    UserInfoDto grpcToUserInfoDto(UserInfo userInfo);

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
    default MentorSlotsInfoResponse convertToMentorSlotsInfoResponse(
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
    default AllTimeSlotsResponse mapMentorTimeSlotEntityPageToAllTimeSlotsResponse
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
    default List<MentorSlotInfoDto> mapGrpcAllTimeSlotsResponseToMentorSlotInfoDtoList
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
    default List<MentorTimeSlotInfoForUserDto> toSlotInfoForUserList(List<MentorSlotInfo> slotsInfoList) {
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