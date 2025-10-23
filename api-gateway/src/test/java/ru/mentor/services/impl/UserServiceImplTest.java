package ru.mentor.services.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.mentor.constant.Role;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.UserException;
import ru.mentor.repository.UserRepository;
import ru.mentor.testUtil.TestConstantHolder;
import ru.mentor.testUtil.TestEntityStubGenerator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    private static final String TEST_PASSWORD = "testpassword";

    @Test
    void createUserSuccess() {
        // Given
        UserEntity userRequest = TestEntityStubGenerator.constructUserEntityWithRole(Role.USER);
        UserEntity expectedUser = TestEntityStubGenerator.constructUserEntityWithRole(Role.USER);
        expectedUser.setId(TestConstantHolder.userId);

        Mockito.when(userRepository.existsByUsername(TestConstantHolder.username))
                .thenReturn(false);
        Mockito.when(userRepository.save(userRequest))
                .thenReturn(expectedUser);

        // When
        UserEntity result = userService.create(userRequest);

        // Then
        Assertions.assertThat(result.getId())
                .isEqualTo(TestConstantHolder.userId);
        Assertions.assertThat(result.getUsername())
                .isEqualTo(TestConstantHolder.username);
        Assertions.assertThat(result.getFirstName())
                .isEqualTo(TestConstantHolder.firstName);
        Assertions.assertThat(result.getLastName())
                .isEqualTo(TestConstantHolder.lastName);
        Assertions.assertThat(result.getTgNickname())
                .isEqualTo(TestConstantHolder.tgNickname);
        Assertions.assertThat(result.getTgChatId())
                .isEqualTo(TestConstantHolder.tgChatId);
        Assertions.assertThat(result.getRole())
                .isEqualTo(Role.USER);

        Mockito.verify(userRepository).existsByUsername(TestConstantHolder.username);
        Mockito.verify(userRepository).save(userRequest);
    }

    @Test
    void createUserWhenUserExistsShouldThrowException() {
        // Given
        UserEntity userRequest = TestEntityStubGenerator.constructUserEntityWithRole(Role.USER);

        Mockito.when(userRepository.existsByUsername(TestConstantHolder.username))
                .thenReturn(true);

        // When & Then
        Assertions.assertThatThrownBy(() -> userService.create(userRequest))
                .isInstanceOf(UserException.class)
                .hasMessage("Юзер с username = " + TestConstantHolder.username + " уже существует");

        Mockito.verify(userRepository).existsByUsername(TestConstantHolder.username);
        Mockito.verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void existsByUserNameWhenUserExistsShouldReturnTrue() {
        // Given
        Mockito.when(userRepository.existsByUsername(TestConstantHolder.username))
                .thenReturn(true);

        // When
        boolean result = userService.existsByUserName(TestConstantHolder.username);

        // Then
        Assertions.assertThat(result).isTrue();
        Mockito.verify(userRepository).existsByUsername(TestConstantHolder.username);
    }

    @Test
    void existsByUserNameWhenUserNotExistsShouldReturnFalse() {
        // Given
        Mockito.when(userRepository.existsByUsername(TestConstantHolder.username))
                .thenReturn(false);

        // When
        boolean result = userService.existsByUserName(TestConstantHolder.username);

        // Then
        Assertions.assertThat(result).isFalse();
        Mockito.verify(userRepository).existsByUsername(TestConstantHolder.username);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void existsByUserNameWithNullOrEmptyUsernameShouldReturnFalse(String username) {
        // Given
        Mockito.when(userRepository.existsByUsername(username))
                .thenReturn(false);

        // When
        boolean result = userService.existsByUserName(username);

        // Then
        Assertions.assertThat(result).isFalse();
        Mockito.verify(userRepository).existsByUsername(username);
    }

    @Test
    void getByUsernameSuccess() {
        // Given
        UserEntity expectedUser = TestEntityStubGenerator.constructUserEntityWithRole(Role.MENTOR);

        Mockito.when(userRepository.findByUsernameOrThrow(TestConstantHolder.username))
                .thenReturn(expectedUser);

        // When
        UserEntity result = userService.getByUsername(TestConstantHolder.username);

        // Then
        Assertions.assertThat(result.getId())
                .isEqualTo(TestConstantHolder.userId);
        Assertions.assertThat(result.getUsername())
                .isEqualTo(TestConstantHolder.username);
        Assertions.assertThat(result.getRole())
                .isEqualTo(Role.MENTOR);
        Assertions.assertThat(result.getFirstName())
                .isEqualTo(TestConstantHolder.firstName);
        Assertions.assertThat(result.getLastName())
                .isEqualTo(TestConstantHolder.lastName);

        Mockito.verify(userRepository).findByUsernameOrThrow(TestConstantHolder.username);
    }

    @Test
    void getByUsernameWhenUserNotFoundShouldThrowException() {
        // Given
        Mockito.when(userRepository.findByUsernameOrThrow(TestConstantHolder.username))
                .thenThrow(new UsernameNotFoundException(TestConstantHolder.notFoundExceptionText));

        // When & Then
        Assertions.assertThatThrownBy(() -> userService.getByUsername(TestConstantHolder.username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(TestConstantHolder.notFoundExceptionText);

        Mockito.verify(userRepository).findByUsernameOrThrow(TestConstantHolder.username);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void getByUsernameWithNullOrEmptyUsernameShouldPropagateException(String username) {
        // Given
        Mockito.when(userRepository.findByUsernameOrThrow(username))
                .thenThrow(new UsernameNotFoundException(TestConstantHolder.notFoundExceptionText));

        // When & Then
        Assertions.assertThatThrownBy(() -> userService.getByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(TestConstantHolder.notFoundExceptionText);

        Mockito.verify(userRepository).findByUsernameOrThrow(username);
    }

    @Test
    void userDetailsServiceShouldReturnCorrectImplementation() {
        // When
        UserDetailsService userDetailsService = userService.userDetailsService();

        // Then
        Assertions.assertThat(userDetailsService).isNotNull();
    }

    @Test
    void userDetailsServiceLoadUserByUsernameSuccess() {
        // Given
        UserEntity expectedUser = TestEntityStubGenerator.constructUserEntityWithRole(Role.USER);
        // Устанавливаем пароль напрямую, так как его нет в TestConstantHolder
        expectedUser.setPassword(TEST_PASSWORD);

        Mockito.when(userRepository.findByUsernameOrThrow(TestConstantHolder.username))
                .thenReturn(expectedUser);

        UserDetailsService userDetailsService = userService.userDetailsService();

        // When
        var result = userDetailsService.loadUserByUsername(TestConstantHolder.username);

        // Then
        Assertions.assertThat(result.getUsername())
                .isEqualTo(TestConstantHolder.username);
        Assertions.assertThat(result.getPassword())
                .isEqualTo(TEST_PASSWORD); // Исправлено: используем локальную константу
        Assertions.assertThat(result.getAuthorities())
                .hasSize(1)
                .extracting("authority")
                .contains("ROLE_USER");

        Mockito.verify(userRepository).findByUsernameOrThrow(TestConstantHolder.username);
    }

    @Test
    void userDetailsServiceWithDifferentRolesShouldReturnCorrectAuthorities() {
        Role[] roles = {Role.USER, Role.MENTOR, Role.ADMIN};

        for (Role role : roles) {
            // Given
            UserEntity user = TestEntityStubGenerator.constructUserEntityWithRole(role);
            user.setPassword(TEST_PASSWORD); // Устанавливаем пароль
            Mockito.when(userRepository.findByUsernameOrThrow(TestConstantHolder.username))
                    .thenReturn(user);

            UserDetailsService userDetailsService = userService.userDetailsService();

            // When
            var result = userDetailsService.loadUserByUsername(TestConstantHolder.username);

            // Then
            Assertions.assertThat(result.getAuthorities())
                    .extracting("authority")
                    .contains("ROLE_" + role.name());

            // Reset mock for next iteration
            Mockito.reset(userRepository);
        }
    }

    @Test
    void getCurrentUserSuccess() {
        // Given
        UserEntity expectedUser = TestEntityStubGenerator.constructUserEntityWithRole(Role.USER);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn(TestConstantHolder.username);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(userRepository.findByUsernameOrThrow(TestConstantHolder.username))
                .thenReturn(expectedUser);

        // When
        UserEntity result = userService.getCurrentUser();

        // Then
        Assertions.assertThat(result.getId())
                .isEqualTo(TestConstantHolder.userId);
        Assertions.assertThat(result.getUsername())
                .isEqualTo(TestConstantHolder.username);
        Assertions.assertThat(result.getRole())
                .isEqualTo(Role.USER);

        Mockito.verify(userRepository).findByUsernameOrThrow(TestConstantHolder.username);
    }

    @Test
    void getCurrentUserWhenNotAuthenticatedShouldThrowException() {
        // Given
        SecurityContextHolder.clearContext();

        // When & Then
        Assertions.assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(Exception.class);
    }

    @Test
    void getCurrentUserIdSuccess() {
        // Given
        UserEntity expectedUser = TestEntityStubGenerator.constructUserEntityWithRole(Role.ADMIN);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(expectedUser);
        SecurityContextHolder.setContext(securityContext);

        // When
        Long result = userService.getCurrentUserId();

        // Then
        Assertions.assertThat(result)
                .isEqualTo(TestConstantHolder.userId);

        Mockito.verify(securityContext).getAuthentication();
        Mockito.verify(authentication).getPrincipal();
    }

    @Test
    void getCurrentUserIdWhenPrincipalIsNotUserEntityShouldThrowException() {
        // Given
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(securityContext);

        // When & Then
        Assertions.assertThatThrownBy(() -> userService.getCurrentUserId())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Пользователь не найден в контексте безопасности");

        Mockito.verify(securityContext).getAuthentication();
        Mockito.verify(authentication).getPrincipal();
    }

    @Test
    void getCurrentUserIdWhenUserEntityHasNullIdShouldThrowException() {
        // Given
        UserEntity userWithoutId = TestEntityStubGenerator.constructUserEntityWithRole(Role.USER);
        userWithoutId.setId(null);

        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(userWithoutId);
        SecurityContextHolder.setContext(securityContext);

        // When & Then
        Assertions.assertThatThrownBy(() -> userService.getCurrentUserId())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("У пользователя в контексте безопасности отсутствует ID"); // Исправлено сообщение

        Mockito.verify(securityContext).getAuthentication();
        Mockito.verify(authentication).getPrincipal();
    }

    @Test
    void createUserWithDifferentRolesShouldSaveSuccessfully() {
        for (Role role : Role.values()) {
            // Given
            UserEntity userRequest = TestEntityStubGenerator.constructUserEntityWithRole(role);
            UserEntity savedUser = TestEntityStubGenerator.constructUserEntityWithRole(role);
            savedUser.setId(TestConstantHolder.userId);

            Mockito.when(userRepository.existsByUsername(userRequest.getUsername()))
                    .thenReturn(false);
            Mockito.when(userRepository.save(userRequest))
                    .thenReturn(savedUser);

            // When
            UserEntity result = userService.create(userRequest);

            // Then
            Assertions.assertThat(result.getRole()).isEqualTo(role);

            // Reset mocks for next iteration
            Mockito.reset(userRepository);
        }
    }

    @Test
    void createUserWithNullUserShouldThrowException() {
        // When & Then
        Assertions.assertThatThrownBy(() -> userService.create(null))
                .isInstanceOf(NullPointerException.class);
    }
}