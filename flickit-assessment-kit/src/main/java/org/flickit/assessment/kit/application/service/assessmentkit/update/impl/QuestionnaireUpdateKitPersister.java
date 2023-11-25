package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.port.out.assessmentresult.InvalidateAssessmentResultByKitPort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionnaireUpdateKitPersister implements UpdateKitPersister {

    private final InvalidateAssessmentResultByKitPort invalidateAssessmentResultByKitPort;

    @Override
    public void persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        List<Questionnaire> savedQuestionnaires = savedKit.getQuestionnaires();
        List<QuestionnaireDslModel> dslQuestionnaires = dslKit.getQuestionnaires();

        Map<String, Questionnaire> savedQuestionnaireCodesMap = savedQuestionnaires.stream().collect(Collectors.toMap(Questionnaire::getCode, i -> i));
        Map<String, QuestionnaireDslModel> newDslQuestionnaireCodesMap = dslQuestionnaires.stream().collect(Collectors.toMap(BaseDslModel::getCode, i -> i));

        List<String> newQuestionnaires = newCodesInNewDsl(savedQuestionnaireCodesMap.keySet(), newDslQuestionnaireCodesMap.keySet());
        List<String> sameQuestionnaires = sameCodesInNewDsl(savedQuestionnaireCodesMap.keySet(), newDslQuestionnaireCodesMap.keySet());

        newQuestionnaires.forEach(i -> createQuestionnaire(newDslQuestionnaireCodesMap.get(i), savedKit.getId()));
        sameQuestionnaires.forEach(i -> updateQuestionnaire(savedQuestionnaireCodesMap.get(i), newDslQuestionnaireCodesMap.get(i), savedKit.getId()));

        if (!newQuestionnaires.isEmpty()) {
            invalidateAssessmentResultByKitPort.invalidateByKitId(savedKit.getId());
        }
    }

    private List<String> newCodesInNewDsl(Set<String> savedItemCodes, Set<String> newItemCodes) {
        return newItemCodes.stream()
            .filter(i -> savedItemCodes.stream()
                .noneMatch(s -> s.equals(i)))
            .toList();
    }

    private List<String> sameCodesInNewDsl(Set<String> savedItemCodes, Set<String> newItemCodes) {
        return savedItemCodes.stream()
            .filter(s -> newItemCodes.stream()
                .anyMatch(i -> i.equals(s)))
            .toList();
    }

    private void createQuestionnaire(QuestionnaireDslModel newQuestionnaire, Long kitId) {

    }

    private void updateQuestionnaire(Questionnaire savedQuestionnaire, QuestionnaireDslModel newQuestionnaire, Long kitId) {

    }
}
