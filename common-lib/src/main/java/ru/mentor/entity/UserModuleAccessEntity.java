package ru.mentor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_module_access")
public class UserModuleAccessEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_module_access_seq")
    @SequenceGenerator(name = "user_module_access_seq", sequenceName = "user_module_access_seq", allocationSize = 1)
    @Column(name = "id_access")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleEntity module;

    @Column(name = "access_granted_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime accessGrantedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "access_granted_by", nullable = false)
    private UserEntity accessGrantedBy;

}
