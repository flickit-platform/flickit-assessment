package org.flickit.assessment.data.jpa.qualityattribute;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.subject.SubjectJpaEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "baseinfo_qualityattribute")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QualityAttributeJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creation_time;

    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime last_modification_date;

    @ManyToOne
    @JoinColumn(name = "assessment_subject_id", referencedColumnName = "id")
    private SubjectJpaEntity subject;

    @Column(name = "index")
    private Integer index;

    @Column(name = "weight", nullable = false)
    private Integer weight;
}
