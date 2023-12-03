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
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_QUESTION_ADDITION_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_QUESTION_DELETION_NOT_ALLOWED;

@Service
@RequiredArgsConstructor
public class QuestionUpdateKitValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification result = new Notification();

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
                List<QuestionDslModel> newQuestions = dslKit.getQuestions().stream().filter(i -> i.getQuestionnaireCode().equals(q)).toList();

                Map<String, Question> savedQuestionCodesMap = savedQuestions.stream().collect(toMap(Question::getCode, i -> i));
                Map<String, QuestionDslModel> dslQuestionCodesMap = newQuestions.stream().collect(toMap(QuestionDslModel::getCode, i -> i));

                if (isAnyDeleted(savedQuestionCodesMap.keySet(), dslQuestionCodesMap.keySet())) {
                    result.add(UPDATE_KIT_BY_DSL_QUESTION_DELETION_NOT_ALLOWED);
                }

                if (isAdded(savedQuestionCodesMap.keySet(), dslQuestionCodesMap.keySet())) {
                    result.add(UPDATE_KIT_BY_DSL_QUESTION_ADDITION_NOT_ALLOWED);
                }
            }

        });

        return result;
    }

    private boolean isAnyDeleted(Set<String> savedQuestionCodesSet, Set<String> dslQuestionCodesSet) {
        return !savedQuestionCodesSet.stream()
            .filter(s -> dslQuestionCodesSet.stream()
                .noneMatch(i -> i.equals(s)))
            .toList()
            .isEmpty();
    }

    private boolean isAdded(Set<String> savedQuestionCodesSet, Set<String> dslQuestionCodesSet) {
        return !dslQuestionCodesSet.stream()
            .filter(s -> savedQuestionCodesSet.stream()
                .noneMatch(i -> i.equals(s)))
            .toList()
            .isEmpty();
    }
}
