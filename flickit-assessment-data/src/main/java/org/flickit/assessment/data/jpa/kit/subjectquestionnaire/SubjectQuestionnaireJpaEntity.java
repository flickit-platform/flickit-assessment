package org.flickit.assessment.data.jpa.kit.subjectquestionnaire;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "baseinfo_assessmentsubject_questionnaires")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubjectQuestionnaireJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_assessmentsubject_questionnaires_id_seq")
    @SequenceGenerator(name = "baseinfo_assessmentsubject_questionnaires_id_seq",
        sequenceName = "baseinfo_assessmentsubject_questionnaires_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "assessmentsubject_id", nullable = false)
    private Long subjectId;

    @Column(name = "questionnaire_id", nullable = false)
    private Long questionnaireId;
}
