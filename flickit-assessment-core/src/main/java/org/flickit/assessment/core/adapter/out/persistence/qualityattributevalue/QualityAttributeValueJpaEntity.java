package org.flickit.assessment.core.adapter.out.persistence.qualityattributevalue;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.core.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
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

    @Column(name = "quality_attribute_id", nullable = false)
    private Long qualityAttributeId;

    @Column(name = "maturity_level_id")
    private Long maturityLevelId;
}
