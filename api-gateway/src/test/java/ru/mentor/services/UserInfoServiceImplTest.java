package ru.mentor.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.UtilMapper;
import ru.mentor.repository.UserRepository;
import ru.mentor.services.impl.UserInfoServiceImpl;
import ru.mentor.services.impl.UserServiceImpl;
import ru.mentor.testUtil.TestDataGenerator;
import ru.mentor.testUtil.TestEntityStubGenerator;

@ExtendWith(MockitoExtension.class)
public class UserInfoServiceImplTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UtilMapper utilMapper;

    @InjectMocks
    private UserInfoServiceImpl userInfoService;

    private final String UPDATED_FIRST_NAME = "updatedFirstName";

    @Test
    public void updateMyUserInfo() {
        UserInfoDto updateDto = TestEntityStubGenerator.getUserInfoDto();
        updateDto.setFirstName(UPDATED_FIRST_NAME);

        UserEntity currentUser = TestDataGenerator.getUserEntity();
        UserEntity updatedEntity = TestDataGenerator.getUserEntity();
        updatedEntity.setFirstName(UPDATED_FIRST_NAME);

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        Mockito.doAnswer(invocation -> {
            currentUser.setFirstName(UPDATED_FIRST_NAME);
            return null;
        }).when(utilMapper).updateUserFromDto(updateDto, currentUser);

        Mockito.when(userRepository.save(Mockito.any(UserEntity.class)))
                .thenReturn(updatedEntity);
        Mockito.when(utilMapper.userEntityToUserInfoDto(updatedEntity))
                .thenReturn(updateDto);

        UserInfoDto result = userInfoService.updateMyUserInfo(updateDto);

        Assertions.assertEquals(updateDto, result);

        Mockito.verify(userService).getCurrentUser();
        Mockito.verify(utilMapper).updateUserFromDto(updateDto, currentUser);
        Mockito.verify(userRepository).save(Mockito.any(UserEntity.class));
        Mockito.verify(utilMapper).userEntityToUserInfoDto(updatedEntity);
    }
}