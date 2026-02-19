package ru.mentor.mapper;

import org.springframework.stereotype.Component;
import ru.mentor.access.grpc.GrantCourseAccessRequest;
import ru.mentor.dto.GrantCourseAccessRequestDto;

@Component
public class AccessRequestMapper {

    public GrantCourseAccessRequestDto toDto(GrantCourseAccessRequest grpcRequest) {
        return GrantCourseAccessRequestDto.builder()
                .userId(grpcRequest.getUserId())
                .courseId(grpcRequest.getCourseId())
                .build();
    }
}
