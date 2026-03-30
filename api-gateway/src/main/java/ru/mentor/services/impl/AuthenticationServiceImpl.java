package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.constant.Role;
import ru.mentor.dto.auth.AuthRequest;
import ru.mentor.dto.auth.JwtAuthResponse;
import ru.mentor.dto.auth.RegRequest;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.InvalidRefreshTokenException;
import ru.mentor.kafka.KafkaFacade;
import ru.mentor.services.AuthenticationService;
import ru.mentor.services.JwtService;
import ru.mentor.services.UserAvatarService;
import ru.mentor.services.UserService;

/**
 * Реализация {@link AuthenticationService} для регистрации, входа и обновления токенов.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final KafkaFacade kafkaFacade;
    private final UserAvatarService userAvatarService;

    /**
     * Регистрация пользователя
     *
     * @param request
     *         данные пользователя
     * @param userAvatar
     *         файл аватара (опционально)
     *
     * @return токен
     */
    @Override
    public JwtAuthResponse registration(RegRequest request, MultipartFile userAvatar) {

        log.debug(
                "[username={}] Получен запрос на регистрацию пользователя.",
                request.getUsername()
        );

        try {
            String userAvatarKey = null;

            if (userAvatar != null) {
                userAvatarKey = userAvatarService.uploadUserAvatar(userAvatar);
            }

            UserEntity user = userService.create(UserEntity.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .tgNickname(request.getTgNickname())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .role(Role.USER)
                    .userAvatarKey(userAvatarKey)
                    .build());

            kafkaFacade.sendUserRegistrationMessage(user);

            log.debug(
                    "[userId={}] Успешно завершена регистрация пользователя.",
                    user.getId()
            );

            return generateTokens(user);
        } catch (Exception e) {
            log.error(
                    "[username={}] Ошибка во время регистрации пользователя.",
                    request.getUsername(),
                    e
            );
            throw e;
        }
    }

    /**
     * Аутентификация пользователя
     *
     * @param request
     *         данные пользователя
     *
     * @return токен
     */
    @Override
    public JwtAuthResponse authentication(AuthRequest request) {

        log.debug(
                "[username={}] Получен запрос на аутентификацию пользователя.",
                request.getUsername()
        );

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));

            UserEntity user = userService.getByUsername(request.getUsername());

            log.debug(
                    "[userId={}] Успешно выполнена аутентификация пользователя.",
                    user.getId()
            );

            return generateTokens(user);
        } catch (Exception e) {
            log.error(
                    "[username={}] Ошибка во время аутентификации пользователя.",
                    request.getUsername(),
                    e
            );
            throw e;
        }
    }

    /**
     * Обновление JWT-токена.
     * Проверяет действительность текущего токена и выдает новый.
     *
     * @param refreshToken
     *         текущий токен
     *
     * @return объект с новым JWT-токеном
     *
     * @throws InvalidRefreshTokenException
     *          если refresh-токен недействителен
     */
    @Override
    public JwtAuthResponse refreshToken(String refreshToken) {

        try {
            String username = jwtService.extractUserName(refreshToken);

            log.debug(
                    "[username={}] Получен запрос на обновление JWT-токена.",
                    username
            );

            UserDetails user = userService.userDetailsService().loadUserByUsername(username);

            if (!jwtService.isTokenValid(refreshToken, user)) {
                log.error(
                        "[username={}] Получен невалидный refresh-токен.",
                        username
                );
                throw new InvalidRefreshTokenException("Невалидный refresh-токен");
            }

            log.debug(
                    "[username={}] Успешно обновлен JWT-токен.",
                    username
            );

            return JwtAuthResponse.builder()
                    .accessToken(jwtService.generateToken(user))
                    .refreshToken(jwtService.generateRefreshToken(user))
                    .build();
        } catch (Exception e) {
            log.error(
                    "Ошибка во время обновления JWT-токена.",
                    e
            );
            throw e;
        }
    }

    /**
     * Генерирует пару JWT-токенов (access/refresh) для указанного пользователя.
     *
     * @param user
     *         пользователь, для которого создаются токены
     *
     * @return объект с парой токенов
     */
    private JwtAuthResponse generateTokens(UserEntity user) {
        return JwtAuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .role(user.getRole())
                .build();
    }
}