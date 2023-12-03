package org.flickit.assessment.data.jpa.kit.questionimpact;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "baseinfo_questionimpact")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class QuestionImpactJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_metricimpact_id_seq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "level")
    private Integer level;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "quality_attribute_id", nullable = false)
    private Long qualityAttributeId;

    @Column(name = "maturity_level_id")
    private Long maturityLevelId;
}
