package org.flickit.assessment.data.jpa.kit.asnweroptionimpact;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

@Entity
@Table(name = "baseinfo_optionvalue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnswerOptionImpactJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_optionvalue_id_seq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @JoinColumn(name = "question_impact_id", nullable = false)
    private Long questionImpactId;

    @JoinColumn(name = "option_id", nullable = false)
    private Long optionId;

    @Column(name = "value", nullable = false)
    private double value;
}
