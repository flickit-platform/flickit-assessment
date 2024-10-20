package org.flickit.assessment.data.jpa.kit.subjectquestionnaire;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fak_subject_questionnaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubjectQuestionnaireJpaEntity implements Cloneable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_subject_questionnaire_id_seq")
    @SequenceGenerator(name = "fak_subject_questionnaire_id_seq",
        sequenceName = "fak_subject_questionnaire_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "questionnaire_id", nullable = false)
    private Long questionnaireId;

    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Override
    public SubjectQuestionnaireJpaEntity clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (SubjectQuestionnaireJpaEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
