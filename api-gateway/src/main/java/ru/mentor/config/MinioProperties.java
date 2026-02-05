package ru.mentor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
public class MinioProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucket;
}
