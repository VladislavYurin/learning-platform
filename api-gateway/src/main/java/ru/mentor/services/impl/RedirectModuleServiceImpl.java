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

@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectModuleServiceImpl implements RedirectModuleService {

    private final UserService userService;

    private final CourseClient courseClient;

    private final CourseMapper courseMapper;

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
