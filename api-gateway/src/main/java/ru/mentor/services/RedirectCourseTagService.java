package ru.mentor.services;

import java.util.List;
import ru.mentor.dto.tag.CourseTagDto;
import ru.mentor.dto.tag.CreateCourseTagRequest;

public interface RedirectCourseTagService {

    CourseTagDto createCourseTag(CreateCourseTagRequest request);
    void deleteCourseTag(Long tagId);
    List<CourseTagDto> getAllTags();
    CourseTagDto getTagById(Long tagId);

}
