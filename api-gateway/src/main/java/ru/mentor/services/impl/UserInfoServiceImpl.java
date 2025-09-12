package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.UserRepository;
import ru.mentor.services.UserInfoService;
import ru.mentor.services.UserService;
import ru.mentor.util.RqGenerator;

/**
 * Реализация сервиса для работы с пользовательской информацией.
 * <p>
 *     Предоставляет операции получения данных текущего пользователя,
 *     просмотра профиля другого пользователя и назначения роли наставника.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    private final UserService userService;

    private final UserRepository userRepository;

    private final BaseMapper baseMapper;

    /**
     * Возвращает данные текущего (аутентифицированного) пользователя.
     * @return DTO с информацией о текущем пользователе
     */
    @Override
    public UserInfoDto getMyUserInfo() {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на получение своей информации юзером [ ID = %d ].",
                rqUId,
                user.getId()
        ));
        return baseMapper.mapUserDto(user);
    }

    /**
     * Возвращает данные другого пользователя по его идентификатору.
     * @param userId идентификатор другого пользователя
     * @return DTO с информацией о запрашиваемом пользователе
     */
    @Override
    public UserInfoDto getOtherUserInfo(Long userId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на получении информации юзера [ ID = %d ] юзером [ ID = %d ].",
                rqUId,
                userId,
                user.getId()
        ));
        return baseMapper.mapUserDto(user);
    }

    /**
     * Назначает текущему пользователю роль наставника и возвращает обновлённые данные.
     * @return DTO с актуальными данными пользователя после назначения роли
     */
    @Override
    public UserInfoDto assignMentorRole() {
        UserEntity user = userService.getCurrentUser();
        String rqUId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ RqUId = %s ] Получен запрос на выдачу роли ментора юзером [ ID = %d ].",
                rqUId,
                user.getId()
        ));
        user.setRole(Role.MENTOR);
        UserEntity savedUser = userRepository.save(user);
        return baseMapper.mapUserDto(savedUser);
    }

}
