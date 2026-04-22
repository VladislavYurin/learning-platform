package ru.mentor.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.UserException;
import ru.mentor.repository.UserRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .id(1L)
                .username("user@test")
                .password("encoded")
                .build();
    }

    @Test
    void create_userDoesNotExist_savesAndReturnsUser(){
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        UserEntity result = userService.create(testUser);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void create_userAlreadyExists_throwsUserException() {
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);

        UserException thrown = assertThrows(UserException.class,
                () -> userService.create(testUser));
        assertEquals(
                String.format("Юзер с username = %s уже существует", testUser.getUsername()),
                thrown.getMessage()
        );
    }

    @Test
    void existsByUserName_delegatesToRepository() {
        String username = "user@test";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        boolean result = userService.existsByUserName(username);

        assertTrue(result);
    }

    @Test
    void getByUsername_returnsUserFromRepository() {
        when(userRepository.findByUsernameOrThrow("user@test")).thenReturn(testUser);

        UserEntity result = userService.getByUsername("user@test");

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());

    }

    @Test
    void userDetailsService_loadUserByUsername_returnsUser() {
        when(userRepository.findByUsernameOrThrow("user@test")).thenReturn(testUser);

        UserDetailsService userDetailsService = userService.userDetailsService();
        UserDetails result = userDetailsService.loadUserByUsername("user@test");

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void getCurrentUser_returnsUserFromSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@test");

        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsernameOrThrow("user@test")).thenReturn(testUser);

        UserEntity result = userService.getCurrentUser();

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void getCurrentUserId_whenPrincipalIsUserEntity_returnsId() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);

        SecurityContextHolder.setContext(securityContext);

        Long result = userService.getCurrentUserId();

        assertNotNull(result);
        assertEquals(testUser.getId(), result);
    }

    @Test
    void getCurrentUserId_whenPrincipalIsNotUserEntity_throwsException() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("not-a-user");

        SecurityContextHolder.setContext(securityContext);

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> userService.getCurrentUserId());

        assertEquals("Пользователь не найден в контексте безопасности", thrown.getMessage());
    }

    @Test
    void getUserById_returnsUserFromRepository() {
        when(userRepository.findByIdOrThrow(1L)).thenReturn(testUser);

        UserEntity result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
    }
}
