package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.GetAccessRequest;
import ru.mentor.dto.front.AccessRequest;
import ru.mentor.entity.UserEntity;
import ru.mentor.feign.MentorClient;
import ru.mentor.mapper.AccessMapper;
import ru.mentor.services.RedirectAccessService;
import ru.mentor.services.UserService;
import ru.mentor.util.RqGenerator;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectAccessServiceImpl implements RedirectAccessService {

    private final UserService userService;

    private final AccessMapper accessMapper;

    private final MentorClient mentorClient;

    @Override
    public ResponseEntity<?> getCourseAccessToUser(AccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на добавление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getCourseId(),
                user.getId()
        ));
        Role.checkUserIsAdminOrMentor(user);
        GetAccessRequest innerRequest = accessMapper.mapToInnerRequest(user, request);
        return mentorClient.getCourseAccessToUser(rqUId, innerRequest);
    }

    @Override
    public ResponseEntity<?> deleteCourseAccessToUser(AccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на удаление доступа юзеру [ ID = %d ] к курсу [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getCourseId(),
                user.getId()
        ));
        Role.checkUserIsAdminOrMentor(user);
        GetAccessRequest innerRequest = accessMapper.mapToInnerRequest(user, request);
        return mentorClient.deleteCourseAccessToUser(rqUId, innerRequest);
    }

    @Override
    public ResponseEntity<?> getModuleAccessToUser(AccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на добавление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getModuleId(),
                user.getId()
        ));
        Role.checkUserIsAdminOrMentor(user);
        GetAccessRequest innerRequest = accessMapper.mapToInnerRequest(user, request);
        return mentorClient.getModuleAccessToUser(rqUId, innerRequest);
    }

    @Override
    public ResponseEntity<?> deleteModuleAccessToUser(AccessRequest request) {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на удаление доступа юзеру [ ID = %d ] к модулю [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                request.getUserId(),
                request.getModuleId(),
                user.getId()
        ));
        Role.checkUserIsAdminOrMentor(user);
        GetAccessRequest innerRequest = accessMapper.mapToInnerRequest(user, request);
        return mentorClient.deleteModuleAccessToUser(rqUId, innerRequest);
    }

}
