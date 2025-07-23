package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.dto.front.AccessRequest;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.MentorClient;
import ru.mentor.mapper.AccessMapper;
import ru.mentor.services.RedirectAccessService;
import ru.mentor.services.UserService;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectAccessServiceImpl implements RedirectAccessService {

    private final UserService userService;

    private final AccessMapper accessMapper;

    private final MentorClient mentorClient;

    @Override
    public void getCourseAccessToUser(AccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        Role.checkUserIsAdminOrMentor(user);
        GetAccessRequest innerRequest = accessMapper.mapToInnerRequest(user, request);
        mentorClient.getCourseAccessToUser(innerRequest);
    }

    @Override
    public void deleteCourseAccessToUser(AccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        Role.checkUserIsAdminOrMentor(user);
        GetAccessRequest innerRequest = accessMapper.mapToInnerRequest(user, request);
        mentorClient.deleteCourseAccessToUser(innerRequest);
    }

    @Override
    public void getModuleAccessToUser(AccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        Role.checkUserIsAdminOrMentor(user);
        GetAccessRequest innerRequest = accessMapper.mapToInnerRequest(user, request);
        mentorClient.getModuleAccessToUser(innerRequest);
    }

    @Override
    public void deleteModuleAccessToUser(AccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        Role.checkUserIsAdminOrMentor(user);
        GetAccessRequest innerRequest = accessMapper.mapToInnerRequest(user, request);
        mentorClient.deleteModuleAccessToUser(innerRequest);
    }

}
