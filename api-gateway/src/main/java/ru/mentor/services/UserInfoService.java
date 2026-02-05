package ru.mentor.services;

import org.springframework.web.multipart.MultipartFile;
import ru.mentor.dto.UserInfoDto;

/**
 * Сервисный интерфейс для работы с пользовательской информацией и аватаром.
 * Предоставляет операции получения данных текущего пользователя,
 * просмотра профиля другого пользователя, назначения роли наставника,
 * обновления аватара.
 */
public interface UserInfoService {

    /**
     * Возвращает профиль текущего (аутентифицированного) пользователя.
     * Идентификатор пользователя берётся из контекста безопасности.
     *
     * @return DTO с данными текущего пользователя
     */
    UserInfoDto getMyUserInfo();

    /**
     * Возвращает профиль другого пользователя по его идентификатору.
     *
     * @param userId
     *         идентификатор пользователя
     *
     * @return DTO с данными о другом пользователе
     */
    UserInfoDto getOtherUserInfo(Long userId);

    /**
     * Назначает текущему пользователю роль наставника (MENTOR)
     * и возвращает обновлённые данные профиля.
     *
     * @return DTO с актуальными данными пользователя после назначения роли
     */
    UserInfoDto assignMentorRole();

    UserInfoDto updateMyUserInfo(UserInfoDto updateDto);

    /**
     * Обновляет аватар текущего пользователя.
     *
     * @param avatar
     *         файл нового аватара
     */
    void updateMyUserAvatar(MultipartFile avatar);

}
