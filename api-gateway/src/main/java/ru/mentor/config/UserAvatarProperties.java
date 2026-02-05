package ru.mentor.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "avatar")
@Setter
@Getter
public class UserAvatarProperties {

    private List<String> allowedExtensions;
    private List<String> allowedContentTypes;
    private long maxSizeBytes;

}
