package ru.mentor.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table("user_course_access")
public class UserCourseAccessEntity {

    @Id
    @Column("id_access")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("course_id")
    private Long courseId;

    @Column("access_granted_by")
    private Long accessGrantedByUserId;

    @Column("access_granted_at")
    private LocalDateTime accessGrantedAt;

}
