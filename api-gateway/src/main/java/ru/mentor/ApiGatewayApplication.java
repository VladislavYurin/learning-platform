package ru.mentor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.mentor.config.MinioProperties;
import ru.mentor.config.UserAvatarProperties;

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties({MinioProperties.class, UserAvatarProperties.class})
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}