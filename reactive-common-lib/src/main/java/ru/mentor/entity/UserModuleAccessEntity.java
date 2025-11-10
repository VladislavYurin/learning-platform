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
@Table("user_module_access")
public class UserModuleAccessEntity {

    @Id
    @Column("id_access")
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("course_id")
    private Long courseId;

    @Column("module_id")
    private Long moduleId;

    @Column("access_granted_at")
    private LocalDateTime accessGrantedAt;

    @Column("access_granted_by")
    private Long accessGrantedByUserId;
}
