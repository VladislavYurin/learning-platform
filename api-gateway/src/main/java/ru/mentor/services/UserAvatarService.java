package ru.mentor.services;

import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.dto.avatar.UserAvatarContentDto;

/**
 * Сервис для работы с аватарами пользователей в MinIO.
 */
public interface UserAvatarService {

    /**
     * Загружает аватар в хранилище.
     */
    String uploadUserAvatar(MultipartFile avatar);

    /**
     * Получает аватар по ключу из хранилища.
     */
    Optional<UserAvatarContentDto> getUserAvatarFromStorage(String userAvatarKey);

    /**
     * Удаляет аватар по ключу из хранилища.
     */
    void deleteUserAvatarFromStorage(String userAvatarKey);

}
