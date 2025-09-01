package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.dto.InnerCreateModuleRequest;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.CourseClient;
import ru.mentor.mapper.CourseMapper;
import ru.mentor.services.RedirectModuleService;
import ru.mentor.services.UserService;
import ru.mentor.util.RqGenerator;

/**
 * Реализация сервиса редиректов/интеграции для операций с модулями курса.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectModuleServiceImpl implements RedirectModuleService {

    private final UserService userService;

    private final CourseClient courseClient;

    private final CourseMapper courseMapper;

    /**
     * Создаёт новый модуль в составе курса.
     * @param request данные для создания модуля
     * @return созданный модуль
     */
    @Override
    public ModuleDto createModule(CreateModuleRequest request) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на создание модуля в курсе [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getCourseId(),
                user.getId()
        ));
        InnerCreateModuleRequest innerCreateModuleRequest = courseMapper.mapToInnerCreateModuleRequest(
                user.getId(),
                request
        );
        return courseClient.createModule(rqUId, innerCreateModuleRequest);
    }

    /**
     * Возвращает модуль по идентификатору курса и модуля.
     * @param courseId идентификатор курса
     * @param moduleId идентификатор модуля внутри курса
     * @return найденный модуль
     */
    @Override
    public ModuleDto getModuleById(Long courseId, Long moduleId) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на получение модуля [ ID = %d ] из курса [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                moduleId,
                courseId,
                user.getId()
        ));
        return courseClient.getModuleById(rqUId, user.getId(), courseId, moduleId);
    }

    /**
     * Импортирует модуль из загруженного файла (например, Markdown).
     * @param request параметры создаваемого модуля
     * @param file файл с содержимым модуля
     * @return созданный модуль
     */
    @Override
    public ModuleDto importModuleFromFile(CreateModuleRequest request, MultipartFile file) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на импорт файлом модуля в курсе [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getCourseId(),
                user.getId()
        ));
        InnerCreateModuleRequest innerCreateModuleRequest = courseMapper.mapToInnerCreateModuleRequest(
                user.getId(),
                request
        );
        return courseClient.importModuleFromMarkdown(rqUId, innerCreateModuleRequest, file);
    }

    /**
     * Удаляет модуль из курса.
     * @param courseId идентификатор курса
     * @param moduleId идентификатор модуля внутри курса
     */
    @Override
    public void deleteModule(Long courseId, Long moduleId) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на удаление модуля [ ID = %d ] в курсе [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                moduleId,
                courseId,
                user.getId()
        ));
        courseClient.deleteModule(rqUId, user.getId(), courseId, moduleId);
    }

}
