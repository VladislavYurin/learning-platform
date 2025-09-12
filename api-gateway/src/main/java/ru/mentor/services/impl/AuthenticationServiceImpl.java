package ru.mentor.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mentor.constant.Role;
import ru.mentor.dto.auth.AuthRequest;
import ru.mentor.dto.auth.JwtAuthResponse;
import ru.mentor.dto.auth.RegRequest;
import ru.mentor.entity.UserEntity;
import ru.mentor.kafka.KafkaFacade;
import ru.mentor.services.AuthenticationService;
import ru.mentor.services.JwtService;
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

    /**
     * Регистрация пользователя
     *
     * @param request
     *         данные пользователя
     *
     * @return токен
     */
    @Override
    public JwtAuthResponse registration(RegRequest request) {

        var user = UserEntity.builder()
                             .username(request.getUsername())
                             .password(passwordEncoder.encode(request.getPassword()))
                             .tgNickname(request.getTgNickname())
                             .firstName(request.getFirstName())
                             .lastName(request.getLastName())
                             .role(Role.USER)
                             .build();

        userService.create(user);
        kafkaFacade.sendUserRegistrationMessage(user);
        return generateTokens(user);

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
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        UserEntity user = userService.getByUsername(request.getUsername());
        return generateTokens(user);

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
     * @throws RuntimeException
     *         если токен недействителен
     */
    @Override
    public JwtAuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUserName(refreshToken);
        UserDetails user = userService.userDetailsService().loadUserByUsername(username);

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Невалидный refresh-токен");
        }

        return JwtAuthResponse.builder()
                              .accessToken(jwtService.generateToken(user))
                              .refreshToken(jwtService.generateRefreshToken(user))
                              .build();

    }

    /**
     * Генерирует пару JWT-токенов (access/refresh) для указанного пользователя.
     * @param user пользователь, для которого создаются токены
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
