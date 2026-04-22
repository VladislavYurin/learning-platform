package ru.mentor.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.mentor.constant.Role;
import ru.mentor.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private static final String JWT_SIGNING_KEY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
    private static final Long ACCESS_EXPIRATION_MINUTES = 15L;
    private static final Long REFRESH_EXPIRATION_DAYS = 7L;

    private JwtServiceImpl jwtService;
    private UserEntity currentUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey", JWT_SIGNING_KEY);
        ReflectionTestUtils.setField(jwtService, "accessExpirationMinutes", ACCESS_EXPIRATION_MINUTES);
        ReflectionTestUtils.setField(jwtService, "refreshExpirationDays", REFRESH_EXPIRATION_DAYS);

        currentUser = UserEntity.builder()
                .id(1L)
                .username("user@test")
                .role(Role.MENTOR)
                .build();
    }

    @Test
    void extractUserName_validToken_returnsUsername() {
        String token = jwtService.generateToken(currentUser);
        String userName = jwtService.extractUserName(token);
        assertEquals(currentUser.getUsername(), userName);
    }

    @Test
    void generateToken_userDetails_returnsValidToken() {
        String token = jwtService.generateToken(currentUser);
        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(currentUser.getUsername(), jwtService.extractUserName(token));
    }

    @Test
    void generateRefreshToken_userDetails_returnsValidToken() {
        String token = jwtService.generateRefreshToken(currentUser);
        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(currentUser.getUsername(), jwtService.extractUserName(token));
    }

    @Test
    void isTokenValid_sameUser_returnsTrue() {
        String token = jwtService.generateToken(currentUser);
        boolean valid = jwtService.isTokenValid(token, currentUser);
        assertTrue(valid);
    }

    @Test
    void isTokenValid_differentUser_returnsFalse() {
        String token = jwtService.generateToken(currentUser);
        UserEntity otherUser = UserEntity.builder()
                .id(2L)
                .username("other@test")
                .role(Role.MENTOR)
                .build();
        boolean valid = jwtService.isTokenValid(token, otherUser);
        assertFalse(valid);
    }
}
