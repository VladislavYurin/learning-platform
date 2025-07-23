package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.entity.UserEntity;
import ru.mentor.mapper.BaseMapper;
import ru.mentor.repository.UserRepository;
import ru.mentor.services.UserInfoService;
import ru.mentor.services.UserService;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final UserService userService;

    private final UserRepository userRepository;

    private final BaseMapper baseMapper;

    @Override
    public UserInfoDto getMyUserInfo() {
        UserEntity user = userService.getCurrentUser();
        return baseMapper.mapUserDto(user);
    }

    @Override
    public UserInfoDto getOtherUserInfo(Long userId) {
        UserEntity user = userRepository.findByIdOrThrow(userId);
        return baseMapper.mapUserDto(user);
    }

}
