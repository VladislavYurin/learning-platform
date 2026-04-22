package ru.mentor.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.exception.EntityAlreadyExistsException;
import ru.mentor.exception.EntityNotFoundException;
import ru.mentor.grpc.tags.AllMentorTagsRequset;
import ru.mentor.grpc.tags.AllMentorTagsResponse;
import ru.mentor.grpc.tags.AttachMentorTagsRequest;
import ru.mentor.grpc.tags.AttachMentorTagsResponse;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagResponse;
import ru.mentor.grpc.tags.GetCurrentMentorTagsRequest;
import ru.mentor.grpc.tags.MentorTagResponse;
import ru.mentor.grpc.tags.MentorTagsResponse;
import ru.mentor.mapper.MentorTagMapperImpl;
import ru.mentor.service.impl.MentorTagServiceImpl;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;

@ExtendWith(MockitoExtension.class)
class MentorTagsGrpcServerTest {

    @Mock
    MentorTagServiceImpl mentorTagService;

    @Spy
    MentorTagMapperImpl mapper;

    @InjectMocks
    MentorTagsGrpcServer grpc;

    MentorTagDto mentorTagDto = TestEntityStubGenerator.constructMentorTagDto();

    @Test
    void createCustomMentorTag_ok() {
        StreamObserver<MentorTagResponse> responseObserver = Mockito.mock(StreamObserver.class);
        CreateCustomMentorTagRequest request = TestGrpcStubGenerator.constructCreateCustomMentorTagRequest();

        Mockito.when(mentorTagService.createCustomMentorTag(
                       Mockito.anyString()
               ))
               .thenReturn(mentorTagDto);

        grpc.createCustomMentorTag(
                request,
                responseObserver
        );

        ArgumentCaptor<MentorTagResponse> responseCaptor = ArgumentCaptor.forClass(MentorTagResponse.class);
        Mockito.verify(responseObserver).onNext(responseCaptor.capture());
        Mockito.verify(responseObserver).onCompleted();

        MentorTagResponse response = responseCaptor.getValue();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(request.getHeader().getRequestId(), response.getRqUid());
        Assertions.assertEquals(mentorTagDto.getId(), response.getMentorTag().getId());
        Assertions.assertEquals(mentorTagDto.getTagName(), response.getMentorTag().getName());
        Assertions.assertEquals(
                mentorTagDto.getType().name(),
                response.getMentorTag().getType().name()
        );
    }

    @Test
    void createCustromMentorTag_tagNameAlreadyExist_throwException() {
        StreamObserver<MentorTagResponse> responseObserver = Mockito.mock(StreamObserver.class);
        CreateCustomMentorTagRequest request = TestGrpcStubGenerator.constructCreateCustomMentorTagRequest();

        Mockito.when(mentorTagService.createCustomMentorTag(Mockito.any()))
               .thenThrow(new EntityAlreadyExistsException("Entity already exist"));

        grpc.createCustomMentorTag(
                request,
                responseObserver
        );

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(responseObserver).onError(errorCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(Mockito.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();
        Mockito.verifyNoInteractions(mapper);

        Throwable throwable = errorCaptor.getValue();
        Assertions.assertInstanceOf(StatusRuntimeException.class, throwable);
        StatusRuntimeException exception = (StatusRuntimeException) throwable;
        Assertions.assertEquals(
                Status.ALREADY_EXISTS.getCode(),
                exception.getStatus().getCode()
        );
        Assertions.assertEquals(
                "Entity already exist",
                exception.getStatus().getDescription()
        );
    }

    @Test
    void listMentorTags_ok() {
        StreamObserver<AllMentorTagsResponse> responseObserver = Mockito.mock(StreamObserver.class);
        AllMentorTagsRequset request = TestGrpcStubGenerator.constructAllMentorTagsRequest();

        Mockito.when(mentorTagService.getAllTags()).thenReturn(
                TestEntityStubGenerator.constructListMentorTagDto()
        );

        grpc.listMentorTags(request, responseObserver);

        ArgumentCaptor<AllMentorTagsResponse> responseCaptor = ArgumentCaptor.forClass(
                AllMentorTagsResponse.class);
        Mockito.verify(responseObserver).onNext(responseCaptor.capture());
        Mockito.verify(responseObserver).onCompleted();

        AllMentorTagsResponse response = responseCaptor.getValue();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(request.getHeader().getRequestId(), response.getRqUid());
        Assertions.assertEquals(2, response.getAllMentorsTagsCount());
    }

    @Test
    void listMentorTags_internalErr_mapsToInternal() {
        StreamObserver<AllMentorTagsResponse> responseObserver = Mockito.mock(StreamObserver.class);
        AllMentorTagsRequset request = TestGrpcStubGenerator.constructAllMentorTagsRequest();

        Mockito.when(mentorTagService.getAllTags()).thenThrow(new RuntimeException("boom"));

        grpc.listMentorTags(request, responseObserver);

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(responseObserver).onError(errorCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(Mockito.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();
        Mockito.verifyNoInteractions(mapper);

        Throwable throwable = errorCaptor.getValue();
        Assertions.assertInstanceOf(StatusRuntimeException.class, throwable);
        StatusRuntimeException exception = (StatusRuntimeException) throwable;
        Assertions.assertEquals(
                Status.INTERNAL.getCode(),
                exception.getStatus().getCode()
        );
        Assertions.assertEquals(
                "boom",
                exception.getStatus().getDescription()
        );
    }

    @Test
    void getMentorTags_ok() {
        StreamObserver<MentorTagsResponse> responseObserver = Mockito.mock(StreamObserver.class);
        GetCurrentMentorTagsRequest request = TestGrpcStubGenerator.constructGetCurrentMentorTagsRequest();

        Mockito.when(mentorTagService.getMentorTags(Mockito.any()))
               .thenReturn(TestEntityStubGenerator.constructListMentorTagDto());

        grpc.getMentorTags(request, responseObserver);

        ArgumentCaptor<MentorTagsResponse> responseCaptor = ArgumentCaptor.forClass(
                MentorTagsResponse.class);
        Mockito.verify(responseObserver).onNext(responseCaptor.capture());
        Mockito.verify(responseObserver).onCompleted();

        MentorTagsResponse response = responseCaptor.getValue();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(request.getHeader().getRequestId(), response.getRqUid());
        Assertions.assertEquals(2, response.getMentorTagsCount());
    }

    @Test
    void getMentorTags_whenMentorNotFound_returnsNotFound() {
        StreamObserver<MentorTagsResponse> responseObserver = Mockito.mock(StreamObserver.class);
        GetCurrentMentorTagsRequest request = TestGrpcStubGenerator.constructGetCurrentMentorTagsRequest();

        Mockito.when(mentorTagService.getMentorTags(Mockito.any()))
               .thenThrow(new EntityNotFoundException("Пользователь не найден"));

        grpc.getMentorTags(request, responseObserver);

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(responseObserver).onError(errorCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(Mockito.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();
        Mockito.verifyNoInteractions(mapper);

        Throwable throwable = errorCaptor.getValue();
        Assertions.assertInstanceOf(StatusRuntimeException.class, throwable);
        StatusRuntimeException exception = (StatusRuntimeException) throwable;
        Assertions.assertEquals(
                Status.NOT_FOUND.getCode(),
                exception.getStatus().getCode()
        );
        Assertions.assertEquals(
                "Пользователь не найден",
                exception.getStatus().getDescription()
        );
    }

    @Test
    void attachMentorTags_ok() {
        StreamObserver<AttachMentorTagsResponse> responseObserver = Mockito.mock(StreamObserver.class);
        AttachMentorTagsRequest request = TestGrpcStubGenerator.constructAttachMentorTagsRequest();

        Mockito.when(mentorTagService.attachTags(Mockito.any(), Mockito.any()))
               .thenReturn(TestEntityStubGenerator.constructListMentorTagLinkEntity());

        grpc.attachMentorTags(request, responseObserver);

        ArgumentCaptor<AttachMentorTagsResponse> responseCaptor = ArgumentCaptor.forClass(
                AttachMentorTagsResponse.class);
        Mockito.verify(responseObserver).onNext(responseCaptor.capture());
        Mockito.verify(responseObserver).onCompleted();

        AttachMentorTagsResponse response = responseCaptor.getValue();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(request.getHeader().getRequestId(), response.getRqUid());
        Assertions.assertEquals(response.getAttachedTagIdsList().getFirst(), 1);
        Assertions.assertEquals(response.getNotAttachedTagIdsList().getFirst(), 2);
        Assertions.assertEquals(response.getNotAttachedTagIdsList().getLast(), 4);
    }

    @Test
    void attachMentorTags_whenMentorNotFound_returnsNotFound() {
        StreamObserver<AttachMentorTagsResponse> responseObserver = Mockito.mock(StreamObserver.class);
        AttachMentorTagsRequest request = TestGrpcStubGenerator.constructAttachMentorTagsRequest();

        Mockito.when(mentorTagService.attachTags(Mockito.any(), Mockito.any()))
               .thenThrow(new EntityNotFoundException("Пользователь не найден"));

        grpc.attachMentorTags(request, responseObserver);

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(responseObserver).onError(errorCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(Mockito.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();
        Mockito.verifyNoInteractions(mapper);

        Throwable throwable = errorCaptor.getValue();
        Assertions.assertInstanceOf(StatusRuntimeException.class, throwable);
        StatusRuntimeException exception = (StatusRuntimeException) throwable;
        Assertions.assertEquals(
                Status.NOT_FOUND.getCode(),
                exception.getStatus().getCode()
        );
        Assertions.assertEquals(
                "Пользователь не найден",
                exception.getStatus().getDescription()
        );
    }

    @Test
    void detachMentorTags_ok() {
        StreamObserver<DetachMentorTagResponse> responseObserver = Mockito.mock(StreamObserver.class);
        DetachMentorTagRequest request = TestGrpcStubGenerator.constructDetachMentorTagRequest();

        Mockito.doNothing().when(mentorTagService).detachTag(Mockito.any(), Mockito.any());

        grpc.detachMentorTag(request, responseObserver);

        ArgumentCaptor<DetachMentorTagResponse> responseCaptor = ArgumentCaptor.forClass(
                DetachMentorTagResponse.class);
        Mockito.verify(responseObserver).onNext(responseCaptor.capture());
        Mockito.verify(responseObserver).onCompleted();

        DetachMentorTagResponse response = responseCaptor.getValue();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(request.getHeader().getRequestId(), response.getRqUid());
        Assertions.assertEquals(response.getMentorId(), request.getMentorId());
        Assertions.assertEquals(response.getTagId(), request.getTagId());
    }

    @Test
    void detachMentorTags_whenMentorNotFound_returnsNotFound() {
        StreamObserver<DetachMentorTagResponse> responseObserver = Mockito.mock(StreamObserver.class);
        DetachMentorTagRequest request = TestGrpcStubGenerator.constructDetachMentorTagRequest();

        Mockito.doThrow(new EntityNotFoundException("Пользователь не найден"))
               .when(mentorTagService)
               .detachTag(Mockito.any(), Mockito.any());

        grpc.detachMentorTag(request, responseObserver);

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
        Mockito.verify(responseObserver).onError(errorCaptor.capture());
        Mockito.verify(responseObserver, Mockito.never()).onNext(Mockito.any());
        Mockito.verify(responseObserver, Mockito.never()).onCompleted();
        Mockito.verifyNoInteractions(mapper);

        Throwable throwable = errorCaptor.getValue();
        Assertions.assertInstanceOf(StatusRuntimeException.class, throwable);
        StatusRuntimeException exception = (StatusRuntimeException) throwable;
        Assertions.assertEquals(
                Status.NOT_FOUND.getCode(),
                exception.getStatus().getCode()
        );
        Assertions.assertEquals(
                "Пользователь не найден",
                exception.getStatus().getDescription()
        );
    }

}
