package org.flickit.assessment.data.jpa.core.subjectinsight;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.AbstractEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(SubjectInsightJpaEntity.EntityId.class)
@Table(name = "fac_subject_insight")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class SubjectInsightJpaEntity extends AbstractEntity<SubjectInsightJpaEntity.EntityId> {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "assessment_result_id", nullable = false)
    private UUID assessmentResultId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "insight", nullable = false)
    private String insight;

    @Column(name = "insight_time", nullable = false)
    private LocalDateTime insightTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "insight_by")
    private UUID insightBy;

    @Column(name = "approved", nullable = false)
    private Boolean approved;

    @Override
    public EntityId getId() {
        return new EntityId(assessmentResultId, subjectId);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private UUID assessmentResultId;
        private Long subjectId;
    }
}
