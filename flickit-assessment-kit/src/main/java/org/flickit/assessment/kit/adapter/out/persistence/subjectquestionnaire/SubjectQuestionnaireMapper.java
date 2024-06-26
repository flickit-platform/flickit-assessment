package org.flickit.assessment.kit.adapter.out.persistence.subjectquestionnaire;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.subjectquestionnaire.SubjectQuestionnaireJpaEntity;
import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubjectQuestionnaireMapper {

    public static SubjectQuestionnaire mapToDomainModel(SubjectQuestionnaireJpaEntity entity) {
        return new SubjectQuestionnaire(
            entity.getId(),
            entity.getSubjectId(),
            entity.getQuestionnaireId());
    }

    public static SubjectQuestionnaireJpaEntity mapToJpaEntity(Long subjectId, Long questionnaireId, Long kitVersionId) {
        return new SubjectQuestionnaireJpaEntity(
            null,
            subjectId,
            questionnaireId,
            kitVersionId
        );
    }
}
