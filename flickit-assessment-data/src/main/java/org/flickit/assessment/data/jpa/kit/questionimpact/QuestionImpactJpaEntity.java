package org.flickit.assessment.data.jpa.kit.questionimpact;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@IdClass(QuestionImpactJpaEntity.EntityId.class)
@Table(name = "fak_question_impact")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QuestionImpactJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_question_impact_id_seq")
    @SequenceGenerator(name = "fak_question_impact_id_seq", sequenceName = "fak_question_impact_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "attribute_id", nullable = false)
    private Long attributeId;

    @Column(name = "maturity_level_id", nullable = false)
    private Long maturityLevelId;

    @OneToMany(mappedBy = "questionImpact", cascade = CascadeType.REMOVE)
    private List<AnswerOptionImpactJpaEntity> answerOptionImpacts;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    public QuestionImpactJpaEntity(Long id,
                                   Integer weight,
                                   Long kitVersionId,
                                   Long questionId,
                                   Long attributeId,
                                   Long maturityLevelId,
                                   LocalDateTime creationTime,
                                   LocalDateTime lastModificationTime,
                                   UUID createdBy,
                                   UUID lastModifiedBy) {
        this.id = id;
        this.weight = weight;
        this.kitVersionId = kitVersionId;
        this.questionId = questionId;
        this.attributeId = attributeId;
        this.maturityLevelId = maturityLevelId;
        this.creationTime = creationTime;
        this.lastModificationTime = lastModificationTime;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private Long id;
        private Long kitVersionId;
    }
}
