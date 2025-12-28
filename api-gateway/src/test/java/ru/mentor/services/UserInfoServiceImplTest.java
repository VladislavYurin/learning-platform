package ru.mentor.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.mentor.constant.Role;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.UserRepository;
import ru.mentor.services.impl.UserInfoServiceImpl;
import ru.mentor.services.impl.UserServiceImpl;
import ru.mentor.testUtil.TestEntityStubGenerator;

@ExtendWith(MockitoExtension.class)
public class UserInfoServiceImplTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BaseMapper baseMapper;

    @InjectMocks
    private UserInfoServiceImpl userInfoService;

    private final String UPDATED_FIRST_NAME = "updateFirstName";

    @Test
    public void updateMyUserInfo() {
        UserInfoDto updateDto = TestEntityStubGenerator.constructUserInfoDtoWithRole(Role.USER);
        updateDto.setFirstName(UPDATED_FIRST_NAME);

        UserEntity currentUser = TestEntityStubGenerator.constructUserEntityWithRole(Role.USER);

        UserEntity updateEntity = TestEntityStubGenerator.constructUserEntityWithRole(Role.USER);
        updateEntity.setFirstName(UPDATED_FIRST_NAME);

        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(baseMapper.mapUserEntity(updateDto)).thenReturn(updateEntity);
        Mockito.when(userRepository.save(updateEntity)).thenReturn(updateEntity);
        Mockito.when(baseMapper.mapUserDto(updateEntity)).thenReturn(updateDto);

        UserInfoDto result = userInfoService.updateMyUserInfo(updateDto);

        Assertions.assertEquals(updateDto, result);

        Mockito.verify(userService).getCurrentUser();
        Mockito.verify(baseMapper).mapUserEntity(updateDto);
        Mockito.verify(userRepository).save(updateEntity);
        Mockito.verify(baseMapper).mapUserDto(updateEntity);
    }
}