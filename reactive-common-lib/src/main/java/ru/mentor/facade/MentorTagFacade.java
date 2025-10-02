package ru.mentor.facade;

import java.util.List;
import reactor.core.publisher.Mono;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.grpc.tags.AllMentorTagsRequset;
import ru.mentor.grpc.tags.AttachMentorTagsRequest;
import ru.mentor.grpc.tags.CreateCustomMentorTagRequest;
import ru.mentor.grpc.tags.DetachMentorTagRequest;

public interface MentorTagFacade {
    Mono<MentorTagDto> createCustomMentorTag (CreateCustomMentorTagRequest request);

    Mono<List<MentorTagDto>> allMentorTags (AllMentorTagsRequset requset);

    Mono<List<Long>> attachMentorTags (AttachMentorTagsRequest request);

    Mono<Void> detachMentorTag (DetachMentorTagRequest request);
}
