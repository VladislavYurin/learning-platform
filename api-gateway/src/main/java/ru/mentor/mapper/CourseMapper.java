package ru.mentor.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.dto.InnerCreateCourseRequest;
import ru.mentor.dto.InnerCreateModuleRequest;
import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.dto.front.CreateModuleRequest;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    public InnerCreateCourseRequest mapToInnerCreateCourseRequest(
            Long userId,
            CreateCourseRequest createCourseRequest) {
        return InnerCreateCourseRequest.builder()
                                       .userId(userId)
                                       .courseDescription(createCourseRequest.getCourseDescription())
                                       .courseName(createCourseRequest.getCourseName())
                                       .build();
    }

    public InnerCreateModuleRequest mapToInnerCreateModuleRequest(
            Long userId,
            CreateModuleRequest createModuleRequest) {
        return InnerCreateModuleRequest.builder()
                                       .userId(userId)
                                       .courseId(createModuleRequest.getCourseId())
                                       .moduleTitle(createModuleRequest.getModuleTitle())
                                       .moduleOrderNumber(createModuleRequest.getModuleOrderNumber())
                                       .moduleContent(
                                               createModuleRequest.getModuleContent())
                                       .build();

    }

}
