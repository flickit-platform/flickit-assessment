package org.flickit.assessment.data.jpa.core.attributevalue;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "fac_quality_attribute_value")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QualityAttributeValueJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assessment_result_id", referencedColumnName = "id", nullable = false)
    private AssessmentResultJpaEntity assessmentResult;

    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "attribute_reference_number", nullable = false)
    private UUID attributeReferenceNumber;

    @Column(name = "maturity_level_id")
    private Long maturityLevelId;

    @Column(name = "confidence_value")
    private Double confidenceValue;

}
