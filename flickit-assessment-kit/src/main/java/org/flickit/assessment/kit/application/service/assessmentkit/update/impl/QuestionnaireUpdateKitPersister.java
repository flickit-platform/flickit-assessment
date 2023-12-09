package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.port.out.questionnaire.BatchUpdateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_QUESTIONNAIRES;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionnaireUpdateKitPersister implements UpdateKitPersister {

    private final CreateQuestionnairePort createQuestionnairePort;
    private final BatchUpdateQuestionnairePort batchUpdateQuestionnairePort;

    @Override
    public int order() {
        return 3;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx, AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
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
        List<Questionnaire> mustBeUpdatedQuestionnaires = new ArrayList<>();

        newQuestionnairesCodes.forEach(i -> finalQuestionnaires.add(createQuestionnaire(dslQuestionnaireCodesMap.get(i), savedKit.getId())));
        sameQuestionnairesCodes.forEach(i -> mustBeUpdatedQuestionnaires.add(updateQuestionnaire(savedQuestionnaireCodesMap.get(i), dslQuestionnaireCodesMap.get(i))));

        batchUpdateQuestionnairePort.batchUpdate(mustBeUpdatedQuestionnaires, savedKit.getId());
        mustBeUpdatedQuestionnaires.forEach(i -> log.debug("Questionnaire[id={}, code={}] updated.", i.getId(), i.getCode()));

        finalQuestionnaires.addAll(mustBeUpdatedQuestionnaires);

        Map<String, Long> questionnaireCodeToIdMap = finalQuestionnaires.stream().collect(Collectors.toMap(Questionnaire::getCode, Questionnaire::getId));
        ctx.put(KEY_QUESTIONNAIRES, questionnaireCodeToIdMap);
        log.debug("Final questionnaires: {}", questionnaireCodeToIdMap);

        return new UpdateKitPersisterResult(!newQuestionnairesCodes.isEmpty());
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

        long persistedId = createQuestionnairePort.persist(createParam, kitId);
        log.debug("Questionnaire[id={}, code={}] created.", persistedId, newQuestionnaire.getCode());

        return new Questionnaire(
            persistedId,
            createParam.getCode(),
            createParam.getTitle(),
            createParam.getIndex(),
            createParam.getDescription(),
            createParam.getCreationTime(),
            createParam.getLastModificationTime()
        );
    }

    private Questionnaire updateQuestionnaire(Questionnaire savedQuestionnaire, QuestionnaireDslModel dslQuestionnaire) {
        if (!savedQuestionnaire.getTitle().equals(dslQuestionnaire.getTitle()) ||
            !savedQuestionnaire.getDescription().equals(dslQuestionnaire.getDescription()) ||
            savedQuestionnaire.getIndex() != dslQuestionnaire.getIndex()) {

            return new Questionnaire(
                savedQuestionnaire.getId(),
                savedQuestionnaire.getCode(),
                dslQuestionnaire.getTitle(),
                dslQuestionnaire.getIndex(),
                dslQuestionnaire.getDescription(),
                savedQuestionnaire.getCreationTime(),
                LocalDateTime.now()
            );
        }
        return savedQuestionnaire;
    }
}
