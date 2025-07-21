package ru.mentor;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.mentor.constant.Role;
import ru.mentor.entity.UserEntity;
import ru.mentor.services.UserService;

@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationRunner {

    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) {
        String adminUsername = "admin@example.com";
        if (userService.existsByUserName(adminUsername)) {
            UserEntity request = UserEntity.builder()
                                           .username(adminUsername)
                                           .password("test")
                                           .email("@tg")
                                           .role(Role.ADMIN)
                                           .build();

            userService.create(request);
        }
    }

}
