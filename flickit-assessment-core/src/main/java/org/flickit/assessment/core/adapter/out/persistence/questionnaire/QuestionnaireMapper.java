package org.flickit.assessment.core.adapter.out.persistence.questionnaire;

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
                                                      int nextQuestion){
        var subjects = subjectsView.stream()
            .map(s -> new QuestionnaireListItem.Subject(s.getId(), s.getTitle()))
            .toList();
        int progress = (int) Math.floor(((double) answerCount / questionnaireView.getQuestionCount()) * 100);

        return new QuestionnaireListItem(
            questionnaireView.getId(),
            questionnaireView.getTitle(),
            questionnaireView.getDescription(),
            questionnaireView.getIndex(),
            questionnaireView.getQuestionCount(),
            answerCount,
            nextQuestion,
            progress,
            subjects
        );
    }

    public static Questionnaire mapToDomainModel(QuestionnaireJpaEntity entity) {
        return new Questionnaire(entity.getId(), entity.getTitle());
    }
}
