package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.mentor.calendar.BookTimeSlotRequest;
import ru.mentor.calendar.CalendarServiceGrpc;
import ru.mentor.calendar.CreateTimeSlotRequest;
import ru.mentor.calendar.TimeSlotResponse;
import ru.mentor.constant.BookingStatus;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.BookedTimeSlotEntity;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.TimeSlotUnavailableException;
import ru.mentor.exception.UserException;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.repository.UserRepository;
import ru.mentor.kafka.KafkaFacade;
import ru.mentor.repository.BookedTimeSlotRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса календаря с использованием gRPC.
 *
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

    private final BookedTimeSlotRepository bookedTimeSlotRepository;

    private final KafkaFacade kafkaFacade;

    private final BaseMapper baseMapper;

    /**
     * Создает временной слот для ментора.
     *
     * @param request           Запрос, содержащий детали временного слота, который нужно создать.
     * @param responseObserver  Наблюдатель для отправки ответа обратно клиенту.
     */
    @Override
    public void createMentorTimeSlot(
            CreateTimeSlotRequest request,
            StreamObserver<TimeSlotResponse> responseObserver) {

        String rqUId = request.getRqUid();
        long mentorId = request.getMentorId();

        log.info("Поступил запрос {} на создание слота от ментора с ID {}",
                rqUId,
                mentorId);

        try {
            UserEntity mentor = userRepository.findById(mentorId)
                    .orElseThrow(() -> new UserException(String.format(
                            "Ментор с ID: %s не найден", mentorId)));

            MentorTimeSlotEntity newMentorTimeSlot = timeSlotMapper.grpcCreateRequestToEntity(request, mentor);
            newMentorTimeSlot = mentorTimeSlotRepository.save(newMentorTimeSlot);
            responseObserver.onNext(timeSlotMapper.entityToGrpcResponse(newMentorTimeSlot, rqUId));
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
     * Планирует отправку уведомлений в Kafka после успешного коммита транзакции.
     * Для отправки сообщений в Kafka используется {@link TransactionSynchronizationManager}
     * который держит контекст текущей транзакции в ThreadLocal и позволяет подписаться
     * на колбэки жизненного цикла транзакции после коммита.
     * @param request запрос на бронирование {@link BookTimeSlotRequest}
     * @param responseObserver наблюдатель отправки ответа клиенту {@link StreamObserver}
     */
    @Override
    @Transactional
    public void bookTimeslot(
            BookTimeSlotRequest request,
            StreamObserver<TimeSlotResponse> responseObserver
    ) {
        String rqUId = request.getRqUid();
        Long userId = request.getUserId();
        Long slotId = request.getSlotId();

        log.info("Поступил запрос {} на бронирование слота от пользователя с ID: {}",
                rqUId,
                userId);

        try {
            UserEntity user = userRepository.findByIdOrThrow(userId);

            MentorTimeSlotEntity slotEntity = mentorTimeSlotRepository.findByIdOrThrow(slotId);

            checkSlotIsAvailable(slotEntity, userId);
            checkSlotNotBooked(slotEntity);

            BookedTimeSlotEntity booking = bookedTimeSlotRepository.save(
                    BookedTimeSlotEntity.builder()
                            .mentor(slotEntity.getMentor())
                            .mentee(user)
                            .startTime(slotEntity.getStartTime())
                            .endTime(slotEntity.getEndTime())
                            .status(BookingStatus.CONFIRMED)
                            .slot(slotEntity)
                            .build()
            );

            responseObserver.onNext(timeSlotMapper.entityToGrpcResponse(slotEntity, rqUId));
            responseObserver.onCompleted();

            UserInfoDto mentorDto = baseMapper.mapUserDto(slotEntity.getMentor());
            UserInfoDto menteeDto = baseMapper.mapUserDto(user);
            LocalDateTime startAt = booking.getStartTime();
            LocalDateTime endAt = booking.getEndTime();

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    try {
                        kafkaFacade.sendCreateSlotBookedMessage(
                                mentorDto, menteeDto, startAt, endAt);
                    } catch (Exception ex) {
                        log.error("Kafka: sendCreateSlotBookedMessage failed (slotId={}, mentorId={})",
                                slotEntity.getId(), slotEntity.getMentor().getId(), ex);
                    }
                }
            });

        } catch (TimeSlotUnavailableException e){
            responseObserver.onError(Status.UNAVAILABLE
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (RuntimeException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private void checkSlotIsAvailable(MentorTimeSlotEntity slotEntity, Long userId) throws TimeSlotUnavailableException{
        if (slotIsFull(slotEntity))
            throw new TimeSlotUnavailableException(
                    "На встрече нет свободных мест"
            );

        if (slotIsInactive(slotEntity))
            throw new TimeSlotUnavailableException(
                    "Слот не активен"
            );

        if (existsOverlappingSlots(userId, slotEntity))
            throw new TimeSlotUnavailableException(
                    "Вы уже записаны на другой слот в это время"
            );
    }

    private boolean slotIsFull(MentorTimeSlotEntity slotEntity){
        return slotEntity.getMeetingParticipants().size() + 1 > slotEntity.getMaxParticipants();
    }

    private boolean slotIsInactive(MentorTimeSlotEntity slotEntity) {
        return !slotEntity.getIsActive();
    }

    private boolean existsOverlappingSlots(Long userId, MentorTimeSlotEntity slotEntity){
        return mentorTimeSlotRepository.existsOverlappingSlots(userId, slotEntity.getStartTime(), slotEntity.getEndTime());
    }

    /**
     * Проверяет отсутствие активных броней переданного слота.
     * @param slot временной слот наставника, для которого выполняется проверка
     */
    private void checkSlotNotBooked(MentorTimeSlotEntity slot) {
        long active = bookedTimeSlotRepository.countBySlotIdAndStatusIn(
                slot.getId(),
                List.of(BookingStatus.REQUESTED, BookingStatus.CONFIRMED)
        );
        if (active > 0) {
            throw new TimeSlotUnavailableException("Слот уже забронирован");
        }
    }
}
