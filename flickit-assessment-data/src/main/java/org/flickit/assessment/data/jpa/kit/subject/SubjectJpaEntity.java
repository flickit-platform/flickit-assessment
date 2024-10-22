package org.flickit.assessment.data.jpa.kit.subject;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(SubjectJpaEntity.EntityId.class)
@Table(name = "fak_subject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class SubjectJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_subject_id_seq")
    @SequenceGenerator(name = "fak_subject_id_seq", sequenceName = "fak_subject_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "code", length = 100, nullable = false)
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private long id;
        private long kitVersionId;
    }
}
