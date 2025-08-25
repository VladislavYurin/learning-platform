package ru.mentor.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

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
import ru.mentor.admin.CourseResponse;
import ru.mentor.admin.GetCourseRequest;
import ru.mentor.admin.GrpcPageRequest;
import ru.mentor.admin.PageDetails;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.PageSettings;
import ru.mentor.grpc.AdminCourseServiceGrpcClient;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.services.impl.RedirectAdminCourseServiceImpl;

@ExtendWith(MockitoExtension.class)
class RedirectAdminCourseServiceImplTest {

    @Mock
    private AdminCourseServiceGrpcClient courseServiceClient;
    @Mock
    private AdminCourseMapper courseMapper;
    @Mock
    private BaseMapper baseMapper;

    @InjectMocks
    private RedirectAdminCourseServiceImpl redirectService;

    private final String requestId = UUID.randomUUID().toString();
    private final int pageNumber = 0;
    private final int pageSize = 10;
    private final Long courseId = 1L;
    private final String courseNotFoundExceptionText = "Courses not found";

    @Test
    void getCourseById_success() {
        CourseResponse grpcResponse = constructCourseResponse();
        CourseDto dto = Mockito.mock(CourseDto.class);

        Mockito.when(courseMapper.constructGetCourseRequest(anyString(), eq(courseId)))
               .thenReturn(constructGetCourseRequest());
        Mockito.when(courseServiceClient.getCourse(any(GetCourseRequest.class)))
               .thenReturn(grpcResponse);
        Mockito.when(courseMapper.mapGrpcCourseResponseToCourseDto(grpcResponse))
               .thenReturn(dto);

        CourseDto result = redirectService.getCourseById(courseId);

        assertThat(result).isEqualTo(dto);
        Mockito.verify(courseMapper).constructGetCourseRequest(anyString(), eq(courseId));
        Mockito.verify(courseServiceClient).getCourse(any(GetCourseRequest.class));
        Mockito.verify(courseMapper).mapGrpcCourseResponseToCourseDto(grpcResponse);
    }

    @Test
    void getCourseById_failure() {
        Mockito.when(courseMapper.constructGetCourseRequest(anyString(), eq(courseId)))
               .thenReturn(constructGetCourseRequest());
        Mockito.when(courseServiceClient.getCourse(any(GetCourseRequest.class)))
               .thenThrow(new RuntimeException(courseNotFoundExceptionText));

        assertThatThrownBy(() -> redirectService.getCourseById(courseId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(courseNotFoundExceptionText);

        Mockito.verify(courseMapper).constructGetCourseRequest(anyString(), eq(courseId));
        Mockito.verify(courseServiceClient).getCourse(any(GetCourseRequest.class));
    }

    @Test
    void getAllCourses_success() {
        PageSettings pageSettings = constructPageSettings();
        String requestId = UUID.randomUUID().toString();

        GrpcPageRequest grpcPageRequest = constructGrpcPageRequest(requestId);
        AllCoursesResponse allCoursesResponse = constructAllCoursesResponse();

        List<CourseDto> dtoList = List.of(Mockito.mock(CourseDto.class));
        Page<CourseDto> expectedPage = constructExpectedPage(dtoList);

        Mockito.when(baseMapper.constructGrpcPageRequest(anyString(), eq(pageSettings)))
               .thenReturn(grpcPageRequest);
        Mockito.when(courseServiceClient.getAllCourses(grpcPageRequest))
               .thenReturn(allCoursesResponse);
        Mockito.when(courseMapper.mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse))
               .thenReturn(expectedPage);

        Page<CourseDto> result = redirectService.getAllCourses(pageSettings);

        assertThat(result).isEqualTo(expectedPage);
        Mockito.verify(baseMapper).constructGrpcPageRequest(anyString(), eq(pageSettings));
        Mockito.verify(courseServiceClient).getAllCourses(grpcPageRequest);
        Mockito.verify(courseMapper).mapGrpcCourseResponseToCourseDtoPage(allCoursesResponse);
    }

    @Test
    void getAllCourses_failure() {
        PageSettings pageSettings = constructPageSettings();

        GrpcPageRequest grpcPageRequest = constructGrpcPageRequest(requestId);

        Mockito.when(baseMapper.constructGrpcPageRequest(anyString(), eq(pageSettings)))
               .thenReturn(grpcPageRequest);
        Mockito.when(courseServiceClient.getAllCourses(grpcPageRequest))
               .thenThrow(new RuntimeException(courseNotFoundExceptionText));

        assertThatThrownBy(() -> redirectService.getAllCourses(pageSettings))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(courseNotFoundExceptionText);

        Mockito.verify(baseMapper).constructGrpcPageRequest(anyString(), eq(pageSettings));
        Mockito.verify(courseServiceClient).getAllCourses(grpcPageRequest);
    }

    private GetCourseRequest constructGetCourseRequest() {
        return GetCourseRequest.newBuilder()
                               .setRequestId(requestId)
                               .setCourseId(courseId)
                               .build();
    }

    private PageSettings constructPageSettings() {
        return new PageSettings(pageNumber, pageSize);
    }

    private CourseResponse constructCourseResponse() {
        return CourseResponse.newBuilder()
                             .setCourseId(courseId)
                             .build();
    }

    private PageImpl<CourseDto> constructExpectedPage(List<CourseDto> dtoList) {
        return new PageImpl<>(
                dtoList,
                PageRequest.of(pageNumber, pageSize),
                1
        );
    }

    private AllCoursesResponse constructAllCoursesResponse() {
        return AllCoursesResponse.newBuilder()
                                 .setPageDetails(constructPageDetails())
                                 .build();
    }

    private PageDetails constructPageDetails() {
        return PageDetails.newBuilder()
                          .setPage(pageNumber)
                          .setSize(pageSize)
                          .setTotalElements(1)
                          .build();
    }

    private GrpcPageRequest constructGrpcPageRequest(String requestId) {
        return GrpcPageRequest.newBuilder()
                              .setRequestId(requestId)
                              .build();
    }

}