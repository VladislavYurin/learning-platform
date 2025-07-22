package ru.mentor.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.dto.InnerCreateCourseRequest;
import ru.mentor.dto.front.CreateCourseRequest;

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

}
