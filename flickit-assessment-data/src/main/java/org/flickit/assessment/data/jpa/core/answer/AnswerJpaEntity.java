package org.flickit.assessment.data.jpa.core.answer;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "fac_answer")
@AuditTable("fac_answer_au")
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
    @Audited
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assessment_result_id", referencedColumnName = "id", nullable = false)
    private AssessmentResultJpaEntity assessmentResult;

    @Column(name = "questionnaire_id", nullable = false)
    private Long questionnaireId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "question_ref_num", nullable = false)
    private UUID questionRefNum;

    @Column(name = "answer_option_id")
    @Audited
    private Long answerOptionId;

    @Column(name = "confidence_level_id")
    @Audited
    private Integer confidenceLevelId;

    @Column(name = "is_not_applicable")
    @Audited
    private Boolean isNotApplicable;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    @Audited
    private UUID lastModifiedBy;

    @Override
    public String toString() {
        return id.toString();
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String QUESTION_ID = "questionId";
        public static final String QUESTION_INDEX = "questionIndex";
    }
}
