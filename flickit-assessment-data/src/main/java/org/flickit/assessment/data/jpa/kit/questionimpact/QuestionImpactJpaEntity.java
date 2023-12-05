package org.flickit.assessment.data.jpa.kit.questionimpact;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

import java.util.List;

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
    @SequenceGenerator(name = "baseinfo_metricimpact_id_seq", sequenceName = "baseinfo_metricimpact_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "quality_attribute_id", nullable = false)
    private Long qualityAttributeId;

    @ManyToOne
    @JoinColumn(name = "maturity_level_id", referencedColumnName = "id")
    private MaturityLevelJpaEntity maturityLevel;

    public QuestionImpactJpaEntity(Long id, Integer weight, Long questionId, Long qualityAttributeId, MaturityLevelJpaEntity maturityLevel) {
        this.id = id;
        this.weight = weight;
        this.questionId = questionId;
        this.qualityAttributeId = qualityAttributeId;
        this.maturityLevel = maturityLevel;
    }

    @OneToMany(mappedBy = "questionImpact", cascade = CascadeType.REMOVE)
    private List<AnswerOptionImpactJpaEntity> answerOptionImpacts;
}
