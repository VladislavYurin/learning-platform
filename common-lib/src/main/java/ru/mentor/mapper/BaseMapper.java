package ru.mentor.mapper;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;

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
                               entity.getAuthor(),
                               isNeedToFetchModules,
                               isNeedToFetchSubmodules
                       ))
                       .toList();
    }

    public CourseDto mapCourse(
            CourseEntity entity,
            UserEntity user,
            Boolean isNeedToFetchModules,
            Boolean isNeedToFetchModuleContent) {
        return CourseDto.builder()
                        .id(entity.getId())
                        .courseTitle(entity.getCourseTitle())
                        .courseDescription(entity.getDescription())
                        .isActive(entity.getIsActive())
                        .author(user != null ? mapUserDto(user) : null)
                        .modules(
                                isNeedToFetchModules ? mapModules(
                                        entity.getModules(),
                                        isNeedToFetchModuleContent
                                ) : null)
                        .createdAt(entity.getCreatedAt())
                        .build();
    }

    public List<ModuleDto> mapModules(
            List<ModuleEntity> entities,
            Boolean isNeedToFetchModuleContent) {
        return entities.stream()
                       .map(module -> mapModule(module, isNeedToFetchModuleContent))
                       .sorted(Comparator.comparingInt(ModuleDto::getModuleOrderNumber))
                       .toList();
    }

    public ModuleDto mapModule(ModuleEntity entity, Boolean isNeedToFetchModuleContent) {
        return ModuleDto.builder()
                        .id(entity.getId())
                        .moduleTitle(entity.getModuleTitle())
                        .moduleOrderNumber(entity.getModuleOrderNumber())
                        .moduleContent(
                                isNeedToFetchModuleContent ? entity.getModuleContent() : null)
                        .isActive(entity.getIsActive())
                        .createdAt(entity.getCreatedAt())
                        .createdAt(entity.getCreatedAt())
                        .build();
    }

    public UserInfoDto mapUserDto(UserEntity entity) {
        return UserInfoDto.builder()
                          .id(entity.getId())
                          .username(entity.getUsername())
                          .role(entity.getRole())
                          .firstName(entity.getFirstName())
                          .lastName(entity.getLastName())
                          .tgNickname(entity.getTgNickname())
                          .build();

    }

}
