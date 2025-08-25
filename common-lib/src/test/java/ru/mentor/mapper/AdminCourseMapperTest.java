package ru.mentor.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.protobuf.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mentor.admin.AllCoursesResponse;
import ru.mentor.admin.AuthorResponse;
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.GetCourseRequest;
import ru.mentor.admin.PageDetails;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;

@ExtendWith(MockitoExtension.class)
class AdminCourseMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AdminCourseMapper adminCourseMapper;

    private final String requestId = UUID.randomUUID().toString();
    private final long authorId = 1L;
    private final String username = "testuser";
    private final int pageNumber = 0;
    private final int pageSize = 10;
    private final int totalElementsCount = 1;
    private final long courseId = 1L;
    private final String title = "Course title";
    private final String description = "Course description";
    private final LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);

    @Test
    void mapGrpcCourseResponseToCourseDto_success() {
        AuthorResponse authorResponse = constructAuthorResponse();
        UserInfoDto userInfoDto = constructUserInfoDto();
        CourseResponse grpcResponse = constructCourseResponse(authorResponse);

        Mockito.when(userMapper.mapGrpcAuthorResponseToUserInfoDto(authorResponse))
               .thenReturn(userInfoDto);

        CourseDto dto = adminCourseMapper.mapGrpcCourseResponseToCourseDto(grpcResponse);

        assertThat(dto.getId()).isEqualTo(courseId);
        assertThat(dto.getCourseTitle()).isEqualTo(title);
        assertThat(dto.getCourseDescription()).isEqualTo(description);
        assertThat(dto.getAuthor()).isEqualTo(userInfoDto);
    }

    @Test
    void mapGrpcCourseResponseToCourseDtoPage_success() {
        AuthorResponse authorResponse = constructAuthorResponse();
        UserInfoDto userInfoDto = constructUserInfoDto();
        CourseResponse grpcResponse = constructCourseResponse(authorResponse);
        AllCoursesResponse grpcAllResponse = constructAllCoursesResponse(grpcResponse);

        Mockito.when(userMapper.mapGrpcAuthorResponseToUserInfoDto(authorResponse))
               .thenReturn(userInfoDto);

        Page<CourseDto> result = adminCourseMapper.mapGrpcCourseResponseToCourseDtoPage(
                grpcAllResponse);

        assertThat(result.getContent()).hasSize(totalElementsCount);
        assertThat(result.getContent().get(pageNumber).getId()).isEqualTo(courseId);
    }

    @Test
    void mapCourseEntityToGrpcCourseResponse_success() {
        UserEntity user = constructUserEntity();
        CourseEntity courseEntity = constructCourseEntity(user);
        AuthorResponse authorResponse = constructAuthorResponse();

        Mockito.when(userMapper.mapUserEntityToCourseAuthorResponse(user))
               .thenReturn(authorResponse);

        CourseResponse grpcResponse = adminCourseMapper.mapCourseEntityToGrpcCourseResponse(
                courseEntity);

        assertThat(grpcResponse.getCourseId()).isEqualTo(courseId);
        assertThat(grpcResponse.getTitle()).isEqualTo(title);
        assertThat(grpcResponse.getAuthor()).isEqualTo(authorResponse);
    }

    @Test
    void mapCourseEntityPageToGrpcAllCoursesResponse_success() {
        UserEntity user = constructUserEntity();
        CourseEntity courseEntity = constructCourseEntity(user);
        AuthorResponse authorResponse = constructAuthorResponse();

        Mockito.when(userMapper.mapUserEntityToCourseAuthorResponse(user))
               .thenReturn(authorResponse);

        Page<CourseEntity> coursePage = constructCourseEntityPage(courseEntity);
        AllCoursesResponse response =
                adminCourseMapper.mapCourseEntityPageToGrpcAllCoursesResponse(coursePage);

        assertThat(response.getCoursesList()).hasSize(totalElementsCount);
        assertThat(response.getCoursesList().get(pageNumber)
                           .getCourseId()).isEqualTo(courseId);
    }

    @Test
    void constructGetCourseRequest_success() {
        GetCourseRequest request = adminCourseMapper.constructGetCourseRequest(requestId, courseId);

        assertThat(request.getRequestId()).isEqualTo(requestId);
        assertThat(request.getCourseId()).isEqualTo(courseId);
    }

    private UserEntity constructUserEntity() {
        return UserEntity.builder()
                         .id(authorId)
                         .username(username)
                         .build();
    }

    private AllCoursesResponse constructAllCoursesResponse(CourseResponse grpcResponse) {
        return AllCoursesResponse.newBuilder()
                                 .setPageDetails(constructPageDetails())
                                 .addCourses(grpcResponse)
                                 .build();
    }

    private PageImpl<CourseEntity> constructCourseEntityPage(CourseEntity courseEntity) {
        return new PageImpl<>(
                List.of(courseEntity),
                PageRequest.of(pageNumber, pageSize),
                totalElementsCount
        );
    }

    private CourseEntity constructCourseEntity(UserEntity user) {
        return CourseEntity.builder()
                           .id(courseId)
                           .courseTitle(title)
                           .description(description)
                           .isActive(true)
                           .createdAt(createdAt)
                           .author(user)
                           .build();
    }

    private PageDetails constructPageDetails() {
        return PageDetails.newBuilder()
                          .setPage(pageNumber)
                          .setSize(pageSize)
                          .setTotalElements(totalElementsCount)
                          .setTotalPages(totalElementsCount)
                          .build();
    }

    private CourseResponse constructCourseResponse(AuthorResponse authorResponse) {
        return CourseResponse.newBuilder()
                             .setCourseId(courseId)
                             .setTitle(title)
                             .setDescription(description)
                             .setIsActive(true)
                             .setCreatedAt(constructCreatedAtTimestamp())
                             .setAuthor(authorResponse)
                             .build();
    }

    private Timestamp constructCreatedAtTimestamp() {
        return Timestamp.newBuilder()
                        .setSeconds(createdAt.toEpochSecond(ZoneOffset.UTC))
                        .setNanos(createdAt.getNano())
                        .build();
    }

    private UserInfoDto constructUserInfoDto() {
        return UserInfoDto.builder().id(authorId).username(username).build();
    }

    private AuthorResponse constructAuthorResponse() {
        return AuthorResponse.newBuilder().setUserId(authorId).build();
    }

}