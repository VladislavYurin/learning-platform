package ru.mentor.mapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.mentor.common.AllActiveCoursesResponse;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.CreateCourseGrpcRequest;
import ru.mentor.common.DeleteCourseRequest;
import ru.mentor.common.GetAllActiveCoursesPreviewRequest;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.common.Header;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.front.CreateCourseRequest;
import ru.mentor.dto.tag.CourseTagDto;

/**
 * Маппер для формирования внутреннего запроса на создание курса и модуля.
 */
@Component
@RequiredArgsConstructor
public class CourseMapper {
    private final BaseMapper baseMapper;

    private final AdminCourseMapper adminCourseMapper;
    private final TagGrpcMapper tagGrpcMapper;
    private final UserMapper userMapper;

    public CreateCourseGrpcRequest constructGrpcCreateRequest(Header header, Long userId, CreateCourseRequest request) {
        return CreateCourseGrpcRequest.newBuilder()
                .setHeader(header)
                .setUserId(userId)
                .setCourseName(request.getCourseName())
                .setCourseDescription(request.getCourseDescription())
                .addAllTagIds(request.getTagIds())
                .build();
    }

    public GetCourseRequest constructGrpcGetRequest(Header header, Long userId, Long courseId) {
        return GetCourseRequest.newBuilder()
                .setHeader(header)
                .setSenderId(userId)
                .setCourseId(courseId)
                .build();
    }

    public GetAllActiveCoursesPreviewRequest constructGetAllActiveCoursesPreviewRequest(Header header, Long userId) {
        return GetAllActiveCoursesPreviewRequest.newBuilder()
                .setHeader(header)
                .setSenderId(userId)
                .build();
    }

    public DeleteCourseRequest constructGrpcDeleteRequest(Header header, Long userId, Long courseId) {
        return DeleteCourseRequest.newBuilder()
                .setHeader(header)
                .setSenderId(userId)
                .setCourseId(courseId)
                .build();
    }

    public GrpcPageRequest constructGrpcPageRequest(Header header, int pageNumber, int pageSize, Long userId) {
        return baseMapper.constructGrpcPageRequest(header, pageNumber, pageSize, userId);
    }

    public CourseDto mapGrpcCourseResponseToCourseDto(CourseResponse courseResponse) {
        return adminCourseMapper.mapGrpcCourseResponseToCourseDto(courseResponse);
    }

    public Page<CourseDto> mapGrpcCourseResponseToCourseDtoPage(AllCoursesResponse allActiveCourses) {
        return adminCourseMapper.mapGrpcCourseResponseToCourseDtoPage(allActiveCourses);
    }

    public List<CourseDto> mapGrpcAllActiveCoursesResponseToCourseDtoList(AllActiveCoursesResponse courses) {
        return courses.getCoursesList()
                      .stream()
                      .sorted(Comparator.comparing(CourseResponse::getTitle))
                      .map(this::mapCourseResponseToDto)
                      .toList();
    }

    public CourseDto mapCourseResponseToDto(CourseResponse course) {
        List<CourseTagDto> tagsList =
                course.getTagsList()
                      .stream()
                      .map(tagGrpcMapper::fromGrpc)
                      .toList();

        return CourseDto.builder()
                        .id(course.getCourseId())
                        .courseTitle(course.getTitle())
                        .courseDescription(course.getDescription())
                        .isActive(true)
                        .createdAt(LocalDateTime.ofEpochSecond(
                                course.getCreatedAt().getSeconds(),
                                course.getCreatedAt().getNanos(),
                                ZoneOffset.UTC))
                        .author(userMapper.mapGrpcAuthorResponseToUserInfoDto(
                                course.getAuthor()))
                        .tags(tagsList)
                        .build();
    }

}
