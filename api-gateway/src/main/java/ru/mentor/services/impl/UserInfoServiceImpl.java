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
import ru.mentor.util.RqGenerator;

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

    /**
     * Возвращает данные текущего (аутентифицированного) пользователя.
     *
     * @return DTO с информацией о текущем пользователе
     */
    @Override
    public UserInfoDto getMyUserInfo() {
        UserEntity user = userService.getCurrentUser();
        String requestId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на получение своей информации юзером [ ID = %d ].",
                requestId,
                user.getId()
        ));
        return baseMapper.mapUserDtoWithOptionalMentorTags(user);
    }

    /**
     * Возвращает данные другого пользователя по его идентификатору.
     *
     * @param userId
     *         идентификатор другого пользователя
     *
     * @return DTO с информацией о запрашиваемом пользователе
     */
    @Override
    public UserInfoDto getOtherUserInfo(Long userId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        String requestId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на получении информации юзера [ ID = %d ] юзером [ ID = %d ].",
                requestId,
                userId,
                user.getId()
        ));
        return baseMapper.mapUserDtoWithOptionalMentorTags(user);
    }

    /**
     * Назначает текущему пользователю роль наставника и возвращает обновлённые данные.
     *
     * @return DTO с актуальными данными пользователя после назначения роли
     */
    @Override
    public UserInfoDto assignMentorRole() {
        UserEntity user = userService.getCurrentUser();
        String requestId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на выдачу роли ментора юзером [ ID = %d ].",
                requestId,
                user.getId()
        ));
        user.setRole(Role.MENTOR);
        UserEntity savedUser = userRepository.save(user);
        return baseMapper.mapUserDtoWithOptionalMentorTags(savedUser);
    }

    /**
     * Изменяет данные текущего (аутентифицированного) пользователя.
     * Идентификатор пользователя берётся из контекста безопасности.
     *
     * @param updateDto
     *         обновляемые данные текущего пользователя
     *
     * @return DTO с актуальными данными текущего пользователя
     */
    @Override
    public UserInfoDto updateMyUserInfo(UserInfoDto updateDto) {
        UserEntity currentUser = userService.getCurrentUser();
        String requestId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на изменение своей информации юзером [ ID = %d ].",
                requestId,
                updateDto.getId()
        ));

        UserEntity updatedUser = baseMapper.mapUserEntity(updateDto);
        updatedUser.setPassword(currentUser.getPassword());
        updatedUser.setUserAvatarKey(currentUser.getUserAvatarKey());
        return baseMapper.mapUserDtoWithOptionalMentorTags(userRepository.save(updatedUser));

    }

    /**
     * Обновляет аватар пользователя:
     * загружает новый файл аватара в MinIO, сохраняет новый ключ в записи пользователя в БД,
     * затем удаляет предыдущий файл из MinIO (при наличии старого ключа).
     *
     * @param avatar
     *         файл нового аватара
     */
    @Override
    public void updateMyUserAvatar(MultipartFile avatar) {
        UserEntity user = userService.getCurrentUser();
        String requestId = RqGenerator.generateRqId();
        log.info(String.format(
                "[ requestId = %s ] Получен запрос на обновление аватара юзером [ ID = %d ].",
                requestId,
                user.getId()
        ));
        String oldKey = user.getUserAvatarKey();
        String newKey = userAvatarService.uploadUserAvatar(avatar);
        user.setUserAvatarKey(newKey);
        userRepository.save(user);
        if (oldKey != null && !oldKey.isBlank()) {
            userAvatarService.deleteUserAvatarFromStorage(oldKey);
        }
    }

}
