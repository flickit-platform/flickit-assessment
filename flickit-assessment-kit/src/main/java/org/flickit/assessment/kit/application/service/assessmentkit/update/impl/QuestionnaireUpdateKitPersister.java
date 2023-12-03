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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionnaireUpdateKitPersister implements UpdateKitPersister {

    private final CreateQuestionnairePort createQuestionnairePort;
    private final UpdateQuestionnairePort updateQuestionnairePort;

    @Override
    public int order() {
        return 3;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        List<Questionnaire> savedQuestionnaires = savedKit.getQuestionnaires();
        List<QuestionnaireDslModel> dslQuestionnaires = dslKit.getQuestionnaires();

        Map<String, Questionnaire> savedQuestionnaireCodesMap = savedQuestionnaires.stream().collect(Collectors.toMap(Questionnaire::getCode, i -> i));
        Map<String, QuestionnaireDslModel> dslQuestionnaireCodesMap = dslQuestionnaires.stream().collect(Collectors.toMap(BaseDslModel::getCode, i -> i));

        List<String> newQuestionnairesCodes = dslQuestionnaireCodesMap.keySet().stream()
            .filter(i -> !savedQuestionnaireCodesMap.containsKey(i))
            .toList();
        List<String> sameQuestionnairesCodes = savedQuestionnaireCodesMap.keySet().stream()
            .filter(dslQuestionnaireCodesMap::containsKey)
            .toList();

        List<Questionnaire> finalQuestionnaires = new ArrayList<>();

        newQuestionnairesCodes.forEach(i -> finalQuestionnaires.add(createQuestionnaire(dslQuestionnaireCodesMap.get(i), savedKit.getId())));
        sameQuestionnairesCodes.forEach(i -> finalQuestionnaires.add(updateQuestionnaire(savedQuestionnaireCodesMap.get(i), dslQuestionnaireCodesMap.get(i))));

        AssessmentKit updatedKit = savedKit.toBuilder()
            .questionnaires(finalQuestionnaires)
            .build();
        return new UpdateKitPersisterResult(updatedKit, !newQuestionnairesCodes.isEmpty());
    }

    private Questionnaire createQuestionnaire(QuestionnaireDslModel newQuestionnaire, long kitId) {
        var createParam = new Questionnaire(
            null,
            newQuestionnaire.getCode(),
            newQuestionnaire.getTitle(),
            newQuestionnaire.getIndex(),
            newQuestionnaire.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        long id = createQuestionnairePort.persist(createParam, kitId);
        log.debug("A questionnaire with code [{}] is created.", newQuestionnaire.getCode());

        return new Questionnaire(
            id,
            createParam.getCode(),
            createParam.getTitle(),
            createParam.getIndex(),
            createParam.getDescription(),
            createParam.getCreationTime(),
            createParam.getLastModificationTime()
        );
    }

    private Questionnaire updateQuestionnaire(Questionnaire savedQuestionnaire, QuestionnaireDslModel newQuestionnaire) {
        if (!savedQuestionnaire.getTitle().equals(newQuestionnaire.getTitle()) ||
            !savedQuestionnaire.getDescription().equals(newQuestionnaire.getDescription()) ||
            savedQuestionnaire.getIndex() != newQuestionnaire.getIndex()) {
            var updateParam = new UpdateQuestionnairePort.Param(
                savedQuestionnaire.getId(),
                newQuestionnaire.getTitle(),
                newQuestionnaire.getIndex(),
                newQuestionnaire.getDescription(),
                LocalDateTime.now());

            updateQuestionnairePort.update(updateParam);
            log.debug("A questionnaire with code [{}] is updated.", savedQuestionnaire.getCode());

            return new Questionnaire(updateParam.id(),
                savedQuestionnaire.getCode(),
                updateParam.title(),
                updateParam.index(),
                updateParam.description(),
                savedQuestionnaire.getCreationTime(),
                updateParam.lastModificationTime()
            );
        }
        return savedQuestionnaire;
    }
}
