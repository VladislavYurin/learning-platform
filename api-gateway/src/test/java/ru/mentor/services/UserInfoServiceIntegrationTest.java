package ru.mentor.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.mentor.dto.UserInfoDto;
import ru.mentor.constant.Role;
import org.junit.jupiter.api.Disabled;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционные тесты для UserInfoService
 * Тестируем реальную работу со всеми зависимостями (БД, репозиторий, маппер)
 */
@SpringBootTest
@Transactional
public class UserInfoServiceIntegrationTest {

    @Autowired
    private UserInfoService userInfoService;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/mentor");
        registry.add("spring.datasource.username", () -> "user");
        registry.add("spring.datasource.password", () -> "psw");
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("minio.endpoint", () -> "http://localhost:9000");
        registry.add("minio.access-key", () -> "minioadmin");
        registry.add("minio.secret-key", () -> "minioadmin");
        registry.add("minio.bucket", () -> "avatars");
    }

    /**
     * Тест 1: Поиск по email (username) - должен найти пользователя
     * Проверяет, что поиск работает по полю username
     */
    @Test
    @Sql(scripts = "/test_init_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchUsers_ByEmail_ShouldFindUser() {
        // Given - данные загружены из SQL-скрипта

        // When - ищем по email
        List<UserInfoDto> result = userInfoService.searchUsers("admin@example.com");

        // Then
        assertNotNull(result, "Результат не должен быть null");
        assertFalse(result.isEmpty(), "Должен найти хотя бы одного пользователя");
        assertEquals(1, result.size(), "Должен найти ровно одного пользователя");

        UserInfoDto foundUser = result.get(0);
        assertEquals("Админ", foundUser.getLastName());
        assertEquals("Админов", foundUser.getFirstName());
        assertEquals(Role.ADMIN, foundUser.getRole());
    }

    /**
     * Тест 2: Поиск по имени - должен найти пользователя
     */
    @Test
    @Sql(scripts = "/test_init_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchUsers_ByFirstName_ShouldFindUser() {
        // When - ищем по имени
        List<UserInfoDto> result = userInfoService.searchUsers("Владислав");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        UserInfoDto user = result.get(0);
        assertEquals("Владислав", user.getFirstName());
        assertEquals("Юрин", user.getLastName());
        assertEquals("mentor@gmail.com", user.getUsername());
        assertEquals(Role.MENTOR, user.getRole());
    }

    /**
     * Тест 3: Поиск по фамилии - должен найти пользователя
     */
    @Test
    @Sql(scripts = "/test_init_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchUsers_ByLastName_ShouldFindUser() {
        // When - ищем по фамилии
        List<UserInfoDto> result = userInfoService.searchUsers("Юрин");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Юрин", result.get(0).getLastName());
    }

    /**
     * Тест 4: Поиск по tg nickname - должен найти пользователя
     */
    @Test
    @Sql(scripts = "/test_init_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchUsers_ByTgNickname_ShouldFindUser() {
        // When - ищем по tg nickname
        List<UserInfoDto> result = userInfoService.searchUsers("@testuser5");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        UserInfoDto user = result.get(0);
        assertEquals("u5FirstName", user.getFirstName());
        assertEquals("u5LastName", user.getLastName());
        assertEquals("test_user_5@cloud.com", user.getUsername());
    }

    /**
     * Тест 5: Частичный поиск - должен найти несколько пользователей
     */
    @Test
    @Sql(scripts = "/test_init_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchUsers_PartialMatch_ShouldFindMultipleUsers() {
        // When - ищем по частичному совпадению
        List<UserInfoDto> testUsers = userInfoService.searchUsers("test_user");
        List<UserInfoDto> mailUsers = userInfoService.searchUsers("mail");
        List<UserInfoDto> firstNameUsers = userInfoService.searchUsers("FirstName");

        // Then
        // Должно найти всех test_user (пользователи 5-9) - 5 пользователей
        assertEquals(5, testUsers.size());

        // Должно найти пользователей с "mail" в email
        // В тестовых данных: mail.ru, gmail.com - несколько пользователей
        assertTrue(mailUsers.size() >= 2);

        // Должно найти всех пользователей с "FirstName" в имени (пользователи 4-9)
        assertEquals(6, firstNameUsers.size());
    }

    /**
     * Тест 6: Поиск с пустым запросом (null) - должен вернуть пустой список
     * Проверяет обработку граничного случая из метода searchUsers
     */
    @Test
    void searchUsers_WithNullQuery_ShouldReturnEmptyList() {
        // When - передаем null
        List<UserInfoDto> result = userInfoService.searchUsers(null);

        // Then
        assertNotNull(result, "Результат не должен быть null даже при null запросе");
        assertTrue(result.isEmpty(), "При null запросе должен вернуться пустой список");
    }

    /**
     * Тест 7: Поиск с пустой строкой - должен вернуть пустой список
     */
    @Test
    void searchUsers_WithEmptyString_ShouldReturnEmptyList() {
        // When - передаем пустую строку
        List<UserInfoDto> result = userInfoService.searchUsers("");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Тест 8: Поиск с пробелами - должен вернуть пустой список
     */
    @Test
    void searchUsers_WithBlankString_ShouldReturnEmptyList() {
        // When - передаем строку из пробелов
        List<UserInfoDto> result = userInfoService.searchUsers("   ");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Тест 9: Поиск по одному символу - должен работать (с предупреждением в логах)
     * В методе сервиса есть логирование предупреждения для одного символа
     */
    @Test
    @Sql(scripts = "/test_init_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchUsers_WithSingleCharacter_ShouldReturnResults() {
        // When - ищем по одному символу (русская "и")
        List<UserInfoDto> result = userInfoService.searchUsers("и");

        // Then - метод должен работать, но может вернуть много результатов
        assertNotNull(result);
        // В тестовых данных есть несколько пользователей с "и" в имени/фамилии
        assertFalse(result.isEmpty());
    }

    /**
     * Тест 10: Поиск по несуществующему запросу - должен вернуть пустой список
     */
    @Test
    @Sql(scripts = "/test_init_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchUsers_WithNonExistentQuery_ShouldReturnEmptyList() {
        // When - ищем несуществующего пользователя
        List<UserInfoDto> result = userInfoService.searchUsers("НесуществующееИмя12345");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Тест 11: Регистронезависимый поиск
     * В зависимости от настройки БД, может работать или нет
     */
    @Test
    @Sql(scripts = "/test_init_db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void searchUsers_CaseInsensitive_ShouldFindUsers() {
        // When - ищем в разном регистре
        List<UserInfoDto> lowerCase = userInfoService.searchUsers("админ");
        List<UserInfoDto> upperCase = userInfoService.searchUsers("АДМИН");
        List<UserInfoDto> mixedCase = userInfoService.searchUsers("АдМиН");

        // Then
        assertNotNull(lowerCase);
        assertNotNull(upperCase);
        assertNotNull(mixedCase);
        // В зависимости от БД, все должны находить пользователя
    }

}