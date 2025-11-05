package ru.mentor.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("course_tag_link")
public class CourseTagLinkEntity {

    @Id
    private Long idCourseTag;
    private Long idCourse;
    private Long idTag;
    private LocalDateTime createdAt;

}
