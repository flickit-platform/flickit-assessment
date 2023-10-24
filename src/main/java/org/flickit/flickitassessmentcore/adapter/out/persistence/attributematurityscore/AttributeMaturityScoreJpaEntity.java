package org.flickit.flickitassessmentcore.adapter.out.persistence.attributematurityscore;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "fac_attribute_maturity_score")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttributeMaturityScoreJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quality_attribute_value_id", referencedColumnName = "id", nullable = false)
    private QualityAttributeValueJpaEntity attributeValue;

    @Column(name = "maturity_level_id", nullable = false)
    private Long maturityLevelId;

    @Column(name = "score")
    private Double score;
}
