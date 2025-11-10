package ru.mentor.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.mentor.common.AllCoursesResponse;
import ru.mentor.common.CourseResponse;
import ru.mentor.common.GetCourseRequest;
import ru.mentor.common.GrpcPageRequest;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.gateway.model.CourseDto;
import ru.mentor.grpc.AdminCourseServiceGrpcClient;
import ru.mentor.mapper.AdminCourseMapper;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.services.impl.RedirectAdminCourseServiceImpl;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    @Test
    void getCourseById_success() {
        Long courseId = 1L;

        GetCourseRequest getReq = mock(GetCourseRequest.class);
        CourseResponse grpcResp = mock(CourseResponse.class);
        CourseDto expected = new CourseDto().id(courseId).courseTitle("T").courseDescription("D");

        when(courseMapper.constructGetCourseRequest(anyString(), eq(courseId))).thenReturn(getReq);
        when(courseServiceClient.getCourse(getReq)).thenReturn(grpcResp);
        when(courseMapper.mapGrpcCourseResponseToCourseDto(grpcResp)).thenReturn(expected);

        CourseDto actual = redirectService.getCourseById(courseId);

        assertThat(actual).isEqualTo(expected);
        verify(courseMapper).constructGetCourseRequest(anyString(), eq(courseId));
        verify(courseServiceClient).getCourse(getReq);
        verify(courseMapper).mapGrpcCourseResponseToCourseDto(grpcResp);
        verifyNoMoreInteractions(courseMapper, courseServiceClient, baseMapper);
    }

    @Test
    void getCourseById_failure() {
        Long courseId = 42L;

        GetCourseRequest getReq = mock(GetCourseRequest.class);
        when(courseMapper.constructGetCourseRequest(anyString(), eq(courseId))).thenReturn(getReq);
        when(courseServiceClient.getCourse(getReq))
                .thenThrow(new EntityNotFoundException("not found"));

        assertThrows(EntityNotFoundException.class, () -> redirectService.getCourseById(courseId));

        verify(courseMapper).constructGetCourseRequest(anyString(), eq(courseId));
        verify(courseServiceClient).getCourse(getReq);
        verify(courseMapper, never()).mapGrpcCourseResponseToCourseDto(any());
        verifyNoMoreInteractions(courseMapper, courseServiceClient, baseMapper);
    }

    @Test
    void getAllCourses_success() {
        int pageNumber = 0, pageSize = 10;

        GrpcPageRequest pageReq = mock(GrpcPageRequest.class);
        AllCoursesResponse grpcResp = mock(AllCoursesResponse.class);
        Page<CourseDto> expected = new PageImpl<>(Collections.singletonList(new CourseDto().id(1L)));

        when(baseMapper.constructGrpcPageRequest(anyString(), eq(pageNumber), eq(pageSize))).thenReturn(pageReq);
        when(courseServiceClient.getAllCourses(pageReq)).thenReturn(grpcResp);
        when(courseMapper.mapGrpcCourseResponseToCourseDtoPage(grpcResp)).thenReturn(expected);

        Page<CourseDto> actual = redirectService.getAllCourses(pageNumber, pageSize);

        assertThat(actual).isEqualTo(expected);
        verify(baseMapper).constructGrpcPageRequest(anyString(), eq(pageNumber), eq(pageSize));
        verify(courseServiceClient).getAllCourses(pageReq);
        verify(courseMapper).mapGrpcCourseResponseToCourseDtoPage(grpcResp);
        verifyNoMoreInteractions(courseMapper, courseServiceClient, baseMapper);
    }

    @Test
    void getAllCourses_failure() {
        int pageNumber = 1, pageSize = 5;

        GrpcPageRequest pageReq = mock(GrpcPageRequest.class);
        when(baseMapper.constructGrpcPageRequest(anyString(), eq(pageNumber), eq(pageSize))).thenReturn(pageReq);
        when(courseServiceClient.getAllCourses(pageReq))
                .thenThrow(new EntityNotFoundException("not found"));

        assertThrows(EntityNotFoundException.class,
                () -> redirectService.getAllCourses(pageNumber, pageSize));

        verify(baseMapper).constructGrpcPageRequest(anyString(), eq(pageNumber), eq(pageSize));
        verify(courseServiceClient).getAllCourses(pageReq);
        verify(courseMapper, never()).mapGrpcCourseResponseToCourseDtoPage(any());
        verifyNoMoreInteractions(courseMapper, courseServiceClient, baseMapper);
    }
}