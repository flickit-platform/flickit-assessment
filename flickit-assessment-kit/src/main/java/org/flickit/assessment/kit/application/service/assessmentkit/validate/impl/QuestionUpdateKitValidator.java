package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.UpdateKitValidator;
import org.flickit.assessment.kit.common.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_QUESTION_ADDITION_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_KIT_BY_DSL_QUESTION_DELETION_NOT_ALLOWED;

@Service
@RequiredArgsConstructor
public class QuestionUpdateKitValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification result = new Notification();

        var savedQuestions = savedKit.getQuestionnaires().stream().flatMap(q -> q.getQuestions().stream()).toList();
        var newQuestions = dslKit.getQuestions();

        Map<String, Question> savedQuestionCodesMap = savedQuestions.stream().collect(Collectors.toMap(Question::getCode, i -> i));
        Map<String, QuestionDslModel> dslQuestionCodesMap = newQuestions.stream().collect(Collectors.toMap(QuestionDslModel::getCode, i -> i));

        if (isAnyDeleted(savedQuestionCodesMap.keySet(), dslQuestionCodesMap.keySet())) {
            result.add(UPDATE_KIT_BY_DSL_QUESTION_DELETION_NOT_ALLOWED);
        }

        if (isAdded(savedQuestionCodesMap.keySet(), dslQuestionCodesMap.keySet())) {
            result.add(UPDATE_KIT_BY_DSL_QUESTION_ADDITION_NOT_ALLOWED);
        }

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
