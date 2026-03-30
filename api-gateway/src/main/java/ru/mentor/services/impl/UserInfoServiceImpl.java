package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.constant.Role;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.UserRepository;
import ru.mentor.services.UserAvatarService;
import ru.mentor.services.UserInfoService;
import ru.mentor.services.UserService;

/**
 * Реализация сервиса для работы с пользовательской информацией и аватаром.
 * <p>
 * Предоставляет операции получения данных текущего пользователя,
 * просмотра профиля другого пользователя, назначения роли наставника,
 * обновления аватара.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BaseMapper baseMapper;
    private final UserAvatarService userAvatarService;

    @Override
    public UserInfoDto getMyUserInfo() {
        UserEntity user = userService.getCurrentUser();
        Long userId = user.getId();

        log.debug(
                "[userId={}] Получен запрос на получение своей информации.",
                userId
        );

        try {
            UserInfoDto response = baseMapper.mapUserDto(user);

            log.debug(
                    "[userId={}] Успешно получена информация о текущем пользователе.",
                    userId
            );

            return response;
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при получении информации о текущем пользователе.",
                    userId,
                    e
            );
            throw e;
        }
    }

    @Override
    public UserInfoDto getOtherUserInfo(Long userId) {
        Long currentUserId = userService.getCurrentUser().getId();

        log.debug(
                "[userId={}] [targetUserId={}] Получен запрос на получение информации о пользователе.",
                currentUserId,
                userId
        );

        try {
            UserEntity user = userRepository.findByIdOrThrow(userId);
            UserInfoDto response = baseMapper.mapUserDto(user);

            log.debug(
                    "[userId={}] [targetUserId={}] Успешно получена информация о пользователе.",
                    currentUserId,
                    userId
            );

            return response;
        } catch (Exception e) {
            log.error(
                    "[userId={}] [targetUserId={}] Ошибка при получении информации о пользователе.",
                    currentUserId,
                    userId,
                    e
            );
            throw e;
        }
    }

    @Override
    public UserInfoDto assignMentorRole() {
        UserEntity user = userService.getCurrentUser();
        Long userId = user.getId();

        log.debug(
                "[userId={}] Получен запрос на выдачу роли ментора.",
                userId
        );

        try {
            user.setRole(Role.MENTOR);
            UserEntity savedUser = userRepository.save(user);
            UserInfoDto response = baseMapper.mapUserDto(savedUser);

            log.debug(
                    "[userId={}] Успешно выдана роль ментора.",
                    userId
            );

            return response;
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при выдаче роли ментора.",
                    userId,
                    e
            );
            throw e;
        }
    }

    @Override
    public UserInfoDto updateMyUserInfo(UserInfoDto updateDto) {
        UserEntity currentUser = userService.getCurrentUser();
        Long userId = currentUser.getId();

        log.debug(
                "[userId={}] Получен запрос на изменение своей информации.",
                userId
        );

        try {
            UserEntity updatedUser = baseMapper.mapUserEntity(updateDto);
            updatedUser.setPassword(currentUser.getPassword());
            updatedUser.setUserAvatarKey(currentUser.getUserAvatarKey());

            UserInfoDto response = baseMapper.mapUserDto(userRepository.save(updatedUser));

            log.debug(
                    "[userId={}] Успешно обновлена информация текущего пользователя.",
                    userId
            );

            return response;
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при обновлении информации текущего пользователя.",
                    userId,
                    e
            );
            throw e;
        }
    }

    @Override
    public void updateMyUserAvatar(MultipartFile avatar) {
        UserEntity user = userService.getCurrentUser();
        Long userId = user.getId();

        log.debug(
                "[userId={}] Получен запрос на обновление аватара.",
                userId
        );

        try {
            String oldKey = user.getUserAvatarKey();
            String newKey = userAvatarService.uploadUserAvatar(avatar);
            user.setUserAvatarKey(newKey);
            userRepository.save(user);

            if (oldKey != null && !oldKey.isBlank()) {
                userAvatarService.deleteUserAvatarFromStorage(oldKey);
            }

            log.debug(
                    "[userId={}] Успешно обновлен аватар пользователя.",
                    userId
            );
        } catch (Exception e) {
            log.error(
                    "[userId={}] Ошибка при обновлении аватара пользователя.",
                    userId,
                    e
            );
            throw e;
        }
    }
}