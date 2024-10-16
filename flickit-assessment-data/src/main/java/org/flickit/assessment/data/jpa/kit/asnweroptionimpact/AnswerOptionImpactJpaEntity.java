package org.flickit.assessment.data.jpa.kit.asnweroptionimpact;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fak_answer_option_impact")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnswerOptionImpactJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_answer_option_impact_id_seq")
    @SequenceGenerator(name = "fak_answer_option_impact_id_seq", sequenceName = "fak_answer_option_impact_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Column(name = "question_impact_id", nullable = false)
    private Long questionImpactId;

    @Column(name = "value", nullable = false)
    private double value;

    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

}
