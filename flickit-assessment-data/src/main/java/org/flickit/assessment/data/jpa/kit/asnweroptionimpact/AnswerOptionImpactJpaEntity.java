package org.flickit.assessment.data.jpa.kit.asnweroptionimpact;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(AnswerOptionImpactJpaEntity.EntityId.class)
@Table(name = "fak_answer_option_impact")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnswerOptionImpactJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Column(name = "question_impact_id", nullable = false)
    private Long questionImpactId;

    @Column(name = "value")
    private Double value;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
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
