package ru.mentor.mapper;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.SubmoduleDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.SubmoduleEntity;

@Component
@RequiredArgsConstructor
public class BaseMapper {

    public List<CourseDto> mapCourses(
            List<CourseEntity> entities,
            Boolean isNeedToFetchModules,
            Boolean isNeedToFetchSubmodules) {
        return entities.stream()
                       .map(entity -> mapCourse(
                               entity,
                               isNeedToFetchModules,
                               isNeedToFetchSubmodules
                       ))
                       .toList();
    }

    public CourseDto mapCourse(
            CourseEntity entity,
            Boolean isNeedToFetchInnerEntities,
            Boolean isNeedToFetchSubmodules) {
        return CourseDto.builder()
                        .id(entity.getId())
                        .courseTitle(entity.getCourseTitle())
                        .courseDescription(entity.getDescription())
                        .isActive(entity.getIsActive())
                        .authorId(entity.getAuthor().getId())
                        .modules(
                                isNeedToFetchInnerEntities ? mapModules(
                                        entity.getModules(),
                                        isNeedToFetchSubmodules
                                ) : null)
                        .build();
    }

    public List<ModuleDto> mapModules(
            List<ModuleEntity> entities,
            Boolean isNeedToFetchSubmodules) {
        return entities.stream()
                       .map(module -> mapModule(module, isNeedToFetchSubmodules))
                       .sorted(Comparator.comparingInt(ModuleDto::getModuleOrderNumber))
                       .toList();
    }

    public ModuleDto mapModule(ModuleEntity entity, Boolean isNeedToFetchSubmodules) {
        return ModuleDto.builder()
                        .id(entity.getId())
                        .moduleTitle(entity.getModuleTitle())
                        .moduleOrderNumber(entity.getModuleOrderNumber())
                        .moduleDescription(entity.getDescription())
                        .isActive(entity.getIsActive())
                        .createdAt(entity.getCreatedAt())
                        .submodules(isNeedToFetchSubmodules ? mapSubmodules(entity.getSubmodules())
                                            : null)
                        .build();
    }

    public List<SubmoduleDto> mapSubmodules(List<SubmoduleEntity> entities) {
        return entities.stream()
                       .map(this::mapSubmoduleDto)
                       .sorted(Comparator.comparingInt(SubmoduleDto::getSubmoduleOrderNumber))
                       .toList();
    }

    public SubmoduleDto mapSubmoduleDto(SubmoduleEntity entity) {
        return SubmoduleDto.builder()
                           .id(entity.getId())
                           .submoduleTitle(entity.getSubmoduleTitle())
                           .submoduleContent(entity.getSubmoduleContent())
                           .submoduleOrderNumber(entity.getSubmoduleOrderNumber())
                           .createdAt(entity.getCreatedAt())
                           .build();
    }

}
