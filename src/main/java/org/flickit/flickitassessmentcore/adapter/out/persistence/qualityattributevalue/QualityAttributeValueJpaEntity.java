package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "assessment_qualityattributevalue")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class QualityAttributeValueJpaEntity {
    @Id
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

    @Override
    public String toString() {
        return id.toString();
    }

}
