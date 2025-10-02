package ru.mentor.services;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.common.CourseTagResponse;
import ru.mentor.common.CreateCourseTagGrpcRequest;
import ru.mentor.common.DeleteCourseTagRequest;
import ru.mentor.common.GetAllCourseTagsRequest;
import ru.mentor.common.GetCourseTagRequest;
import ru.mentor.common.ListCourseTagsResponse;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.dto.tag.CreateCourseTagRequest;
import ru.mentor.exception.GrpcRetryException;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.CourseTagsGrpcClient;
import ru.mentor.mapper.CourseTagsMapper;
import ru.mentor.mapper.TagGrpcMapper;
import ru.mentor.services.impl.RedirectCourseTagServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class RedirectCourseTagServiceImplTest {

    @Mock
    private CourseTagsGrpcClient client;
    @Mock
    private TagGrpcMapper tagGrpcMapper;
    @Mock
    private UserService userService;
    @Mock
    private CourseTagsMapper courseTagsMapper;
    @Mock
    private HeaderFactory headerFactory;
    @InjectMocks
    private RedirectCourseTagServiceImpl redirectCourseTagService;

    @Test
    void createCourseTag() {
        CreateCourseTagRequest createCourseTagRequest = TestEntityStubGenerator.constructCreateCourseTagRequest();
        CreateCourseTagGrpcRequest createCourseTagGrpcRequest = TestGrpcStubGenerator.constructCreateCourseTagGrpcRequest();
        CourseTagResponse courseTagResponse = TestGrpcStubGenerator.constructCourseTagResponse();
        CourseTagDto dto = Mockito.mock(CourseTagDto.class);

        Mockito.when(courseTagsMapper.constructGrpcCreateRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(createCourseTagRequest)
        )).thenReturn(createCourseTagGrpcRequest);

        Mockito.when(tagGrpcMapper.fromGrpc(courseTagResponse)).thenReturn(dto);
        Mockito.when(client.createCourseTag(createCourseTagGrpcRequest)).thenReturn(courseTagResponse);
        Mockito.when(userService.getCurrentUserId()).thenReturn(TestConstantHolder.userId);

        CourseTagDto result = redirectCourseTagService.createCourseTag(createCourseTagRequest);
        Assertions.assertThat(result).isEqualTo(dto);
        Mockito.verify(courseTagsMapper).constructGrpcCreateRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(createCourseTagRequest)
        );
        Mockito.verify(client).createCourseTag(ArgumentMatchers.any(CreateCourseTagGrpcRequest.class));
        Mockito.verify(tagGrpcMapper).fromGrpc(courseTagResponse);
    }

    @Test
    void deleteCourseTag() {
        DeleteCourseTagRequest deleteCourseTagRequest = TestGrpcStubGenerator.constructDeleteCourseTagGrpcRequest();
        Mockito.when(courseTagsMapper.constructGrpcDeleteRequest(
                       ArgumentMatchers.any(),
                       ArgumentMatchers.eq(TestConstantHolder.userId),
                       ArgumentMatchers.eq(TestConstantHolder.courseTagId)
               ))
               .thenReturn(deleteCourseTagRequest);

        Mockito.when(userService.getCurrentUserId()).thenReturn(TestConstantHolder.userId);
        redirectCourseTagService.deleteCourseTag(TestConstantHolder.courseTagId);

        Mockito.verify(courseTagsMapper).constructGrpcDeleteRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(TestConstantHolder.courseTagId)
        );
        Mockito.verify(client).deleteCourseTag(ArgumentMatchers.eq(deleteCourseTagRequest));
    }

    @Test
    void getAllTags_success() {
        GetAllCourseTagsRequest getAllCourseTagsRequest = TestGrpcStubGenerator.constructGetAllCourseTagsRequest();
        ListCourseTagsResponse listCourseTagsResponse = TestGrpcStubGenerator.constructAllCourseTagsResponse();
        int expectedCount = listCourseTagsResponse.getTagsList().size();

        Mockito.when(courseTagsMapper.constructAllCourseTagsRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId)
        )).thenReturn(getAllCourseTagsRequest);

        Mockito.when(client.getAllTags(ArgumentMatchers.any(GetAllCourseTagsRequest.class)))
               .thenReturn(listCourseTagsResponse);

        Mockito.when(userService.getCurrentUserId())
               .thenReturn(TestConstantHolder.userId);

        CourseTagDto mockDto = Mockito.mock(CourseTagDto.class);
        Mockito.when(tagGrpcMapper.fromGrpc(ArgumentMatchers.any(CourseTagResponse.class)))
               .thenReturn(mockDto);

        List<CourseTagDto> result = redirectCourseTagService.getAllTags();

        Assertions.assertThat(result)
                  .isNotNull()
                  .hasSize(expectedCount);

        for (CourseTagDto dto : result) {
            Assertions.assertThat(dto).isSameAs(mockDto);
        }

        Mockito.verify(courseTagsMapper).constructAllCourseTagsRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId)
        );
        Mockito.verify(client).getAllTags(ArgumentMatchers.any(GetAllCourseTagsRequest.class));
        Mockito.verify(tagGrpcMapper, Mockito.times(expectedCount))
               .fromGrpc(ArgumentMatchers.any(CourseTagResponse.class));
    }

    @Test
    void getAllTags_failure() {
        GetAllCourseTagsRequest getAllCourseTagsRequest = TestGrpcStubGenerator.constructGetAllCourseTagsRequest();

        Mockito.when(courseTagsMapper.constructAllCourseTagsRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId)
        )).thenReturn(getAllCourseTagsRequest);

        Mockito.when(client.getAllTags(ArgumentMatchers.eq(getAllCourseTagsRequest)))
               .thenAnswer(invocation -> {
                   GetAllCourseTagsRequest request = invocation.getArgument(0, GetAllCourseTagsRequest.class);
                   throw new GrpcRetryException(TestConstantHolder.grpcExceptionText, request.getHeader().getRequestId());
               });

        Mockito.when(userService.getCurrentUserId()).thenReturn(TestConstantHolder.userId);

        Assertions.assertThatThrownBy(()-> redirectCourseTagService.getAllTags())
                  .isInstanceOf(GrpcRetryException.class)
                  .hasMessageContaining(TestConstantHolder.grpcExceptionText);

        Mockito.verify(client).getAllTags(ArgumentMatchers.eq(getAllCourseTagsRequest));
    }

    @Test
    void getTagById_success() {
        GetCourseTagRequest getCourseTagRequest = TestGrpcStubGenerator.constructGetCourseTagGrpcRequest();
        CourseTagResponse courseTagResponse = TestGrpcStubGenerator.constructCourseTagResponse();
        CourseTagDto dto = Mockito.mock(CourseTagDto.class);

        Mockito.when(courseTagsMapper.constructGrpcGetRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(TestConstantHolder.courseTagId)
        )).thenReturn(getCourseTagRequest);

        Mockito.when(client.getCourseTag(ArgumentMatchers.any(GetCourseTagRequest.class)))
               .thenReturn(courseTagResponse);
        Mockito.when(tagGrpcMapper.fromGrpc(courseTagResponse))
               .thenReturn(dto);
        Mockito.when(userService.getCurrentUserId())
               .thenReturn(TestConstantHolder.userId);

        CourseTagDto result = redirectCourseTagService.getTagById(TestConstantHolder.courseTagId);

        Assertions.assertThat(result).isEqualTo(dto);
        Mockito.verify(courseTagsMapper).constructGrpcGetRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(TestConstantHolder.courseTagId)
        );
        Mockito.verify(client).getCourseTag(ArgumentMatchers.any(GetCourseTagRequest.class));
        Mockito.verify(tagGrpcMapper).fromGrpc(courseTagResponse);
    }

    @Test
    void getTagById_failure() {
        GetCourseTagRequest getCourseTagRequest = TestGrpcStubGenerator.constructGetCourseTagGrpcRequest();

        Mockito.when(courseTagsMapper.constructGrpcGetRequest(
                ArgumentMatchers.any(),
                ArgumentMatchers.eq(TestConstantHolder.userId),
                ArgumentMatchers.eq(TestConstantHolder.courseTagId)
        )).thenReturn(getCourseTagRequest);

        Mockito.when(client.getCourseTag(ArgumentMatchers.eq(getCourseTagRequest)))
               .thenAnswer(invocation -> {
                   GetCourseTagRequest request = invocation.getArgument(0, GetCourseTagRequest.class);
                   throw new GrpcRetryException(TestConstantHolder.grpcExceptionText, request.getHeader().getRequestId());
               });

        Mockito.when(userService.getCurrentUserId()).thenReturn(TestConstantHolder.userId);

        Assertions.assertThatThrownBy(() -> redirectCourseTagService.getTagById(TestConstantHolder.courseTagId))
                  .isInstanceOf(GrpcRetryException.class)
                  .hasMessageContaining(TestConstantHolder.grpcExceptionText);
        Mockito.verify(client).getCourseTag(ArgumentMatchers.eq(getCourseTagRequest));
    }

}