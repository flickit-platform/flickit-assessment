package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "fac_answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AnswerJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assessment_result_id", referencedColumnName = "id", nullable = false)
    private AssessmentResultJpaEntity assessmentResult;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "answer_option_id")
    private Long answerOptionId;

    @Column(name = "is_applicable")
    private Boolean isApplicable;

    @Override
    public String toString() {
        return id.toString();
    }
}
