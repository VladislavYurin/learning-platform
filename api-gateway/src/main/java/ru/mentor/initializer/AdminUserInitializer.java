package ru.mentor.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.mentor.constant.Role;
import ru.mentor.entity.UserEntity;
import ru.mentor.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        String adminUsername = "admin@example.com";
        if (!userRepository.existsByUsername(adminUsername)) {
            UserEntity request = UserEntity.builder()
                                           .username(adminUsername)
                                           .password("test")
                                           .email("@mail")
                                           .tgNickname("@tg")
                                           .role(Role.ADMIN)
                                           .build();

            userRepository.save(request);
        }
    }

}
