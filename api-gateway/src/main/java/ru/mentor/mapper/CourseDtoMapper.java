package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.mentor.dto.CourseDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateTimeMapper.class)
public interface CourseDtoMapper {

    ru.mentor.dto.CourseDto toCommon(ru.mentor.gateway.model.CourseDto apiDto);

    ru.mentor.gateway.model.CourseDto toApi(ru.mentor.dto.CourseDto commonDto);

    List<ru.mentor.gateway.model.CourseDto> toApiList(List<CourseDto> list);

    List<ru.mentor.dto.CourseDto> toCommonList(List<ru.mentor.gateway.model.CourseDto> list);

    default Page<ru.mentor.gateway.model.CourseDto> toApiPage(Page<ru.mentor.dto.CourseDto> commonPage) {
        List<ru.mentor.gateway.model.CourseDto> content = toApiList(commonPage.getContent());
        return new PageImpl<>(content, commonPage.getPageable(), commonPage.getTotalElements());
    }
}
