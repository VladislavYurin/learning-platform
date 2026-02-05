package ru.mentor.dto.avatar;

import java.io.InputStream;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserAvatarContentDto {
    InputStream inputStream;
    String contentType;
    String filename;
    Long size;
}
