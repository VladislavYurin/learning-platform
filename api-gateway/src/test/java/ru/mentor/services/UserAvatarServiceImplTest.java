package ru.mentor.services;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.ErrorResponse;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import ru.mentor.config.MinioProperties;
import ru.mentor.config.UserAvatarProperties;
import ru.mentor.dto.avatar.UserAvatarContentDto;
import ru.mentor.exception.useravatar.UserAvatarException;
import ru.mentor.exception.useravatar.UserAvatarServiceException;
import ru.mentor.services.impl.UserAvatarServiceImpl;
import ru.mentor.testUtil.TestConstantHolder;

@ExtendWith(MockitoExtension.class)
class UserAvatarServiceImplTest {
    private static final String AVATAR_PART_NAME = "avatar";
    private static final String META_ORIGINAL_FILENAME = "original-filename";
    private final UserAvatarProperties avatarProperties = new UserAvatarProperties();
    private final MinioProperties minioProperties = new MinioProperties();
    private UserAvatarServiceImpl service;

    @Mock
    private MinioClient minioClient;

    @BeforeEach
    void init() {
        avatarProperties.setAllowedExtensions(TestConstantHolder.avatarAllowedExtensions);
        avatarProperties.setAllowedContentTypes(TestConstantHolder.avatarAllowedContentTypes);
        avatarProperties.setMaxSizeBytes(TestConstantHolder.avatarMaxSizeBytes);
        minioProperties.setBucket(TestConstantHolder.avatarBucket);
        service = new UserAvatarServiceImpl(minioClient, minioProperties, avatarProperties);
    }

    @Test
    void uploadUserAvatar_fileSizeExceedsLimit_throwsAvatarExceptionAndDoesNotCallPutObject()
            throws Exception {
        byte[] tooBig = new byte[(int) TestConstantHolder.avatarMaxSizeBytes + 1];
        MockMultipartFile avatar = file(
                TestConstantHolder.avatarFilenameJpg,
                TestConstantHolder.avatarContentTypeJpeg,
                tooBig
        );

        Assertions.assertThrows(
                UserAvatarException.class,
                () -> service.uploadUserAvatar(avatar)
        );

        Mockito.verify(minioClient, Mockito.never())
               .putObject(ArgumentMatchers.any(PutObjectArgs.class));
    }

    @Test
    void uploadUserAvatar_contentTypeIsNull_throwsAvatarExceptionAndDoesNotCallPutObject()
            throws Exception {
        MockMultipartFile avatar = file(
                TestConstantHolder.avatarFilenameJpg,
                null,
                TestConstantHolder.avatarMinimalContent
        );

        Assertions.assertThrows(
                UserAvatarException.class,
                () -> service.uploadUserAvatar(avatar)
        );

        Mockito.verify(minioClient, Mockito.never())
               .putObject(ArgumentMatchers.any(PutObjectArgs.class));
    }

    @Test
    void uploadUserAvatar_contentTypeNotAllowed_throwsAvatarExceptionAndDoesNotCallPutObject()
            throws Exception {
        MockMultipartFile avatar = file(
                TestConstantHolder.avatarFilenameJpg,
                TestConstantHolder.avatarContentTypeText,
                TestConstantHolder.avatarMinimalContent
        );

        Assertions.assertThrows(
                UserAvatarException.class,
                () -> service.uploadUserAvatar(avatar)
        );

        Mockito.verify(minioClient, Mockito.never())
               .putObject(ArgumentMatchers.any(PutObjectArgs.class));
    }

    @Test
    void uploadUserAvatar_originalFilenameHasNoDot_throwsAvatarExceptionAndDoesNotCallPutObject()
            throws Exception {
        MockMultipartFile avatar = file(
                TestConstantHolder.avatarFilenameNoDot,
                TestConstantHolder.avatarContentTypeJpeg,
                TestConstantHolder.avatarMinimalContent
        );

        Assertions.assertThrows(
                UserAvatarException.class,
                () -> service.uploadUserAvatar(avatar)
        );

        Mockito.verify(minioClient, Mockito.never())
               .putObject(ArgumentMatchers.any(PutObjectArgs.class));
    }

    @Test
    void uploadUserAvatar_extensionNotAllowed_throwsAvatarExceptionAndDoesNotCallPutObject()
            throws Exception {
        MockMultipartFile avatar = file(
                TestConstantHolder.avatarFilenameGif,
                TestConstantHolder.avatarContentTypeJpeg,
                TestConstantHolder.avatarMinimalContent
        );

        Assertions.assertThrows(
                UserAvatarException.class,
                () -> service.uploadUserAvatar(avatar)
        );

        Mockito.verify(minioClient, Mockito.never())
               .putObject(ArgumentMatchers.any(PutObjectArgs.class));
    }

    @Test
    void uploadUserAvatar_validFile_returnsAvatarKeyAndCallsMinioPutObject() throws Exception {
        Mockito.when(minioClient.bucketExists(ArgumentMatchers.any(BucketExistsArgs.class)))
               .thenReturn(true);
        Mockito.when(minioClient.putObject(ArgumentMatchers.any(PutObjectArgs.class)))
               .thenReturn(null);

        MockMultipartFile avatar = file(
                TestConstantHolder.avatarFilenameJpg,
                TestConstantHolder.avatarContentTypeJpeg,
                TestConstantHolder.avatarMinimalContent
        );

        String key = Assertions.assertDoesNotThrow(() -> service.uploadUserAvatar(avatar));
        Assertions.assertNotNull(key);
        Assertions.assertFalse(key.isBlank());

        Mockito.verify(minioClient).bucketExists(ArgumentMatchers.any(BucketExistsArgs.class));
        Mockito.verify(minioClient).putObject(ArgumentMatchers.any(PutObjectArgs.class));
    }

    @Test
    void uploadUserAvatar_bucketExistsThrowsException_throwsAvatarExceptionAndDoesNotCallPutObject()
            throws Exception {
        Mockito.when(minioClient.bucketExists(ArgumentMatchers.any(BucketExistsArgs.class)))
               .thenThrow(new RuntimeException("minio bucketExists failed"));

        MockMultipartFile avatar = file(
                TestConstantHolder.avatarFilenameJpg,
                TestConstantHolder.avatarContentTypeJpeg,
                TestConstantHolder.avatarMinimalContent
        );

        Assertions.assertThrows(
                UserAvatarException.class,
                () -> service.uploadUserAvatar(avatar)
        );

        Mockito.verify(minioClient, Mockito.never())
               .putObject(ArgumentMatchers.any(PutObjectArgs.class));
    }

    @Test
    void uploadUserAvatar_putObjectThrowsException_throwsAvatarException() throws Exception {
        Mockito.when(minioClient.bucketExists(ArgumentMatchers.any(BucketExistsArgs.class)))
               .thenReturn(true);
        Mockito.when(minioClient.putObject(ArgumentMatchers.any(PutObjectArgs.class)))
               .thenThrow(new RuntimeException("minio putObject failed"));

        MockMultipartFile avatar = file(
                TestConstantHolder.avatarFilenameJpg,
                TestConstantHolder.avatarContentTypeJpeg,
                TestConstantHolder.avatarMinimalContent
        );

        Assertions.assertThrows(
                UserAvatarException.class,
                () -> service.uploadUserAvatar(avatar)
        );

        Mockito.verify(minioClient).putObject(ArgumentMatchers.any(PutObjectArgs.class));
    }

    @Test
    void uploadUserAvatar_bucketDoesNotExist_callsMakeBucketAndUploads() throws Exception {
        Mockito.when(minioClient.bucketExists(ArgumentMatchers.any(BucketExistsArgs.class)))
               .thenReturn(false);
        Mockito.when(minioClient.putObject(ArgumentMatchers.any(PutObjectArgs.class)))
               .thenReturn(null);

        MockMultipartFile avatar = file(
                TestConstantHolder.avatarFilenameJpg,
                TestConstantHolder.avatarContentTypeJpeg,
                TestConstantHolder.avatarMinimalContent
        );

        Assertions.assertDoesNotThrow(() -> service.uploadUserAvatar(avatar));

        Mockito.verify(minioClient).bucketExists(ArgumentMatchers.any(BucketExistsArgs.class));
        Mockito.verify(minioClient).makeBucket(ArgumentMatchers.any(MakeBucketArgs.class));
        Mockito.verify(minioClient).putObject(ArgumentMatchers.any(PutObjectArgs.class));
    }

    @Test
    void uploadUserAvatar_makeBucketThrowsException_throwsAvatarExceptionAndDoesNotCallPutObject()
            throws Exception {
        Mockito.when(minioClient.bucketExists(ArgumentMatchers.any(BucketExistsArgs.class)))
               .thenReturn(false);

        Mockito.doThrow(new RuntimeException("minio makeBucket failed"))
               .when(minioClient)
               .makeBucket(ArgumentMatchers.any(MakeBucketArgs.class));

        MockMultipartFile avatar = file(
                TestConstantHolder.avatarFilenameJpg,
                TestConstantHolder.avatarContentTypeJpeg,
                TestConstantHolder.avatarMinimalContent
        );

        Assertions.assertThrows(
                UserAvatarException.class,
                () -> service.uploadUserAvatar(avatar)
        );

        Mockito.verify(minioClient, Mockito.never())
               .putObject(ArgumentMatchers.any(PutObjectArgs.class));
    }

    @Test
    void getUserAvatarFromStorage_keyIsNullOrBlank_returnsEmpty() {
        Assertions.assertTrue(service.getUserAvatarFromStorage(null).isEmpty());
        Assertions.assertTrue(service.getUserAvatarFromStorage("   ").isEmpty());

        Mockito.verifyNoInteractions(minioClient);
    }

    @Test
    void getUserAvatarFromStorage_fileExists_returnsDtoWithStreamAndMeta() throws Exception {
        StatObjectResponse stat = Mockito.mock(StatObjectResponse.class);
        Mockito.when(stat.contentType()).thenReturn("image/jpeg");
        Mockito.when(stat.size()).thenReturn(123L);
        Mockito.when(stat.userMetadata()).thenReturn(Map.of(META_ORIGINAL_FILENAME, "cat.jpg"));

        GetObjectResponse response = Mockito.mock(GetObjectResponse.class);

        Mockito.when(minioClient.statObject(ArgumentMatchers.any(StatObjectArgs.class)))
               .thenReturn(stat);
        Mockito.when(minioClient.getObject(ArgumentMatchers.any(GetObjectArgs.class)))
               .thenReturn(response);

        Optional<UserAvatarContentDto> opt = service.getUserAvatarFromStorage(TestConstantHolder.avatarFilenameJpg);

        Assertions.assertTrue(opt.isPresent());
        UserAvatarContentDto dto = opt.get();

        Assertions.assertEquals("image/jpeg", dto.getContentType());
        Assertions.assertEquals("cat.jpg", dto.getFilename());
        Assertions.assertEquals(123L, dto.getSize());
        Assertions.assertSame(response, dto.getInputStream());

        Mockito.verify(minioClient).statObject(ArgumentMatchers.any(StatObjectArgs.class));
        Mockito.verify(minioClient).getObject(ArgumentMatchers.any(GetObjectArgs.class));
    }

    @Test
    void getUserAvatarFromStorage_minioReturnsError_throwsUserAvatarServiceException()
            throws Exception {
        String key = "userAvatar_missing";

        ErrorResponseException ex = Mockito.mock(ErrorResponseException.class);
        ErrorResponse err = Mockito.mock(ErrorResponse.class);
        Mockito.when(ex.errorResponse()).thenReturn(err);
        Mockito.when(err.code()).thenReturn("NoSuchKey");

        Mockito.when(minioClient.statObject(ArgumentMatchers.any(StatObjectArgs.class)))
               .thenThrow(ex);

        Assertions.assertThrows(
                UserAvatarServiceException.class,
                () -> service.getUserAvatarFromStorage(key)
        );
    }

    @Test
    void deleteUserAvatarFromStorage_keyIsNullOrBlank_doesNothing() {
        service.deleteUserAvatarFromStorage(null);
        service.deleteUserAvatarFromStorage("  ");

        Mockito.verifyNoInteractions(minioClient);
    }

    @Test
    void deleteUserAvatarFromStorage_validKey_callsRemoveObject() throws Exception {
        String key = "userAvatar_toDelete";

        service.deleteUserAvatarFromStorage(key);

        Mockito.verify(minioClient).removeObject(ArgumentMatchers.any(RemoveObjectArgs.class));
    }

    @Test
    void deleteUserAvatarFromStorage_minioReturnsError_throwsUserAvatarServiceException()
            throws Exception {
        String key = "userAvatar_toDelete";

        ErrorResponseException ex = Mockito.mock(ErrorResponseException.class);
        ErrorResponse err = Mockito.mock(ErrorResponse.class);
        Mockito.when(ex.errorResponse()).thenReturn(err);
        Mockito.when(err.code()).thenReturn("AccessDenied");

        Mockito.doThrow(ex)
               .when(minioClient)
               .removeObject(ArgumentMatchers.any(RemoveObjectArgs.class));

        Assertions.assertThrows(
                UserAvatarServiceException.class,
                () -> service.deleteUserAvatarFromStorage(key)
        );
    }

    private MockMultipartFile file(String originalFilename, String contentType, byte[] content) {
        return new MockMultipartFile(AVATAR_PART_NAME, originalFilename, contentType, content);
    }

}
