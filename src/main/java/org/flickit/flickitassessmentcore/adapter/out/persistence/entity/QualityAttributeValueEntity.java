package org.flickit.flickitassessmentcore.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "assessment_qualityattributevalue")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class QualityAttributeValueEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "assessment_result_id", nullable = false)
    private UUID assessmentResultId;
    @Column(name = "quality_attribute_id", nullable = false)
    private Long qualityAttributeId;
    @Column(name = "maturity_level_id")
    private Long maturityLevelId;

    @Override
    public String toString() {
        return id.toString();
    }

}
