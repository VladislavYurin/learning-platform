package ru.mentor.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.mentor.constant.Role;
import ru.mentor.dto.auth.AuthRequest;
import ru.mentor.dto.auth.JwtAuthResponse;
import org.springframework.web.multipart.MultipartFile;
import ru.mentor.dto.auth.RegRequest;
import ru.mentor.entity.UserEntity;
import ru.mentor.kafka.KafkaFacade;
import ru.mentor.services.JwtService;
import ru.mentor.services.UserAvatarService;
import ru.mentor.services.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private KafkaFacade kafkaFacade;

    @Mock
    private UserAvatarService userAvatarService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private static final String TG_NICKNAME = "@user_test";
    private static final String FIRST_NAME = "Влад";
    private static final String LAST_NAME = "Юрин";
    private static final String USERNAME = "user@test";
    private static final String PASSWORD = "password123";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    private UserEntity testUser;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(1L)
                .username(USERNAME)
                .password("encoded")
                .role(Role.USER)
                .build();

        authRequest = new AuthRequest(USERNAME, PASSWORD);
    }

    @Test
    void registration_withoutAvatar_createsUserAndReturnsTokens() {
        RegRequest request = new RegRequest(USERNAME, PASSWORD);
        request.setTgNickname(TG_NICKNAME);
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);

        when(passwordEncoder.encode(PASSWORD)).thenReturn("encoded");
        when(userService.create(any(UserEntity.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(testUser)).thenReturn(REFRESH_TOKEN);

        JwtAuthResponse result = authenticationService.registration(request, null);

        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void registration_withAvatar_uploadsAvatarAndReturnsTokens() {
        RegRequest request = new RegRequest(USERNAME, PASSWORD);
        request.setTgNickname(TG_NICKNAME);
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);

        MultipartFile mockAvatar = mock(MultipartFile.class);
        String avatarKey = "avatars/user-123.jpg";

        when(userAvatarService.uploadUserAvatar(mockAvatar)).thenReturn(avatarKey);
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encoded");
        when(userService.create(any(UserEntity.class))).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(testUser)).thenReturn(REFRESH_TOKEN);

        JwtAuthResponse result = authenticationService.registration(request, mockAvatar);

        verify(userAvatarService).uploadUserAvatar(eq(mockAvatar));
        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void authentication_validCredentials_returnsTokens() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD, List.of()));
        when(userService.getByUsername(USERNAME)).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(testUser)).thenReturn(REFRESH_TOKEN);

        JwtAuthResponse result = authenticationService.authentication(authRequest);

        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void refreshToken_validToken_returnsNewTokens() {
        when(jwtService.extractUserName(REFRESH_TOKEN)).thenReturn(USERNAME);
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(testUser);
        when(jwtService.isTokenValid(REFRESH_TOKEN, testUser)).thenReturn(true);
        when(jwtService.generateToken(testUser)).thenReturn(ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(testUser)).thenReturn(REFRESH_TOKEN);

        JwtAuthResponse result = authenticationService.refreshToken(REFRESH_TOKEN);

        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.getAccessToken());
        assertEquals(REFRESH_TOKEN, result.getRefreshToken());
    }

    @Test
    void refreshToken_invalidToken_throwsException() {
        when(jwtService.extractUserName(REFRESH_TOKEN)).thenReturn(USERNAME);
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(testUser);
        when(jwtService.isTokenValid(REFRESH_TOKEN, testUser)).thenReturn(false);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> authenticationService.refreshToken(REFRESH_TOKEN));

        assertEquals("Невалидный refresh-токен", thrown.getMessage());
    }
}
