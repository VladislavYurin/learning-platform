package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.mentor.entity.UserEntity;
import ru.mentor.services.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public UserEntity create(UserEntity userEntity) {
        return null;
    }

    @Override
    public UserEntity getByUsername(String username) {
        return null;
    }

    @Override
    public UserDetailsService userDetailsService() {
        return null;
    }

    @Override
    public UserEntity getCurrentUser() {
        return null;
    }

    @Override
    public Long getCurrentUserId() {
        return null;
    }

}
