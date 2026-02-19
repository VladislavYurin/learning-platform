package ru.mentor.grpc;


import com.google.protobuf.Empty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.dto.GrantCourseAccessRequestDto;
import ru.mentor.mentor.grpc.MentorGrantCourseAccessRequest;
import ru.mentor.mentor.grpc.ReactorMentorCourseAccessServiceGrpc;
import ru.mentor.service.AccessService;

/**
 * gRPC-адаптер
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class MentorCourseAccessServer extends
        ReactorMentorCourseAccessServiceGrpc.MentorCourseAccessServiceImplBase {

    public static final String GET_COURSE_REQUEST_LOG_TEXT =
            "[ requestId = {} ] Поступил запрос на открытие доступа к курсу "
                    + " [ ID = {} ] от наставника [ ID = {} ]";


    private final AccessService accessService;

    @Override
    public Mono<Empty> grantCourseAccess(Mono<MentorGrantCourseAccessRequest> requestMono) {
        return requestMono
                .flatMap(req -> accessService.grantCourseAccess(req.getHeader().getRequestId(),
                        GrantCourseAccessRequestDto.builder()
                                .userId(req.getUserId())
                                .courseId(req.getCourseId())
                                .build()
                ))
                .thenReturn(Empty.getDefaultInstance());
    }

    private void logGetCourseRequest(GetCourseRequest request) {
        log.info(
                GET_COURSE_REQUEST_LOG_TEXT,
                request.getHeader().getRequestId(),
                request.getCourseId(),
                request.getSenderId()
        );
    }
}

