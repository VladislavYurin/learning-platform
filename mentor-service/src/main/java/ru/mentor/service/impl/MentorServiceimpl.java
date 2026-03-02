package ru.mentor.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.mapper.UserMapper;
import ru.mentor.repository.UserRepository;
import ru.mentor.service.MentorService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с менторами
 * Инкапсулирует логику получения менторов из репозитория.
 */
@Service
@RequiredArgsConstructor
public class MentorServiceimpl implements MentorService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Получает из репозитория всех пользователей с ролью {@link Role#MENTOR}
     * и преобразует их в DTO.
     *
     * @return список DTO с информацией о менторах
     */
    @Override
    public List<UserInfoDto> getAllMentors() {
        return userRepository.findAllByRole(Role.MENTOR).stream()
                .map(userMapper ::mapUserEntityToUserInfoDto)
                .collect(Collectors.toList());
    }
}
