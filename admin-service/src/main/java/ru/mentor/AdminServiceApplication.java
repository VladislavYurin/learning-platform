package ru.mentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * Точка входа в admin-service.
 *
 * <p>Сервис поднимает gRPC endpoints для административных операций и использует
 * реактивный стек на базе R2DBC.</p>
 */
@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "ru.mentor.repository")
public class AdminServiceApplication {

    /**
     * Запускает Spring Boot приложение admin-service.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }

}
