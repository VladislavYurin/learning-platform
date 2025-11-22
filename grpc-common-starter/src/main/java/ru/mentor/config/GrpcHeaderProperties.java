package ru.mentor.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "grpc.common")
@Setter
@Getter
public class GrpcHeaderProperties {

    @NotBlank
    private String nodeId;

    @NotBlank
    private String apiKey;

}
