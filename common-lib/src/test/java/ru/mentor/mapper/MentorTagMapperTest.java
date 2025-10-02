package ru.mentor.mapper;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.mentor.common.MentorTag;
import ru.mentor.dto.mentorTag.MentorTagAttachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDetachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.entity.MentorTagEntity;
import ru.mentor.grpc.tags.AllMentorTagsResponse;
import ru.mentor.grpc.tags.AttachMentorTagsRequest;
import ru.mentor.grpc.tags.AttachMentorTagsResponse;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagResponse;
import ru.mentor.grpc.tags.MentorTagResponse;
import ru.mentor.grpc.tags.MentorTagsResponse;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;
import ru.mentor.testUtil.TestGrpcStubGenerator;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        MentorTagMapperImpl.class,
        MentorTagUtilMapper.class
})
public class MentorTagMapperTest {

    @Autowired
    private MentorTagMapper mapper;

    @Test
    void shouldMap_mentorTagEntityToDto() {
        MentorTagDto dto = mapper.mentorTagEntityToDto(TestEntityStubGenerator.constructTestMentorTagEntity());
        MentorTagDto expDto = TestEntityStubGenerator.constructMentorTagDto();

        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getId()).isEqualTo(expDto.getId());
        Assertions.assertThat(dto.getTagName()).isEqualTo(expDto.getTagName());
        Assertions.assertThat(dto.getType()).isEqualTo(expDto.getType());
    }

    @Test
    void shouldMap_mentorTagDtoToGrpc() {
        MentorTag tag = mapper.mentorTagDtoToGrpc(TestEntityStubGenerator.constructMentorTagDto());
        MentorTag expTag = TestGrpcStubGenerator.constructMentorTag();

        Assertions.assertThat(tag).isNotNull();
        Assertions.assertThat(tag.getId()).isEqualTo(expTag.getId());
        Assertions.assertThat(tag.getName()).isEqualTo(expTag.getName());
        Assertions.assertThat(tag.getType()).isEqualTo(expTag.getType());
    }

    @Test
    void shouldMap_mentorTagGrpcToDto() {
        MentorTagDto dto = mapper.mentorTagGrpcToDto(TestGrpcStubGenerator.constructMentorTag());
        MentorTagDto expDto = TestEntityStubGenerator.constructMentorTagDto();

        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(dto.getId()).isEqualTo(expDto.getId());
        Assertions.assertThat(dto.getTagName()).isEqualTo(expDto.getTagName());
        Assertions.assertThat(dto.getType()).isEqualTo(expDto.getType());
    }

    @Test
    void shouldMap_attachResponseGrpcToDto() {
        MentorTagAttachResponseDto attachResponseDto = mapper.attachResponseGrpcToDto(TestGrpcStubGenerator.constructAttachMentorTagsResponse());
        MentorTagAttachResponseDto expAttachResponseDto = TestEntityStubGenerator.constructMentorTagAttachResponseDto();

        Assertions.assertThat(attachResponseDto).isNotNull();
        Assertions.assertThat(attachResponseDto.getRqUid()).isEqualTo(expAttachResponseDto.getRqUid());
        Assertions.assertThat(attachResponseDto.getTagsIds()).isEqualTo(expAttachResponseDto.getTagsIds());
        Assertions.assertThat(attachResponseDto.getDidntAttached()).isEqualTo(expAttachResponseDto.getDidntAttached());
    }

    @Test
    void shouldMap_detachResponseGrpcToDto() {
        MentorTagDetachResponseDto detachResponseDto = mapper.detachResponseGrpcToDto(TestGrpcStubGenerator.constructDetachMentorTagResponse());
        MentorTagDetachResponseDto expDetachResponseDto = TestEntityStubGenerator.constructMentorTagDetachResponseDto();

        Assertions.assertThat(detachResponseDto).isNotNull();
        Assertions.assertThat(detachResponseDto.getRqUid()).isEqualTo(expDetachResponseDto.getRqUid());
        Assertions.assertThat(detachResponseDto.getMentorId()).isEqualTo(expDetachResponseDto.getMentorId());
        Assertions.assertThat(detachResponseDto.getTagIds()).isEqualTo(expDetachResponseDto.getTagIds());
    }

    @Test
    void shouldMap_responseToMentorTagDto() {
        MentorTagDto mentorTagDto = mapper.responseToMentorTagDto(TestGrpcStubGenerator.constructMentorTagResponse());
        MentorTagDto expDto = TestEntityStubGenerator.constructMentorTagDto();

        Assertions.assertThat(mentorTagDto).isNotNull();
        Assertions.assertThat(mentorTagDto.getId()).isEqualTo(expDto.getId());
        Assertions.assertThat(mentorTagDto.getTagName()).isEqualTo(expDto.getTagName());
        Assertions.assertThat(mentorTagDto.getType()).isEqualTo(expDto.getType());
    }

    @Test
    void shouldMap_toAllMentorTagsResponse() {
        AllMentorTagsResponse allMentorTagsResponse = mapper.toAllMentorTagsResponse(
                TestConstantHolder.requestId,
                TestEntityStubGenerator.constructListMentorTagDto()
        );
        AllMentorTagsResponse expResponse = TestGrpcStubGenerator.constructAllMentorTagsResponse();

        Assertions.assertThat(allMentorTagsResponse).isNotNull();
        Assertions.assertThat(allMentorTagsResponse.getRqUid()).isEqualTo(expResponse.getRqUid());
        Assertions.assertThat(allMentorTagsResponse.getAllMentorsTagsList()).isEqualTo(expResponse.getAllMentorsTagsList());
        Assertions.assertThat(allMentorTagsResponse.getAllMentorsTagsCount()).isEqualTo(expResponse.getAllMentorsTagsCount());
    }

    @Test
    void shouldMap_toCurrentMentorTagsResponse() {
        MentorTagsResponse mentorTagsResponse = mapper.toCurrentMentorTagsResponse(
                TestConstantHolder.requestId,
                TestEntityStubGenerator.constructListMentorTagDto()
        );
        MentorTagsResponse expResponse = TestGrpcStubGenerator.constructMentorTagsResponse();

        Assertions.assertThat(mentorTagsResponse).isNotNull();
        Assertions.assertThat(mentorTagsResponse.getRqUid()).isEqualTo(expResponse.getRqUid());
        Assertions.assertThat(mentorTagsResponse.getMentorTagsList()).isEqualTo(expResponse.getMentorTagsList());
        Assertions.assertThat(mentorTagsResponse.getMentorTagsCount()).isEqualTo(expResponse.getMentorTagsCount());
    }

    @Test
    void shouldMap_toAttachMentorTagsResponse() {
        AttachMentorTagsResponse attachMentorTagsResponse = mapper.toAttachMentorTagsResponse(
                TestConstantHolder.requestId,
                TestConstantHolder.mentorTagsIds,
                TestConstantHolder.mentorTagsIds
        );
        AttachMentorTagsResponse expResponse = TestGrpcStubGenerator.constructAttachMentorTagsResponse();

        Assertions.assertThat(attachMentorTagsResponse).isNotNull();
        Assertions.assertThat(attachMentorTagsResponse.getRqUid()).isEqualTo(expResponse.getRqUid());
        Assertions.assertThat(attachMentorTagsResponse.getAttachedTagIdsList()).isEqualTo(expResponse.getAttachedTagIdsList());
        Assertions.assertThat(attachMentorTagsResponse.getNotAttachedTagIdsList()).isEqualTo(expResponse.getNotAttachedTagIdsList());
        Assertions.assertThat(attachMentorTagsResponse.getAttachedTagIdsCount()).isEqualTo(expResponse.getAttachedTagIdsCount());
        Assertions.assertThat(attachMentorTagsResponse.getAttachedTagIdsCount()).isEqualTo(expResponse.getAttachedTagIdsCount());
    }

    @Test
    void shouldMap_toAttachMentorTagsRequest() {
        AttachMentorTagsRequest attachMentorTagsRequest = mapper.toAttachMentorTagsRequest(
                TestEntityStubGenerator.constructMentorTagsAttachRequestDto(),
                TestConstantHolder.header
        );
        AttachMentorTagsRequest expRequest = TestGrpcStubGenerator.constructAttachMentorTagsRequest();

        Assertions.assertThat(attachMentorTagsRequest).isNotNull();
        Assertions.assertThat(attachMentorTagsRequest.getHeader()).isEqualTo(expRequest.getHeader());
        Assertions.assertThat(attachMentorTagsRequest.getMentorId()).isEqualTo(expRequest.getMentorId());
        Assertions.assertThat(attachMentorTagsRequest.getTagIdsList()).isEqualTo(expRequest.getTagIdsList());
        Assertions.assertThat(attachMentorTagsRequest.getTagIdsCount()).isEqualTo(expRequest.getTagIdsCount());
    }

    @Test
    void shouldMap_toDetachMentorTagResponse() {
        DetachMentorTagResponse detachMentorTagResponse = mapper.toDetachMentorTagResponse(
                TestConstantHolder.requestId,
                TestConstantHolder.mentorId,
                TestConstantHolder.tagId
        );
        DetachMentorTagResponse expResponse = TestGrpcStubGenerator.constructDetachMentorTagResponse();

        Assertions.assertThat(detachMentorTagResponse).isNotNull();
        Assertions.assertThat(detachMentorTagResponse.getRqUid()).isEqualTo(expResponse.getRqUid());
        Assertions.assertThat(detachMentorTagResponse.getMentorId()).isEqualTo(expResponse.getMentorId());
        Assertions.assertThat(detachMentorTagResponse.getTagId()).isEqualTo(expResponse.getTagId());
    }

    @Test
    void shouldMap_toDetachMentorTagRequest() {
        DetachMentorTagRequest detachMentorTagRequest = mapper.toDetachMentorTagRequest(
                TestEntityStubGenerator.constructMentorTagDetachRequestDto(),
                TestConstantHolder.header
        );

        DetachMentorTagRequest expRequest = TestGrpcStubGenerator.constructDetachMentorTagRequest();

        Assertions.assertThat(detachMentorTagRequest).isNotNull();
        Assertions.assertThat(detachMentorTagRequest.getHeader().getRequestId()).isEqualTo(expRequest.getHeader().getRequestId());
        Assertions.assertThat(detachMentorTagRequest.getMentorId()).isEqualTo(expRequest.getMentorId());
        Assertions.assertThat(detachMentorTagRequest.getTagId()).isEqualTo(expRequest.getTagId());
    }

    @Test
    void shouldMap_toMentorTagEntity() {
        MentorTagEntity mentorTagEntity = mapper.toMentorTagEntity(TestConstantHolder.mentorTagNameBadge);

        MentorTagEntity expEntity = TestEntityStubGenerator.constructTestMentorTagEntityBadge();

        Assertions.assertThat(mentorTagEntity).isNotNull();
        Assertions.assertThat(mentorTagEntity.getTagName()).isEqualTo(expEntity.getTagName());
        Assertions.assertThat(mentorTagEntity.getType()).isEqualTo(expEntity.getType());
    }

    @Test
    void shouldMap_toCreateCustomMentorTagRequest() {
        CreateCustomMentorTagRequest customMentorTagRequest = mapper.toCreateCustomMentorTagRequest(
                TestEntityStubGenerator.constructMentorTagDtoCreateRequest(),
                TestConstantHolder.header
        );

        CreateCustomMentorTagRequest expRequset = TestGrpcStubGenerator.constructCreateCustomMentorTagRequest();

        Assertions.assertThat(customMentorTagRequest).isNotNull();
        Assertions.assertThat(customMentorTagRequest.getName()).isEqualTo(expRequset.getName());
        Assertions.assertThat(customMentorTagRequest.getHeader()).isEqualTo(expRequset.getHeader());
    }

    @Test
    void shouldMap_toMetorTagResponse() {
        MentorTagResponse mentorTagResponse = mapper.toMetorTagResponse(TestConstantHolder.requestId, TestEntityStubGenerator.constructMentorTagDto());

        MentorTagResponse expResponse = TestGrpcStubGenerator.constructMentorTagResponse();

        Assertions.assertThat(mentorTagResponse).isNotNull();
        Assertions.assertThat(mentorTagResponse.getMentorTag()).isEqualTo(expResponse.getMentorTag());
        Assertions.assertThat(mentorTagResponse.getRqUid()).isEqualTo(expResponse.getRqUid());
    }

}
