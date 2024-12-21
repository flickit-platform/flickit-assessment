package org.flickit.assessment.data.jpa.core.assessmentresult;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fac_assessment_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentResultJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assessment_id", referencedColumnName = "id", nullable = false)
    private AssessmentJpaEntity assessment;

    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "maturity_level_id")
    private Long maturityLevelId;

    @Column(name = "confidence_value")
    private Double confidenceValue;

    @Column(name = "is_calculate_valid")
    private Boolean isCalculateValid;

    @Column(name = "is_confidence_valid")
    private Boolean isConfidenceValid;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "last_calculation_time")
    private LocalDateTime lastCalculationTime;

    @Column(name = "last_confidence_calculation_time")
    private LocalDateTime lastConfidenceCalculationTime;
}
