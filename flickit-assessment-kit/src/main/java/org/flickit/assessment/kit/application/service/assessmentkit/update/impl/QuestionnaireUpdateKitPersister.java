package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionnaireUpdateKitPersister implements UpdateKitPersister {

    private final CreateQuestionnairePort createQuestionnairePort;
    private final UpdateQuestionnairePort updateQuestionnairePort;


    @Override
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        List<Questionnaire> savedQuestionnaires = savedKit.getQuestionnaires();
        List<QuestionnaireDslModel> dslQuestionnaires = dslKit.getQuestionnaires();

        Map<String, Questionnaire> savedQuestionnaireCodesMap = savedQuestionnaires.stream().collect(Collectors.toMap(Questionnaire::getCode, i -> i));
        Map<String, QuestionnaireDslModel> newDslQuestionnaireCodesMap = dslQuestionnaires.stream().collect(Collectors.toMap(BaseDslModel::getCode, i -> i));

        List<String> newQuestionnaires = newCodesInNewDsl(savedQuestionnaireCodesMap.keySet(), newDslQuestionnaireCodesMap.keySet());
        List<String> sameQuestionnaires = sameCodesInNewDsl(savedQuestionnaireCodesMap.keySet(), newDslQuestionnaireCodesMap.keySet());

        newQuestionnaires.forEach(i -> createQuestionnaire(newDslQuestionnaireCodesMap.get(i), savedKit.getId()));
        sameQuestionnaires.forEach(i -> updateQuestionnaire(savedQuestionnaireCodesMap.get(i), newDslQuestionnaireCodesMap.get(i)));

        return new UpdateKitPersisterResult(!newQuestionnaires.isEmpty());
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

    private void createQuestionnaire(QuestionnaireDslModel newQuestionnaire, long kitId) {
        var createParam = new Questionnaire(
            null,
            newQuestionnaire.getCode(),
            newQuestionnaire.getTitle(),
            newQuestionnaire.getIndex(),
            newQuestionnaire.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        createQuestionnairePort.persist(createParam, kitId);
        log.debug("A questionnaire with code [{}] is created.", newQuestionnaire.getCode());
    }

    private void updateQuestionnaire(Questionnaire savedQuestionnaire, QuestionnaireDslModel newQuestionnaire) {
        if (!savedQuestionnaire.getTitle().equals(newQuestionnaire.getTitle()) ||
            !savedQuestionnaire.getDescription().equals(newQuestionnaire.getDescription()) ||
            savedQuestionnaire.getIndex() != newQuestionnaire.getIndex()) {
            var updateParam = new UpdateQuestionnairePort.Param(
                savedQuestionnaire.getId(),
                newQuestionnaire.getTitle(),
                newQuestionnaire.getIndex(),
                newQuestionnaire.getDescription());

            updateQuestionnairePort.update(updateParam);
            log.debug("A questionnaire with code [{}] is updated.", savedQuestionnaire.getCode());
        }
    }
}
