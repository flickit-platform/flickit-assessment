package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.UpdateKitValidator;
import org.flickit.assessment.kit.common.Notification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.flickit.assessment.kit.application.service.assessmentkit.validate.impl.DslFieldNames.QUESTION;

@Service
@RequiredArgsConstructor
public class QuestionUpdateKitValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification notification = new Notification();

        var savedQuestionnaires = savedKit.getQuestionnaires();
        var dslQuestionnaires = dslKit.getQuestionnaires();

        Map<String, Questionnaire> savedQuestionnaireCodesMap = savedQuestionnaires.stream().collect(toMap(Questionnaire::getCode, q -> q));
        Map<String, QuestionnaireDslModel> dslQuestionnaireCodesMap = dslQuestionnaires.stream().collect(toMap(QuestionnaireDslModel::getCode, q -> q));

        List<String> sameQuestionnaires = savedQuestionnaireCodesMap.keySet().stream()
            .filter(s -> dslQuestionnaireCodesMap.keySet().stream()
                .anyMatch(s::equals))
            .toList();

        sameQuestionnaires.forEach(q -> {
            Questionnaire questionnaire = savedQuestionnaireCodesMap.get(q);
            if (Objects.nonNull(questionnaire.getQuestions())) {
                List<Question> savedQuestions = questionnaire.getQuestions();
                List<QuestionDslModel> dslQuestions = dslKit.getQuestions().stream().filter(i -> i.getQuestionnaireCode().equals(q)).toList();

                Map<String, Question> savedQuestionCodesMap = savedQuestions.stream().collect(toMap(Question::getCode, i -> i));
                Map<String, QuestionDslModel> dslQuestionCodesMap = dslQuestions.stream().collect(toMap(QuestionDslModel::getCode, i -> i));

                var deletedQuestions = savedQuestionCodesMap.keySet().stream()
                    .filter(s -> !dslQuestionCodesMap.containsKey(s))
                    .collect(toSet());

                var newQuestions = dslQuestionCodesMap.keySet().stream()
                    .filter(s -> !savedQuestionCodesMap.containsKey(s))
                    .collect(toSet());

                if (!deletedQuestions.isEmpty()) {
                    notification.add(new InvalidDeletionError(QUESTION, deletedQuestions));
                }

                if (!newQuestions.isEmpty()) {
                    notification.add(new InvalidAdditionError(QUESTION, newQuestions));
                }
            }

        });

        return notification;
    }

}
