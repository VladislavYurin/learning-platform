package ru.mentor.dto.kafka;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.mentor.dto.UserInfoDto;

@Data
@Builder
public class ModuleAccessGrantedNotificationPayload implements NotificationPayload {

    private String courseTitle;

    private String moduleTitle;

    private LocalDateTime accessGrantedAt;

    private UserInfoDto accessGrantedBy;

}
