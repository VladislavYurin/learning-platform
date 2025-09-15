package ru.mentor.mapper;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.dto.CourseDto;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.dto.kafka.KafkaNotificationDto;
import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.entity.UserEntity;

/**
 * Базовый маппер для преобразования сущностей в DTO объекты.
 * Предоставляет методы для маппинга курсов, модулей и пользователей.
 */
@Component
@RequiredArgsConstructor
public class BaseMapper {

    /**
     * Преобразует список сущностей курсов в список DTO курсов.
     *
     * @param entities список сущностей курсов для преобразования
     * @param isNeedToFetchModules флаг, указывающий нужно ли загружать модули курсов
     * @param isNeedToFetchSubmodules флаг, указывающий нужно ли загружать содержимое модулей
     * @return список DTO курсов
     */
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

    /**
     * Преобразует сущность курса в DTO курса.
     *
     * @param entity сущность курса для преобразования
     * @param user сущность автора курса
     * @param isNeedToFetchModules флаг, указывающий нужно ли загружать модули курса
     * @param isNeedToFetchModuleContent флаг, указывающий нужно ли загружать содержимое модулей
     * @return DTO курса
     */
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

    /**
     * Преобразует список сущностей модулей в список DTO модулей.
     * Модули сортируются по порядковому номеру.
     *
     * @param entities список сущностей модулей для преобразования
     * @param isNeedToFetchModuleContent флаг, указывающий нужно ли загружать содержимое модулей
     * @return список DTO модулей, отсортированный по порядковому номеру
     */
    public List<ModuleDto> mapModules(
            List<ModuleEntity> entities,
            Boolean isNeedToFetchModuleContent) {
        return entities.stream()
                       .map(module -> mapModule(module, isNeedToFetchModuleContent))
                       .sorted(Comparator.comparingInt(ModuleDto::getModuleOrderNumber))
                       .toList();
    }

    /**
     * Преобразует сущность модуля в DTO модуля.
     *
     * @param entity сущность модуля для преобразования
     * @param isNeedToFetchModuleContent флаг, указывающий нужно ли загружать содержимое модуля
     * @return DTO модуля
     */
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

    /**
     * Преобразует сущность пользователя в DTO информации о пользователе.
     *
     * @param entity сущность пользователя для преобразования
     * @return DTO информации о пользователе
     */
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

    /**
     * Преобразует DTO информации о пользователе в сущность пользователя.
     *
     * @param userInfoDto DTO информации о пользователе для преобразования
     * @return сущность пользователя
     */
    public UserEntity mapUserEntity(UserInfoDto userInfoDto) {
        return UserEntity.builder()
                .id(userInfoDto.getId())
                .username(userInfoDto.getUsername())
                .role(userInfoDto.getRole())
                .firstName(userInfoDto.getFirstName())
                .lastName(userInfoDto.getLastName())
                .tgNickname(userInfoDto.getTgNickname())
                .tgChatId(userInfoDto.getTgChatId())
                .build();
    }

}
