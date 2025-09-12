package ru.mentor.dto.kafka;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import ru.mentor.dto.UserInfoDto;

@Data
@Builder
public class CourseAccessGrantedNotificationPayload implements NotificationPayload {

    private String courseTitle;

    private LocalDateTime accessGrantedAt;

    private UserInfoDto accessGrantedBy;

}
