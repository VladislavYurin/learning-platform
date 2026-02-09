package ru.mentor.mapper;

import com.google.protobuf.Timestamp;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import ru.mentor.common.*;

import ru.mentor.dto.MentorSlotInfoDto;
import ru.mentor.dto.MentorTimeSlotCreateRequest;
import ru.mentor.dto.MentorTimeSlotDto;
import ru.mentor.dto.MentorTimeSlotInfoForUserDto;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;


import java.util.List;

/**
 * Маппер для {@link MentorTimeSlotEntity}
 */
@Mapper(componentModel = "spring",
        uses = UtilMapper.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TimeSlotMapper {

    @Autowired
    protected UtilMapper utilMapper;

    /**
     * Преобразовывает объект, полученный через gRPC в DTO для отправки клиенту.
     *
     * @param grpcResponse {@link TimeSlotResponse}
     * @return {@link MentorTimeSlotDto}
     */
    @Named("timeSlotResponseToMentorTimeSlotDto")
    @Mapping(target = "id", source = "slotId")
    @Mapping(target = "mentorId", source = "mentorId")
    @Mapping(target = "requestId", source = "requestId")
    @Mapping(target = "startTime", source = "startTime",
            qualifiedByName = "timestampToLocalDateTime")
    @Mapping(target = "endTime", source = "endTime",
            qualifiedByName = "timestampToLocalDateTime")
    @Mapping(target = "slotType", source = "slotType",
            qualifiedByName = "slotTypeToCalendarSlotType")
    @Mapping(target = "slotMeetingType", source = "slotMeetingType",
            qualifiedByName = "slotMeetingTypeToCalendarSlotMeetingType")
    @Mapping(target = "maxParticipants", source = "maxParticipants")
    @Mapping(target = "meetingLink", source = "meetingLink")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdAt", source = "createdAt",
            qualifiedByName = "timestampToLocalDateTime")
    public abstract MentorTimeSlotDto timeSlotResponseToMentorTimeSlotDto(TimeSlotResponse grpcResponse);

    /**
     * Преобразовывает объект, полученный от клиента в DTO для отправки по gRPC.
     *
     * @param createRequest {@link MentorTimeSlotCreateRequest}
     * @param header        заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param user          объект {@link UserEntity}
     * @return {@link CreateTimeSlotRequest}
     */
    public CreateTimeSlotRequest toCreateTimeSlotRequest(MentorTimeSlotCreateRequest createRequest,
                                                         Header header,
                                                         UserEntity user) {

        Timestamp startTime = utilMapper.localDateTimeToTimestamp(createRequest.getStartTime());

        Timestamp endTime = utilMapper.localDateTimeToTimestamp(createRequest.getEndTime());

        SlotType slotType = utilMapper.calendarSlotTypeToSlotType(createRequest.getSlotType());

        SlotMeetingType slotMeetingType = utilMapper.calendarSlotMeetingTypeToSlotMeetingType(
                createRequest.getSlotMeetingType());

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
     * @param request {@link CreateTimeSlotRequest}
     * @param mentor  {@link UserEntity}
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
    public abstract MentorTimeSlotEntity toMentorTimeSlotEntity(CreateTimeSlotRequest request,
                                                                UserEntity mentor);

    /**
     * Преобразовывает доменную сущность в DTO для ответа по gRPC.
     *
     * @param timeSlotEntity {@link MentorTimeSlotEntity}
     * @param requestId      UUID запроса
     * @return {@link TimeSlotResponse}
     */
    @Named("toTimeSlotResponse")
    @Mapping(target = "requestId", source = "requestId")
    @Mapping(target = "slotId", source = "timeSlotEntity.id")
    @Mapping(target = "mentorId", source = "timeSlotEntity.mentor.id")
    @Mapping(target = "startTime", source = "timeSlotEntity.startTime",
            qualifiedByName = "localDateTimeToTimestamp")
    @Mapping(target = "endTime", source = "timeSlotEntity.endTime",
            qualifiedByName = "localDateTimeToTimestamp")
    @Mapping(target = "slotType", source = "timeSlotEntity.slotType",
            qualifiedByName = "calendarSlotTypeToSlotType")
    @Mapping(target = "slotMeetingType", source = "timeSlotEntity.slotMeetingType",
            qualifiedByName = "calendarSlotMeetingTypeToSlotMeetingType")
    @Mapping(target = "maxParticipants", source = "timeSlotEntity.maxParticipants")
    @Mapping(target = "meetingLink", source = "timeSlotEntity.meetingLink")
    @Mapping(target = "description", source = "timeSlotEntity.description")
    @Mapping(target = "createdAt", source = "timeSlotEntity.createdAt",
            qualifiedByName = "localDateTimeToTimestamp")
    public abstract TimeSlotResponse toTimeSlotResponse(MentorTimeSlotEntity timeSlotEntity,
                                                        String requestId);

    /**
     * Маппит уникальный ID запроса, слота и пользователя в объект запроса на бронирование сбота для
     * отправки по gRPC.
     *
     * @param header заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param slotId ID слота, который бронируется
     * @param userId ID пользователя, который бронирует
     * @return {@link BookTimeSlotRequest}
     */
    public BookTimeSlotRequest toBookTimeSlotRequest(Header header,
                                                     long slotId,
                                                     long userId) {
        return BookTimeSlotRequest.newBuilder()
                .setHeader(header)
                .setSlotId(slotId)
                .setUserId(userId)
                .build();
    }

    /**
     * Маппит уникальный ID запроса, слота и пользователя в объект запроса на отмену слота для отправки по gRPC.
     *
     * @param header заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @param slotId ID слота, который бронируется
     * @param userId ID пользователя, который бронирует
     * @return {@link CancelTimeSlotRequest}
     */
    public CancelTimeSlotRequest toGrpcCancelTimeSlotRequest(Header header,
                                                             Long slotId,
                                                             Long userId) {
        return CancelTimeSlotRequest.newBuilder()
                .setHeader(header)
                .setSlotId(slotId)
                .setUserId(userId)
                .build();
    }

    /**
     * Маппит уникальный ID запроса, в gRPC-ответ.
     *
     * @param rqUid UUID запроса на отмену слота
     * @return {@link CancelTimeSlotResponse}
     */
    public CancelTimeSlotResponse toGrpcCancelTimeSlotResponse(String rqUid) {
        return CancelTimeSlotResponse.newBuilder()
                .setRqUid(rqUid)
                .build();
    }


    public String grpcCancelTimeSlotResponseToDto(CancelTimeSlotResponse cancelTimeSlotResponse) {
        return cancelTimeSlotResponse.getRqUid();
    }

    /**
     * Преобразует сущность {@link MentorTimeSlotEntity} в ДТО {@link MentorSlotInfo} для
     * gRPC-ответа
     *
     * @param mentorTimeSlotEntity - сущность слота {@link MentorTimeSlotEntity}
     * @param requestId            - уникальный идентификатор запроса
     * @return {@link MentorSlotInfo} - ДТО с данными слота и данными участников встречи
     */
    public MentorSlotInfo toMentorSlotInfo(MentorTimeSlotEntity mentorTimeSlotEntity,
                                           String requestId) {
        TimeSlotResponse slotInfo = toTimeSlotResponse(mentorTimeSlotEntity, requestId);

        List<UserInfo> participants = utilMapper.userEntitySetToUserInfoList(
                mentorTimeSlotEntity.getMeetingParticipants());

        return MentorSlotInfo.newBuilder()
                .setSlotInfo(slotInfo)
                .addAllParticipants(participants)
                .build();
    }

    ;

    /**
     * Преобразует gRPC ответ {@link MentorSlotInfo} в ДТО {@link MentorSlotInfoDto} для выдачи в
     * контроллер
     *
     * @param mentorSlotInfo - gRPC-сущность с данными о слоте и о пользователях
     * @return - ДТО для контроллера
     */
    @Mapping(target = "slotDto", source = "slotInfo",
            qualifiedByName = "timeSlotResponseToMentorTimeSlotDto")
    @Mapping(target = "participants", source = "participantsList",
            qualifiedByName = "userInfoListToUserInfoDtoList")
    public abstract MentorSlotInfoDto toDtoMentorSlotInfo(MentorSlotInfo mentorSlotInfo);

    /**
     * Формирует gRPC запрос для получения всех слотов ментора
     *
     * @param mentorId - ID ментора
     * @param header   заголовок gRPC-запроса (requestId/nodeId/apiKey)
     * @return - объект запроса {@link MentorSlotsInfoRequest}
     */
    public MentorSlotsInfoRequest toMentorSlotsInfoGrpcRequest(Long mentorId, Header header) {
        return MentorSlotsInfoRequest.newBuilder()
                .setMentorId(mentorId)
                .setHeader(header)
                .build();
    }

    /**
     * Преобразует список из gRPC-сущностей слотов менторов в список ДТО слотов
     *
     * @param mentorSlotsList - список слотов
     * @return - список ДТО {@link MentorSlotInfoDto}
     */
    public List<MentorSlotInfoDto> toSlotInfoDtoList(List<MentorSlotInfo> mentorSlotsList) {
        return mentorSlotsList.stream()
                .map(this::toDtoMentorSlotInfo)
                .toList();
    }

    /**
     * Конвертирует список сущностей {@link MentorTimeSlotEntity} в gRPC-ответ
     * {@link MentorSlotsInfoResponse}
     *
     * @param mentorSlots - список слотов ментора
     * @param requestId   - идентификатор запроса
     * @return готовый gRPC-ответ
     */
    public MentorSlotsInfoResponse toMentorSlotsInfoResponse(List<MentorTimeSlotEntity> mentorSlots,
                                                             String requestId) {
        List<MentorSlotInfo> mentorSlotsWithUsersInfo = mentorSlots.stream()
                .map(slot -> toMentorSlotInfo(
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
     * @param mentorTimeSlotEntities объект {@link Page} со списком {@link MentorTimeSlotEntity}
     * @param requestId              сквозной UUID запроса
     * @return gRPC-объект {@link AllTimeSlotsResponse}
     */
    public AllTimeSlotsResponse toAllTimeSlotsResponse(Page<MentorTimeSlotEntity> mentorTimeSlotEntities,
                                                       String requestId) {

        return AllTimeSlotsResponse.newBuilder()
                .setPageDetails(MentorTimeSlotEntityPageToPageDetails(
                        mentorTimeSlotEntities))
                .addAllTimeSlots(toMentorSlotInfoList(
                        mentorTimeSlotEntities,
                        requestId
                ))
                .build();
    }

    /**
     * Преобразует gRPC-объект, содержащий список слотов в список DTO слотов.
     *
     * @param allTimeSlots gRPC-объект со списком слотов
     * @return список {@link MentorSlotInfoDto}
     */
    public List<MentorSlotInfoDto> allTimeSlotsResponseToMentorSlotInfoDtoList
    (AllTimeSlotsResponse allTimeSlots) {
        return allTimeSlots.getTimeSlotsList().stream()
                .map(this::toDtoMentorSlotInfo)
                .toList();
    }

    private List<MentorSlotInfo> toMentorSlotInfoList(Page<MentorTimeSlotEntity> mentorTimeSlotEntities,
                                                      String requestId) {

        return mentorTimeSlotEntities.stream()
                .map(entity -> toMentorSlotInfo(
                        entity,
                        requestId
                ))
                .toList();
    }

    private PageDetails MentorTimeSlotEntityPageToPageDetails(Page<MentorTimeSlotEntity> mentorTimeSlotEntities) {

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
     * @param slotsInfoList gRPC ответ с данными о слотах ментора
     * @return {@link List<MentorTimeSlotInfoForUserDto>} - список ДТО с информацией о слотах для ученика
     */
    public List<MentorTimeSlotInfoForUserDto> mentorSlotInfoListToMentorTimeSlotInfoForUserDtoList(
            List<MentorSlotInfo> slotsInfoList) {
        return slotsInfoList.stream()
                .map(slotInfo -> {
                    return MentorTimeSlotInfoForUserDto.builder()
                            .isSlotFull(
                                    slotInfo.getParticipantsList()
                                            .size()
                                            == slotInfo.getSlotInfo()
                                            .getMaxParticipants()
                            )
                            .mentorTimeSlotDto(this.timeSlotResponseToMentorTimeSlotDto(
                                    slotInfo.getSlotInfo()))
                            .build();
                })
                .toList();
    }

}