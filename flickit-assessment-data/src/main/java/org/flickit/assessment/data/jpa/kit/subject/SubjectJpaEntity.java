package org.flickit.assessment.data.jpa.kit.subject;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fak_subject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubjectJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_subject_id_seq")
    @SequenceGenerator(name = "fak_subject_id_seq", sequenceName = "fak_subject_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "index", nullable = false)
    private Integer index;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "weight", nullable = false)
    private Integer weight = 1;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    @Column(name = "kit_id", nullable = false)
    private Long kitId;

    public SubjectJpaEntity(Long id, String code, String title, String description, LocalDateTime creationTime,
                            LocalDateTime lastModificationTime, Long kitId, Integer index) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.description = description;
        this.creationTime = creationTime;
        this.lastModificationTime = lastModificationTime;
        this.kitId = kitId;
        this.index = index;
    }

    @OneToMany(mappedBy = "subject")
    private List<AttributeJpaEntity> attributes;

    public SubjectJpaEntity(Long subjectId) {
        this.id = subjectId;
    }
}
