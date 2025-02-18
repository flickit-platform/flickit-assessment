package org.flickit.assessment.core.adapter.out.persistence.kit.questionnaire;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Questionnaire;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireListItemView;
import org.flickit.assessment.data.jpa.kit.subject.SubjectWithQuestionnaireIdView;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireMapper {

    public static QuestionnaireListItem mapToListItem(QuestionnaireListItemView questionnaireView,
                                                      List<SubjectWithQuestionnaireIdView> subjectsView,
                                                      int answerCount,
                                                      int nextQuestion) {
        var questionnaireEntity = questionnaireView.getQuestionnaire();
        List<QuestionnaireListItem.Subject> subjects = List.of();
        if (subjectsView != null)
            subjects = subjectsView.stream()
                .map(s -> new QuestionnaireListItem.Subject(s.getId(), s.getTitle()))
                .toList();
        int progress = (int) Math.floor(((double) answerCount / questionnaireView.getQuestionCount()) * 100);

        return new QuestionnaireListItem(
            questionnaireEntity.getId(),
            questionnaireEntity.getTitle(),
            questionnaireEntity.getDescription(),
            questionnaireEntity.getIndex(),
            questionnaireView.getQuestionCount(),
            answerCount,
            nextQuestion,
            progress,
            subjects,
            null
        );
    }

    public static Questionnaire mapToDomainModel(QuestionnaireJpaEntity entity) {
        return new Questionnaire(entity.getId(), entity.getTitle());
    }
}
