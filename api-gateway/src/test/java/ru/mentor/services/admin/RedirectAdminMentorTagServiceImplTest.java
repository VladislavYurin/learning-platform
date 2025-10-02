package ru.mentor.services.admin;

import static ru.mentor.testUtil.TestConstantHolder.header;
import static ru.mentor.testUtil.TestConstantHolder.userId;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructListMentorTagDto;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagAttachResponseDto;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagDetachRequestDto;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagDetachResponseDto;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagDto;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagDtoCreateRequest;
import static ru.mentor.testUtil.TestEntityStubGenerator.constructMentorTagsAttachRequestDto;
import static ru.mentor.testUtil.TestGrpcStubGenerator.constructAllMentorTagsResponse;
import static ru.mentor.testUtil.TestGrpcStubGenerator.constructAttachMentorTagsResponse;
import static ru.mentor.testUtil.TestGrpcStubGenerator.constructDetachMentorTagResponse;
import static ru.mentor.testUtil.TestGrpcStubGenerator.constructMentorTagResponse;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.dto.mentorTag.MentorTagAttachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDetachRequestDto;
import ru.mentor.dto.mentorTag.MentorTagDetachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.dto.mentorTag.MentorTagDtoCreateRequest;
import ru.mentor.dto.mentorTag.MentorTagsAttachRequestDto;
import ru.mentor.exception.GrpcExceptionMapper;
import ru.mentor.factory.HeaderFactory;
import ru.mentor.grpc.AdminMentorTagsGrpcClient;
import ru.mentor.grpc.tags.AllMentorTagsRequset;
import ru.mentor.grpc.tags.AttachMentorTagsRequest;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;
import ru.mentor.mapper.MentorTagMapper;
import ru.mentor.services.UserService;
import ru.mentor.services.impl.RedirectAdminMentorTagServiceImpl;

@ExtendWith(MockitoExtension.class)
public class RedirectAdminMentorTagServiceImplTest {

    @Mock
    private AdminMentorTagsGrpcClient grpcClient;

    @Mock
    private HeaderFactory headerFactory;

    @Mock
    private UserService userService;

    @Spy
    private MentorTagMapper mapper = Mappers.getMapper(MentorTagMapper.class);

    @Spy
    private GrpcExceptionMapper exceptionMapper;

    @InjectMocks
    private RedirectAdminMentorTagServiceImpl service;

    @BeforeEach
    void setUp() {
        Mockito.when(headerFactory.create(ArgumentMatchers.anyString()))
               .thenReturn(header);

        Mockito.when(userService.getCurrentUserId())
               .thenReturn(userId);
    }

    @Test
    void allMentorTags_success() {
        List<MentorTagDto> expectedResult = constructListMentorTagDto();

        Mockito.when(grpcClient.getAllMentorTags(Mockito.any(AllMentorTagsRequset.class)))
               .thenReturn(constructAllMentorTagsResponse());

        List<MentorTagDto> result = service.allMentorTags();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedResult.size(), result.size());

        Mockito.verify(grpcClient).getAllMentorTags(Mockito.any());
    }

    @Test
    void allMentorTags_whenGrpcThrows_thenExceptionPropagates() {
        Mockito.when(grpcClient.getAllMentorTags(Mockito.any()))
               .thenThrow(new RuntimeException("gRPC error"));

        Assertions.assertThrows(
                RuntimeException.class,
                () -> service.allMentorTags()
        );

        Mockito.verify(grpcClient).getAllMentorTags(Mockito.any());
    }

    @Test
    void createCustomMentorTag_success() {
        MentorTagDtoCreateRequest request = constructMentorTagDtoCreateRequest();
        MentorTagDto expectedDto = constructMentorTagDto();

        Mockito.when(grpcClient.createCustomMentorTag(Mockito.any(CreateCustomMentorTagRequest.class)))
               .thenReturn(constructMentorTagResponse());

        MentorTagDto result = service.createCustomMentorTag(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedDto.getTagName(), result.getTagName());
        Mockito.verify(grpcClient).createCustomMentorTag(Mockito.any());
        Mockito.verify(headerFactory).create(ArgumentMatchers.anyString());
    }

    @Test
    void createCustomMentorTag_whenGrpcThrows_thenExceptionPropagates() {
        MentorTagDtoCreateRequest request = constructMentorTagDtoCreateRequest();

        Mockito.when(grpcClient.createCustomMentorTag(Mockito.any()))
               .thenThrow(new RuntimeException("gRPC error"));

        Assertions.assertThrows(
                RuntimeException.class,
                () -> service.createCustomMentorTag(request)
        );

        Mockito.verify(grpcClient).createCustomMentorTag(Mockito.any());
    }

    @Test
    void attachMentorTags_success() {
        MentorTagsAttachRequestDto request = constructMentorTagsAttachRequestDto();
        MentorTagAttachResponseDto expectedDto = constructMentorTagAttachResponseDto();

        Mockito.when(grpcClient.attachMentorTagsRequest(Mockito.any(AttachMentorTagsRequest.class)))
               .thenReturn(constructAttachMentorTagsResponse());

        MentorTagAttachResponseDto result = service.attachMentorTags(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedDto.getDidntAttached(), result.getDidntAttached());
        Assertions.assertEquals(expectedDto.getTagsIds(), result.getTagsIds());
        Mockito.verify(grpcClient).attachMentorTagsRequest(Mockito.any());
        Mockito.verify(headerFactory).create(ArgumentMatchers.anyString());
    }

    @Test
    void attachMentorTags_whenGrpcThrows_thenExceptionPropagates() {
        MentorTagsAttachRequestDto request = constructMentorTagsAttachRequestDto();

        Mockito.when(grpcClient.attachMentorTagsRequest(Mockito.any()))
               .thenThrow(new RuntimeException("gRPC error"));

        Assertions.assertThrows(
                RuntimeException.class,
                () -> service.attachMentorTags(request)
        );

        Mockito.verify(grpcClient).attachMentorTagsRequest(Mockito.any());
    }

    @Test
    void detachMentorTag_success() {
        MentorTagDetachRequestDto request = constructMentorTagDetachRequestDto();
        MentorTagDetachResponseDto expectedDto = constructMentorTagDetachResponseDto();

        Mockito.when(grpcClient.detachMentorTagResponse(Mockito.any(DetachMentorTagRequest.class)))
               .thenReturn(constructDetachMentorTagResponse());

        MentorTagDetachResponseDto result = service.detachMentorTag(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(expectedDto.getTagIds(), result.getTagIds());
        Mockito.verify(grpcClient).detachMentorTagResponse(Mockito.any());
        Mockito.verify(headerFactory).create(ArgumentMatchers.anyString());
    }

    @Test
    void detachMentorTag_whenGrpcThrows_thenExceptionPropagates() {
        MentorTagDetachRequestDto request = constructMentorTagDetachRequestDto();

        Mockito.when(grpcClient.detachMentorTagResponse(Mockito.any()))
               .thenThrow(new RuntimeException("gRPC error"));

        Assertions.assertThrows(
                RuntimeException.class,
                () -> service.detachMentorTag(request)
        );

        Mockito.verify(grpcClient).detachMentorTagResponse(Mockito.any());
    }

}

