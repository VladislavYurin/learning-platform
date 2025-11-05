package ru.mentor.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("course_tags")
public class CourseTagEntity {

    @Id
    @Column("id_tag")
    private Long id;

    @Column("tag_name")
    private String tagName;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("is_active")
    private Boolean isActive;

}
