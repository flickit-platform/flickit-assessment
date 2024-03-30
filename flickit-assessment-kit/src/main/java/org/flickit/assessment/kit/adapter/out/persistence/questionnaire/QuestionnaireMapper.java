package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireListItemView;
import org.flickit.assessment.data.jpa.kit.subject.SubjectWithQuestionnaireIdView;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.QuestionnaireListItem;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireMapper {

    public static Questionnaire mapToDomainModel(QuestionnaireJpaEntity entity) {
        return new Questionnaire(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getDescription(),
            entity.getCreationTime(),
            entity.getLastModificationTime()
        );
    }

    static QuestionnaireJpaEntity mapToJpaEntityToPersist(Questionnaire questionnaire, Long kitVersionId, UUID createdBy) {
        return new QuestionnaireJpaEntity(
            null,
            UUID.randomUUID(),
            questionnaire.getCode(),
            questionnaire.getIndex(),
            questionnaire.getTitle(),
            questionnaire.getDescription(),
            kitVersionId,
            questionnaire.getCreationTime(),
            questionnaire.getLastModificationTime(),
            createdBy,
            createdBy
        );
    }

    public static QuestionnaireListItem mapToListItem(QuestionnaireListItemView questionnaireView, List<SubjectWithQuestionnaireIdView> subjectsView, int answerCount){
        var subjects = subjectsView.stream()
            .map(s -> new QuestionnaireListItem.Subject(s.getId(), s.getTitle()))
            .toList();
        int progress = (int) Math.floor(((double) answerCount / questionnaireView.getQuestionCount()) * 100);

        return new QuestionnaireListItem(
            questionnaireView.getId(),
            questionnaireView.getTitle(),
            questionnaireView.getIndex(),
            questionnaireView.getQuestionCount(),
            answerCount,
            progress,
            subjects
        );
    }
}
