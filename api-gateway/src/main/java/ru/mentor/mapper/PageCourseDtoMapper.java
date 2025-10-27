package ru.mentor.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.mentor.gateway.model.CourseDto;
import ru.mentor.gateway.model.PageCourseDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PageCourseDtoMapper {

    List<CourseDto> mapContent(List<CourseDto> content);

    default PageCourseDto toDto(Page<CourseDto> page) {
        PageCourseDto dto = new PageCourseDto();
        dto.setContent(mapContent(page.getContent()));
        dto.setNumber(page.getNumber());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());
        return dto;
    }

    default Page<CourseDto> fromDto(PageCourseDto dto) {
        List<CourseDto> content = dto.getContent();
        int pageNumber = dto.getNumber() != null ? dto.getNumber() : 0;
        int pageSize = dto.getSize() != null ? dto.getSize() : content.size();
        long totalElements = dto.getTotalElements() != null ? dto.getTotalElements() : content.size();

        return new PageImpl<>(
                content,
                PageRequest.of(pageNumber, pageSize),
                totalElements
        );
    }
}
