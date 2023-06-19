package org.flickit.assessment.core.adapter.out.persistence.answer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "assessment_answer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AnswerJpaEntity {
    @Id
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

    @Override
    public String toString() {
        return id.toString();
    }
}
