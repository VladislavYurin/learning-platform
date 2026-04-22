package ru.mentor.services;

import java.util.List;
import org.springframework.stereotype.Service;
import ru.mentor.dto.mentorTag.MentorTagAttachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDetachRequestDto;
import ru.mentor.dto.mentorTag.MentorTagDetachResponseDto;
import ru.mentor.dto.mentorTag.MentorTagDto;
import ru.mentor.dto.mentorTag.MentorTagDtoCreateRequest;
import ru.mentor.dto.mentorTag.MentorTagsAttachRequestDto;

@Service
public interface RedirectAdminMentorTagService {
    MentorTagDto createCustomMentorTag(MentorTagDtoCreateRequest createMentorTagRequset);

    List<MentorTagDto> allMentorTags();

    MentorTagAttachResponseDto attachMentorTags(MentorTagsAttachRequestDto attachRequest);

    MentorTagDetachResponseDto detachMentorTag(MentorTagDetachRequestDto dettachRequest);
}
