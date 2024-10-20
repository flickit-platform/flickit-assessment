package org.flickit.assessment.data.jpa.kit.questionimpact;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(QuestionImpactJpaEntity.EntityId.class)
@Table(name = "fak_question_impact")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QuestionImpactJpaEntity implements Cloneable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "fak_question_impact_id_seq")
    @SequenceGenerator(name = "fak_question_impact_id_seq", sequenceName = "fak_question_impact_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "attribute_id", nullable = false)
    private Long attributeId;

    @Column(name = "maturity_level_id", nullable = false)
    private Long maturityLevelId;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    public QuestionImpactJpaEntity(Long id,
                                   Long kitVersionId,
                                   Integer weight,
                                   Long questionId,
                                   Long attributeId,
                                   Long maturityLevelId,
                                   LocalDateTime creationTime,
                                   LocalDateTime lastModificationTime,
                                   UUID createdBy,
                                   UUID lastModifiedBy) {
        this.id = id;
        this.kitVersionId = kitVersionId;
        this.weight = weight;
        this.questionId = questionId;
        this.attributeId = attributeId;
        this.maturityLevelId = maturityLevelId;
        this.creationTime = creationTime;
        this.lastModificationTime = lastModificationTime;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public QuestionImpactJpaEntity clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (QuestionImpactJpaEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private Long id;
        private Long kitVersionId;
    }
}
