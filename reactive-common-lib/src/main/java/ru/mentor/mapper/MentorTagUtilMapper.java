package ru.mentor.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.mentor.constant.MentorTagType;
import ru.mentor.dto.mentorTag.MentorTagDto;

@Component
@Named("ReactiveMentorTagUtilMapper")
public class MentorTagUtilMapper {

    @Named("mentorTagDtoSortByType")
    public List<MentorTagDto> mentorTagDtoSortByType(List<MentorTagDto> listOfDto){
        Map<MentorTagType, Integer> priority = Map.of(
                MentorTagType.DIRECTION, 0,
                MentorTagType.BADGE, 1
        );

        Comparator<MentorTagDto> comparator =
                Comparator.comparingInt(
                        tag -> priority.getOrDefault(
                                tag.getType(),
                                Integer.MAX_VALUE
                        )
                );

        return listOfDto.stream()
                        .sorted(comparator)
                        .toList();
    }
}
