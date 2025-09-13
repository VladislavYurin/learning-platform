package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.UserException;
import ru.mentor.repository.UserRepository;
import ru.mentor.services.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity create(UserEntity userEntity) {
        if (!userRepository.existsByUsername(userEntity.getUsername())) {
            return userRepository.save(userEntity);
        }
        throw new UserException(String.format(
                "Юзер с username = %s уже существует",
                userEntity.getUsername()
        ));
    }

    @Override
    public boolean existsByUserName(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserEntity getByUsername(String username) {
        return userRepository.findByUsernameOrThrow(username);
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    @Override
    public UserEntity getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    @Override
    public Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserEntity userEntity) {
            return userEntity.getId();
        }
        throw new IllegalStateException("Пользователь не найден в контексте безопасности");
    }

}
