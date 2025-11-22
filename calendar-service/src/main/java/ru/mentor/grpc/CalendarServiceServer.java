package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.mentor.calendar.CalendarServiceGrpc;
import ru.mentor.common.BookTimeSlotRequest;
import ru.mentor.common.CreateTimeSlotRequest;
import ru.mentor.common.MentorSlotsInfoRequest;
import ru.mentor.common.MentorSlotsInfoResponse;
import ru.mentor.common.TimeSlotResponse;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.TimeSlotUnavailableException;
import ru.mentor.exception.UserException;
import ru.mentor.kafka.KafkaFacade;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.repository.UserRepository;

/**
 * Реализация сервиса календаря с использованием gRPC.
 * <p>
 * Этот сервис позволяет менторам создавать временные слоты для планирования.
 * Он расширяет сгенерированный класс CalendarServiceImplBase из Protocol Buffers,
 * чтобы предоставить фактическую реализацию методов gRPC, определенных в .proto файле.
 */
@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CalendarServiceServer extends CalendarServiceGrpc.CalendarServiceImplBase {

    private final TimeSlotMapper timeSlotMapper;

    private final UserRepository userRepository;

    private final MentorTimeSlotRepository mentorTimeSlotRepository;

    private final BaseMapper baseMapper;

    private final KafkaFacade kafkaFacade;

    /**
     * Создает временной слот для ментора.
     *
     * @param request
     *         Запрос, содержащий детали временного слота, который нужно создать.
     * @param responseObserver
     *         Наблюдатель для отправки ответа обратно клиенту.
     */
    @Override
    public void createMentorTimeSlot(
            CreateTimeSlotRequest request,
            StreamObserver<TimeSlotResponse> responseObserver) {

        String requestId = request.getHeader().getRequestId();
        long mentorId = request.getMentorId();

        log.info(
                "Поступил запрос {} на создание слота от ментора с ID {}",
                requestId,
                mentorId
        );

        try {
            UserEntity mentor = userRepository.findById(mentorId)
                                              .orElseThrow(() -> new UserException(String.format(
                                                      "Ментор с ID: %s не найден", mentorId)));

            MentorTimeSlotEntity newMentorTimeSlot = timeSlotMapper.grpcCreateRequestToEntity(
                    request,
                    mentor
            );
            newMentorTimeSlot = mentorTimeSlotRepository.save(newMentorTimeSlot);
            responseObserver.onNext(timeSlotMapper.entityToGrpcResponse(
                    newMentorTimeSlot,
                    requestId
            ));
            responseObserver.onCompleted();

        } catch (UserException e) {
            responseObserver.onError(Status.NOT_FOUND
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        }
    }

    /**
     * gRPC-эндпоинт для бронирования слота учеником.
     * Возвращает ответ через объект {@link StreamObserver}
     * Планирует отправку сообщений в Kafka после успешного коммита транзакции.
     * Используется {@link TransactionSynchronizationManager} - позволяет подписаться
     * на колбэки жизненного цикла транзакции после коммита.
     *
     * @param request
     *         запрос на бронирование {@link BookTimeSlotRequest}
     * @param responseObserver
     *         наблюдатель отправки ответа клиенту {@link StreamObserver}
     */
    @Transactional
    @Override
    public void bookTimeslot(
            BookTimeSlotRequest request,
            StreamObserver<TimeSlotResponse> responseObserver
    ) {
        String requestId = request.getHeader().getRequestId();
        Long userId = request.getUserId();
        Long slotId = request.getSlotId();

        log.info(
                "Поступил запрос {} на бронирование слота от пользователя с ID: {}",
                requestId,
                userId
        );

        try {
            UserEntity user = userRepository.findByIdOrThrow(userId);

            MentorTimeSlotEntity slotEntity = mentorTimeSlotRepository.findByIdOrThrow(slotId);

            checkSlotIsAvailable(slotEntity, userId);

            slotEntity.getMeetingParticipants().add(user);
            MentorTimeSlotEntity bookedTimeSlot = mentorTimeSlotRepository.saveAndFlush(slotEntity);

            UserInfoDto mentorDto = baseMapper.mapUserDto(slotEntity.getMentor());
            UserInfoDto menteeDto = baseMapper.mapUserDto(user);
            LocalDateTime startAt = slotEntity.getStartTime();
            LocalDateTime endAt = slotEntity.getEndTime();

            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronization() {
                            @Override
                            public void afterCommit() {
                                try {
                                    kafkaFacade.sendCreateSlotBookedMessage(
                                            mentorDto,
                                            startAt,
                                            endAt,
                                            menteeDto
                                    );
                                } catch (Exception ex) {
                                    log.error(String.format(
                                            "[ Slot = %s ] Ошибка отправки сообщения в Kafka [ MentorId = %s ].",
                                            slotId,
                                            mentorDto.getId()
                                    ));
                                }
                            }
                        }
                );
            } else {
                try {
                    kafkaFacade.sendCreateSlotBookedMessage(mentorDto, startAt, endAt, menteeDto);
                } catch (Exception ex) {
                    log.error(String.format(
                            "[ Slot = %s ] Ошибка отправки сообщения в Kafka [ MentorId = %s ].",
                            slotId,
                            mentorDto.getId()
                    ));
                }
            }

            responseObserver.onNext(timeSlotMapper.entityToGrpcResponse(bookedTimeSlot, requestId));
            responseObserver.onCompleted();

        } catch (TimeSlotUnavailableException e) {
            responseObserver.onError(Status.UNAVAILABLE
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                                             .withDescription(e.getMessage())
                                             .asRuntimeException());
        }
    }

    private void checkSlotIsAvailable(MentorTimeSlotEntity slotEntity, Long userId)
            throws TimeSlotUnavailableException {
        if (slotIsFull(slotEntity)) {
            throw new TimeSlotUnavailableException(
                    "На встрече нет свободных мест"
            );
        }

        if (slotIsInactive(slotEntity)) {
            throw new TimeSlotUnavailableException(
                    "Слот не активен"
            );
        }

        if (existsOverlappingSlots(userId, slotEntity)) {
            throw new TimeSlotUnavailableException(
                    "Вы уже записаны на другой слот в это время"
            );
        }
    }

    private boolean slotIsFull(MentorTimeSlotEntity slotEntity) {
        return slotEntity.getMeetingParticipants().size() + 1 > slotEntity.getMaxParticipants();
    }

    private boolean slotIsInactive(MentorTimeSlotEntity slotEntity) {
        return !slotEntity.getIsActive();
    }

    private boolean existsOverlappingSlots(Long userId, MentorTimeSlotEntity slotEntity) {
        return mentorTimeSlotRepository.existsOverlappingSlots(
                userId,
                slotEntity.getStartTime(),
                slotEntity.getEndTime()
        );
    }

    /**
     * gRPC - эндпоинт для получения всех слотов ментора
     *
     * @param request
     *         сгенерированный из proto {@link MentorSlotsInfoRequest}
     * @param responseObserver
     *         - объект {@link StreamObserver} для возврата ответа
     */

    @Override
    public void getMentorSlots(
            MentorSlotsInfoRequest request,
            StreamObserver<MentorSlotsInfoResponse> responseObserver) {

        String requestId = request.getHeader().getRequestId();
        long mentorId = request.getMentorId();

        log.info(
                "Поступил запрос {} в gRPC сервис на получение всех слотов ментора с ID {}",
                requestId, mentorId
        );

        List<MentorTimeSlotEntity> mentorSlots =
                mentorTimeSlotRepository.findByMentorIdWithParticipants(mentorId);

        MentorSlotsInfoResponse response =
                timeSlotMapper.convertToMentorSlotsInfoResponse(mentorSlots, requestId);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
