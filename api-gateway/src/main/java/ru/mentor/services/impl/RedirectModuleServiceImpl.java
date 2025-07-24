package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.constant.Role;
import ru.mentor.dto.InnerCreateModuleRequest;
import ru.mentor.dto.ModuleDto;
import ru.mentor.dto.front.CreateModuleRequest;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.CourseClient;
import ru.mentor.mapper.CourseMapper;
import ru.mentor.services.RedirectModuleService;
import ru.mentor.services.UserService;

@Service
@RequiredArgsConstructor
public class RedirectModuleServiceImpl implements RedirectModuleService {

    private final UserService userService;

    private final CourseClient courseClient;

    private final CourseMapper courseMapper;

    @Override
    public ModuleDto createModule(CreateModuleRequest request) {
        UserEntity user = userService.getCurrentUser();
        Role.checkUserIsAdminOrMentor(user);
        InnerCreateModuleRequest innerCreateModuleRequest = courseMapper.mapToInnerCreateModuleRequest(
                user.getId(),
                request
        );
        return courseClient.createModule(innerCreateModuleRequest);
    }

    @Override
    public ModuleDto getModuleById(Long courseId, Long moduleId) {
        UserEntity user = userService.getCurrentUser();
        return courseClient.getModuleById(user.getId(), courseId, moduleId);
    }

    @Override
    public ModuleDto importModuleFromFile(CreateModuleRequest request, MultipartFile file) {
        UserEntity user = userService.getCurrentUser();
        Role.checkUserIsAdminOrMentor(user);
        InnerCreateModuleRequest innerCreateModuleRequest = courseMapper.mapToInnerCreateModuleRequest(
                user.getId(),
                request
        );
        return courseClient.importModuleFromMarkdown(innerCreateModuleRequest, file);
    }

}
