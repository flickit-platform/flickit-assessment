package org.flickit.assessment.data.jpa.kit.subject;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "baseinfo_assessmentsubject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubjectJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_assessmentsubject_id_seq")
    @SequenceGenerator(name = "baseinfo_assessmentsubject_id_seq", sequenceName = "baseinfo_assessmentsubject_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "assessment_kit_id", nullable = false)
    private Long assessmentKitId;

    @Column(name = "index", nullable = false)
    private Integer index;

    public SubjectJpaEntity(Long id, String code, String title, String description, LocalDateTime creationTime,
                            LocalDateTime lastModificationTime, Long assessmentKitId, Integer index) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.creationTime = creationTime;
        this.lastModificationTime = lastModificationTime;
        this.assessmentKitId = assessmentKitId;
        this.index = index;
    }

    @OneToMany(mappedBy = "subject")
    private List<AttributeJpaEntity> attributes;
}
