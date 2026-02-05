package ru.mentor.services;

import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.dto.avatar.UserAvatarContentDto;

/**
 * Сервис предоставляет функционал для загрузки аватара и генерации ссылки на файл
 */
public interface UserAvatarService {

    /**
     * Загружает файл в хранилище
     *
     * @param avatar
     *         файл аватара
     *
     * @return ключ сохранённого файла
     */
    String uploadUserAvatar(MultipartFile avatar);

    /**
     * Возвращает ресурс аватара по его ключу в хранилище.
     *
     * @param avatarKey
     *         ключ объекта в хранилище (MinIO)
     *
     * @return Resource c содержимым файла
     */
    Optional<UserAvatarContentDto> getUserAvatarFromStorage(String avatarKey);

    void deleteUserAvatarFromStorage(String userAvatarKey);

}
