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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "submodules")
public class SubmoduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "submodule_id_seq")
    @SequenceGenerator(name = "submodule_id_seq", sequenceName = "submodule_id_seq", allocationSize = 1)
    @Column(name = "id_submodule")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_module", nullable = false)
    private ModuleEntity module;

    @Column(name = "submodule_title", nullable = false, columnDefinition = "TEXT")
    private String submoduleTitle;

    @Column(name = "submodule_content", nullable = false, columnDefinition = "TEXT")
    private String submoduleContent;

    @Column(name = "submodule_number", nullable = false)
    private Integer submoduleOrderNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

}
