package ru.mentor.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.dto.front.AccessRequest;
import ru.mentor.entity.UserEntity;

@Component
@RequiredArgsConstructor
public class AccessMapper {

    public GetAccessRequest mapToInnerRequest(UserEntity user, AccessRequest request) {
        return GetAccessRequest.builder()
                               .mentorId(user.getId())
                               .userId(request.getUserId())
                               .moduleId(request.getModuleId())
                               .courseId(request.getCourseId())
                               .build();
    }

}
