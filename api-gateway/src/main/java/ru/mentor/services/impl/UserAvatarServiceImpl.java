package ru.mentor.services.impl;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.config.MinioProperties;
import ru.mentor.config.UserAvatarProperties;
import ru.mentor.dto.avatar.UserAvatarContentDto;
import ru.mentor.exception.useravatar.UserAvatarServiceException;
import ru.mentor.exception.useravatar.UserAvatarValidationException;
import ru.mentor.services.UserAvatarService;
import ru.mentor.util.RqGenerator;

/**
 * Сервис предоставляет функционал для работы с аватаром пользователя в MinIO:
 * загрузка, получение и удаление файла по ключу.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarServiceImpl implements UserAvatarService {

    private static final String USER_AVATAR_KEY_PREFIX = "userAvatar_";
    private static final char EXTENSION_SEPARATOR = '.';
    private static final String META_ORIGINAL_FILENAME = "original-filename";
    private static final String DEFAULT_USER_AVATAR_FILENAME = "userAvatar";
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final UserAvatarProperties userAvatarProperties;

    /**
     * Загружает файл аватара в MinIO и возвращает ключ сохранённого объекта.
     *
     * @param avatar
     *         файл аватара
     *
     * @return ключ сохранённого аватара
     */
    @Override
    public String uploadUserAvatar(MultipartFile avatar) {
        String requestId = RqGenerator.generateRqId();
        log.info(
                "[ requestId = {} ] Поступил запрос на загрузку файла в MinIO",
                requestId
        );

        String bucket = minioProperties.getBucket();
        ensureBucketExists(bucket);
        validate(avatar);

        String key = USER_AVATAR_KEY_PREFIX + UUID.randomUUID();

        Map<String, String> meta = buildUserAvatarMetadata(avatar);

        try (InputStream is = avatar.getInputStream()) {
            PutObjectArgs args = PutObjectArgs.builder()
                                              .bucket(bucket)
                                              .object(key)
                                              .stream(is, avatar.getSize(), -1)
                                              .contentType(avatar.getContentType())
                                              .userMetadata(meta) // <-- добавили
                                              .build();

            minioClient.putObject(args);
            log.info(
                    "[ requestId = {} ] Файл загружен в MinIO: bucket={}, key={}, size={}",
                    requestId,
                    bucket,
                    key,
                    avatar.getSize()
            );
            return key;
        } catch (Exception e) {
            throw new UserAvatarServiceException("Ошибка загрузки файла", e);
        }
    }

    /**
     * Возвращает DTO с содержимым аватара по ключу из MinIO.
     *
     * @param userAvatarKey
     *         ключ аватара в MinIO
     *
     * @return Optional с {@link UserAvatarContentDto}; empty, если у пользователя не задан ключ аватара
     *
     * @throws UserAvatarServiceException
     *         если ключ задан, но объект в MinIO не найден или произошла ошибка хранилища
     */
    @Override
    public Optional<UserAvatarContentDto> getUserAvatarFromStorage(String userAvatarKey) {
        String requestId = RqGenerator.generateRqId();
        log.info("[ requestId = {} ] Поступил запрос на получение аватара из MinIO.", requestId);

        if (userAvatarKey == null || userAvatarKey.isBlank()) {
            return Optional.empty();
        }

        String bucket = minioProperties.getBucket();

        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                                  .bucket(bucket)
                                  .object(userAvatarKey)
                                  .build()
            );

            String contentType =
                    (stat != null && stat.contentType() != null && !stat.contentType().isBlank())
                            ? stat.contentType()
                            : "application/octet-stream";

            String metaName = null;
            if (stat != null && stat.userMetadata() != null) {
                metaName = stat.userMetadata().get(META_ORIGINAL_FILENAME);
            }
            String filename = (metaName != null && !metaName.isBlank())
                    ? metaName
                    : DEFAULT_USER_AVATAR_FILENAME;

            InputStream is = minioClient.getObject(
                    GetObjectArgs.builder()
                                 .bucket(bucket)
                                 .object(userAvatarKey)
                                 .build()
            );

            UserAvatarContentDto dto = UserAvatarContentDto.builder()
                                                           .inputStream(is)
                                                           .contentType(contentType)
                                                           .filename(filename)
                                                           .size(stat != null ? stat.size() : null)
                                                           .build();

            return Optional.of(dto);

        } catch (io.minio.errors.ErrorResponseException e) {
            String code = e.errorResponse() != null ? e.errorResponse().code() : null;
            throw new UserAvatarServiceException(
                    "Ошибка хранилища при получении файла. key=" + userAvatarKey
                            + (code != null ? ", code=" + code : ""),
                    e
            );
        } catch (Exception e) {
            throw new UserAvatarServiceException(
                    "Внутренняя ошибка при получении файла. key=" + userAvatarKey,
                    e
            );
        }
    }

    /**
     * Удаляет аватар из MinIO по ключу.
     *
     * @param userAvatarKey
     *         ключ аватара в MinIO
     */
    @Override
    public void deleteUserAvatarFromStorage(String userAvatarKey) {
        String requestId = RqGenerator.generateRqId();
        log.info(
                "[ requestId = {} ] Поступил запрос на удаление аватара из MinIO.",
                requestId
        );

        if (userAvatarKey == null || userAvatarKey.isBlank()) {
            return;
        }

        String bucket = minioProperties.getBucket();

        try {
            minioClient.removeObject(
                    io.minio.RemoveObjectArgs.builder()
                                             .bucket(bucket)
                                             .object(userAvatarKey)
                                             .build()
            );

            log.info(
                    "[ requestId = {} ] Аватар удалён из MinIO: bucket={}, key={}",
                    requestId,
                    bucket,
                    userAvatarKey
            );

        } catch (io.minio.errors.ErrorResponseException e) {
            String code = e.errorResponse() != null ? e.errorResponse().code() : null;
            throw new UserAvatarServiceException(
                    "Ошибка хранилища при удалении файла. key=" + userAvatarKey
                            + (code != null ? ", code=" + code : ""),
                    e
            );
        } catch (Exception e) {
            throw new UserAvatarServiceException(
                    "Внутренняя ошибка при удалении файла. key=" + userAvatarKey,
                    e
            );
        }
    }

    private void validate(MultipartFile avatar) {
        if (avatar == null) {
            throw new UserAvatarValidationException("Файл не передан");
        }

        if (avatar.getSize() > userAvatarProperties.getMaxSizeBytes()) {
            throw new UserAvatarValidationException("Размер файла превышает допустимый лимит");
        }

        String contentType = avatar.getContentType();
        if (contentType == null || !userAvatarProperties.getAllowedContentTypes()
                                                        .contains(contentType)) {
            throw new UserAvatarValidationException(
                    "Недопустимый content-type файла: " + contentType);
        }

        String ext = extractExtension(avatar.getOriginalFilename());
        if (ext == null || !userAvatarProperties.getAllowedExtensions().contains(ext)) {
            throw new UserAvatarValidationException("Недопустимое расширение файла: " + ext);
        }

        if (avatar.isEmpty()) {
            throw new UserAvatarValidationException("Файл пустой");
        }
    }

    private void ensureBucketExists(String bucket) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucket).build()
                );
                log.info("Создан bucket в MinIO: {}", bucket);
            }
        } catch (Exception e) {
            throw new UserAvatarServiceException(
                    "Ошибка проверки/создания bucket в MinIO: " + bucket, e);
        }
    }

    private String extractExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int dotIndex = filename.lastIndexOf(EXTENSION_SEPARATOR);
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return null;
        }
        return filename.substring(dotIndex + 1).toLowerCase(Locale.ROOT).trim();
    }

    private java.util.Map<String, String> buildUserAvatarMetadata(MultipartFile avatar) {
        String name = avatar.getOriginalFilename();

        if (name == null || name.isBlank()) {
            name = DEFAULT_USER_AVATAR_FILENAME;
        } else {
            name = name.replace('\\', '/');
            int idx = name.lastIndexOf('/');
            if (idx >= 0) {
                name = name.substring(idx + 1);
            }

            name = name.replace("\"", "")
                       .replace("\r", "")
                       .replace("\n", "")
                       .trim();

            if (name.isBlank()) {
                name = DEFAULT_USER_AVATAR_FILENAME;
            }
        }

        java.util.Map<String, String> meta = new java.util.HashMap<>();
        meta.put(META_ORIGINAL_FILENAME, name);
        return meta;
    }

}
