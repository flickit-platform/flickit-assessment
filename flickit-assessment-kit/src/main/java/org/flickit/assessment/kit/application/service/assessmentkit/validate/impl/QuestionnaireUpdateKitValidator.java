package org.flickit.assessment.kit.application.service.assessmentkit.validate.impl;

import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.service.assessmentkit.validate.UpdateKitValidator;
import org.flickit.assessment.kit.common.Notification;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionnaireUpdateKitValidator implements UpdateKitValidator {

    @Override
    public Notification validate(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Notification result = new Notification();

        List<Questionnaire> savedQuestionnaires = savedKit.getQuestionnaires();
        List<QuestionnaireDslModel> dslQuestionnaires = dslKit.getQuestionnaires();

        Map<String, Questionnaire> savedQuestionnaireCodesMap = savedQuestionnaires.stream().collect(Collectors.toMap(Questionnaire::getCode, i -> i));
        Map<String, QuestionnaireDslModel> newQuestionnaireCodesMap = dslQuestionnaires.stream().collect(Collectors.toMap(QuestionnaireDslModel::getCode, i -> i));

        if (isAnyDeleted(savedQuestionnaireCodesMap.keySet(), newQuestionnaireCodesMap.keySet())) {
            result.add("");
        }

        if (isAnyChangedCode(savedQuestionnaireCodesMap, newQuestionnaireCodesMap)) {
            result.add("");
        }

        return result;
    }

    private boolean isAnyDeleted(Set<String> savedQuestionnaireCodesSet, Set<String> newQuestionnaireCodesSet) {
        return !savedQuestionnaireCodesSet.stream()
                .filter(s -> newQuestionnaireCodesSet.stream()
                        .noneMatch(i -> i.equals(s)))
                .toList().isEmpty();
    }

    private boolean isAnyChangedCode(Map<String, Questionnaire> savedQuestionnaireCodesMap, Map<String, QuestionnaireDslModel> newQuestionnaireCodesMap) {
        return false;
    }

}
