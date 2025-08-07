package ru.mentor.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.mentor.calendar.CalendarServiceGrpc;
import ru.mentor.calendar.CreateTimeSlotRequest;
import ru.mentor.calendar.TimeSlotResponse;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CalendarServiceServer extends CalendarServiceGrpc.CalendarServiceImplBase {

    @Override
    public void createMentorTimeSlot(
            CreateTimeSlotRequest request,
            StreamObserver<TimeSlotResponse> responseObserver) {

    }

}
