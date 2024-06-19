package org.flickit.assessment.data.jpa.core.evidence;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "fac_evidence")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EvidenceJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    @Column(name = "assessment_id", nullable = false)
    private UUID assessmentId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "type")
    private Integer type;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String LAST_MODIFICATION_TIME = "lastModificationTime";
    }
}
