package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.mentor.calendar.BookTimeSlotRequest;
import ru.mentor.calendar.CalendarServiceGrpc;
import ru.mentor.calendar.CreateTimeSlotRequest;
import ru.mentor.calendar.TimeSlotResponse;
import ru.mentor.entity.MentorTimeSlotEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.TimeSlotUnavailableException;
import ru.mentor.exception.UserException;
import ru.mentor.mapper.TimeSlotMapper;
import ru.mentor.repository.MentorTimeSlotRepository;
import ru.mentor.repository.UserRepository;

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
     *
     * @param request {@link BookTimeSlotRequest}
     * @param responseObserver {@link StreamObserver}
     */
    @Override
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

            slotEntity.getMeetingParticipants().add(user);
            MentorTimeSlotEntity bookedTimeSlot = mentorTimeSlotRepository.save(slotEntity);

            responseObserver.onNext(timeSlotMapper.entityToGrpcResponse(bookedTimeSlot, rqUId));
            responseObserver.onCompleted();

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

}
