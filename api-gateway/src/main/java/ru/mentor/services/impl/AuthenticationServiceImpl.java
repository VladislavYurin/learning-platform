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
                             .tgNickname(request.getTgName())
                             .firstName(request.getFirstName())
                             .lastName(request.getLastName())
                             .role(Role.USER)
                             .build();

        userService.create(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthResponse(jwt);
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

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthResponse(jwt);
    }

    /**
     * Обновление JWT-токена.
     * Проверяет действительность текущего токена и выдает новый.
     *
     * @param token
     *         текущий токен
     *
     * @return объект с новым JWT-токеном
     *
     * @throws RuntimeException
     *         если токен недействителен
     */
    @Override
    public JwtAuthResponse refreshToken(String token) {
        String username = jwtService.extractUserName(token);

        UserDetails userDetails = userService
                .userDetailsService()
                .loadUserByUsername(username);

        if (jwtService.isTokenValid(token, userDetails)) {
            String newToken = jwtService.generateToken(userDetails);
            return new JwtAuthResponse(newToken);
        }

        throw new RuntimeException("Недопустимый токен");
    }

}
