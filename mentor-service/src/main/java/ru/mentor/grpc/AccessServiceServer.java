package ru.mentor.grpc;

import com.google.protobuf.Empty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;


import ru.mentor.access.grpc.*;
import ru.mentor.dto.GrantCourseAccessRequestDto;
import ru.mentor.service.AccessService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class AccessServiceServer extends ReactorAccessServiceGrpc.AccessServiceImplBase {

    private final AccessService accessService;

    @Override
    public Mono<Empty> grantCourseAccess(Mono<GrantCourseAccessRequest> requestMono) {
        return requestMono
                .flatMap(req -> accessService.grantCourseAccess(req.getHeader().getRequestId(),
                        GrantCourseAccessRequestDto.builder()
                        .userId(req.getUserId())
                        .courseId(req.getCourseId())
                                .build()
                        ))
                .thenReturn(Empty.getDefaultInstance());
    }
}
