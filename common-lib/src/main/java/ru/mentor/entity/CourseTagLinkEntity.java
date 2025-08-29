package ru.mentor.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course_tag_link",
        uniqueConstraints = @UniqueConstraint(name = "uk_course_tags_course_tag", columnNames = {"id_course_tag", "id_tag"}))
@Builder
@EqualsAndHashCode(of = "id")
public class CourseTagLinkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_course_tag")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_course", referencedColumnName = "id_course")
    private CourseEntity course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tag", referencedColumnName = "id_tag")
    private CourseTagEntity tag;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
