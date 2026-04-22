package ru.mentor.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import ru.mentor.common.Header;
import ru.mentor.common.MentorTag;
import ru.mentor.common.MentorTagTypeProto;
import ru.mentor.constant.MentorTagType;
import ru.mentor.dto.mentorTag.MentorTagAttachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDetachRequestDto;
import ru.mentor.dto.mentorTag.MentorTagDetachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.dto.mentorTag.MentorTagDtoCreateRequest;
import ru.mentor.dto.mentorTag.MentorTagsAttachRequestDto;
import ru.mentor.entity.MentorTagEntity;
import ru.mentor.grpc.tags.AllMentorTagsResponse;
import ru.mentor.grpc.tags.AttachMentorTagsRequest;
import ru.mentor.grpc.tags.AttachMentorTagsResponse;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagResponse;
import ru.mentor.grpc.tags.GetCurrentMentorTagsRequest;
import ru.mentor.grpc.tags.MentorTagResponse;
import ru.mentor.grpc.tags.MentorTagsResponse;

@Mapper(
        componentModel = "spring",
        uses = MentorTagUtilMapper.class
)

public interface MentorTagMapper {

    MentorTagDto mentorTagEntityToDto(MentorTagEntity entity);

    @Mapping(target = "name", source = "tagName")
    MentorTag mentorTagDtoToGrpc(MentorTagDto dto);

    @Mapping(target = "tagName", source = "name")
    MentorTagDto mentorTagGrpcToDto(MentorTag mentorTag);

    @Mapping(target = "tagsIds", source = "attachedTagIdsList")
    @Mapping(target = "didntAttached", source = "notAttachedTagIdsList")
    MentorTagAttachResponseDto attachResponseGrpcToDto(AttachMentorTagsResponse response);

    @Mapping(target = "mentorId", source = "mentorId")
    @Mapping(target = "tagIds", source = "tagId")
    MentorTagDetachResponseDto detachResponseGrpcToDto(DetachMentorTagResponse response);

    @Mapping(target = "id", source = "mentorTag.id")
    @Mapping(target = "tagName", source = "mentorTag.name")
    @Mapping(target = "type", source = "mentorTag.type")
    MentorTagDto responseToMentorTagDto(MentorTagResponse response);

    default AllMentorTagsResponse toAllMentorTagsResponse(String rqUid, List<MentorTagDto> tags) {
        return AllMentorTagsResponse.newBuilder()
                                    .setRqUid(rqUid)
                                    .addAllAllMentorsTags(
                                            tags.stream()
                                                .map(this::mentorTagDtoToGrpc)
                                                .toList()
                                    )
                                    .build();
    }

    default GetCurrentMentorTagsRequest toCurrentMentorTagsRequest(Header header, Long mentorId) {
        return GetCurrentMentorTagsRequest.newBuilder()
                                          .setHeader(header)
                                          .setMentorId(mentorId)
                                          .build();
    }

    default MentorTagsResponse toCurrentMentorTagsResponse(String rqUid, List<MentorTagDto> tags) {
        Map<MentorTagType, Integer> priority = Map.of(
                MentorTagType.DIRECTION, 0,
                MentorTagType.BADGE, 1
        );

        Comparator<MentorTag> comparator =
                Comparator.comparingInt(
                        tag -> priority.getOrDefault(
                                tag.getType(),
                                Integer.MAX_VALUE
                        )
                );

        return MentorTagsResponse.newBuilder()
                                 .setRqUid(rqUid)
                                 .addAllMentorTags(
                                         tags.stream()
                                             .map(this::mentorTagDtoToGrpc)
                                             .sorted(comparator)
                                             .toList()
                                 )
                                 .build();
    }

    default List<MentorTagDto> toListMentorTagDto(MentorTagsResponse response) {
        return response.getMentorTagsList().stream()
                       .map(this::mentorTagGrpcToDto)
                       .toList();
    }

    default AttachMentorTagsResponse toAttachMentorTagsResponse(
            String rqUid,
            List<Long> attachedTagsIds,
            List<Long> didntAttachedIds) {
        return AttachMentorTagsResponse.newBuilder()
                                       .setRqUid(rqUid)
                                       .addAllAttachedTagIds(attachedTagsIds)
                                       .addAllNotAttachedTagIds(didntAttachedIds)
                                       .build();
    }

    ;

    default AttachMentorTagsRequest toAttachMentorTagsRequest(
            MentorTagsAttachRequestDto requestDto,
            Header header) {
        AttachMentorTagsRequest out = AttachMentorTagsRequest.newBuilder()
                                                             .setHeader(header)
                                                             .setMentorId(requestDto.getMentorId())
                                                             .addAllTagIds(requestDto.getTagsIds())
                                                             .build();

        return out;
    }

    @Mapping(target = "rqUid", source = "rqUid")
    @Mapping(target = "mentorId", source = "mentorId")
    @Mapping(target = "tagId", source = "tagId")
    DetachMentorTagResponse toDetachMentorTagResponse(String rqUid, Long mentorId, Long tagId);

    default DetachMentorTagRequest toDetachMentorTagRequest(
            MentorTagDetachRequestDto dettachRequestDto,
            Header header) {
        return DetachMentorTagRequest.newBuilder()
                                     .setHeader(header)
                                     .setMentorId(dettachRequestDto.getMentorId())
                                     .setTagId(dettachRequestDto.getTagId())
                                     .build();
    }

    @Mapping(target = "tagName", source = "name")
    @Mapping(target = "type", constant = "BADGE")
    MentorTagEntity toMentorTagEntity(String name);

    default CreateCustomMentorTagRequest toCreateCustomMentorTagRequest(
            MentorTagDtoCreateRequest createRequest,
            Header header) {
        return CreateCustomMentorTagRequest.newBuilder()
                                           .setHeader(header)
                                           .setName(createRequest.getTagName())
                                           .build();
    }

    @Mapping(target = "rqUid", source = "rqUid")
    @Mapping(target = "mentorTag", source = "createdMentorTag")
    MentorTagResponse toMetorTagResponse(String rqUid, MentorTagDto createdMentorTag);

    @ValueMapping(source = MappingConstants.NULL, target = "UNRECOGNIZED")
    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.THROW_EXCEPTION)
    MentorTagTypeProto tagTypeGrpcToEnum(MentorTagType type);

    @ValueMapping(source = MappingConstants.ANY_REMAINING, target = MappingConstants.THROW_EXCEPTION)
    MentorTagType tagTypeEnumToGrpc(MentorTagTypeProto type);

    List<MentorTag> mapListDtoToGrpc(List<MentorTagDto> value);

}
