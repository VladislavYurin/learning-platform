package ru.mentor.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.mentor.constant.Role;
import ru.mentor.entity.UserEntity;
import ru.mentor.repository.UserRepository;

/**
 * Класс для иициализации административного пользователя, выполняется при старте приложения.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * Создаёт учётную запись администратора при старте приложения, если она ещё не существует.
     * <p>
     *     Определяет логин администратора. Проверяет наличие пользователя по логину.
     *     Если логин не найден, то создаёт запись с ролью {@code ADMIN},
     *     кодируя пароль через PasswordEncoder и сохраняет её.
     * </p>
     * Пароль не хранится в открытом виде — используется {@link PasswordEncoder}
     * @param args аргументы запуска приложения
     */
    @Override
    public void run(ApplicationArguments args) {
        String adminUsername = "admin@example.com";
        if (!userRepository.existsByUsername(adminUsername)) {
            UserEntity request = UserEntity.builder()
                                           .username(adminUsername)
                                           .password(passwordEncoder.encode("testtesttest"))
                                           .lastName("Админ")
                                           .firstName("Админов")
                                           .tgNickname("@tg")
                                           .role(Role.ADMIN)
                                           .build();

            userRepository.save(request);
        }

        String defaultMentor = "vlad.yurin98@gmail.com";
        if (!userRepository.existsByUsername(defaultMentor)) {
            UserEntity request = UserEntity.builder()
                                           .username(defaultMentor)
                                           .password(passwordEncoder.encode("140698"))
                                           .lastName("Юрин")
                                           .firstName("Владислав")
                                           .tgNickname("@vladislavyurin")
                                           .role(Role.MENTOR)
                                           .build();

            userRepository.save(request);
        }

        String defaultUser = "pipa@popa.ru";
        if (!userRepository.existsByUsername(defaultUser)) {
            UserEntity request = UserEntity.builder()
                                           .username(defaultUser)
                                           .password(passwordEncoder.encode("140698"))
                                           .lastName("Пипа")
                                           .firstName("Попов")
                                           .tgNickname("@vladislavyurin")
                                           .role(Role.USER)
                                           .build();

            userRepository.save(request);
        }
    }

}
