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
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_QUESTIONNAIRES;
import static org.flickit.assessment.kit.util.GenerateCodeUtil.*;

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
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx,
                                            AssessmentKit savedKit,
                                            AssessmentKitDslModel dslKit,
                                            UUID currentUserId) {
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

        newQuestionnairesCodes.forEach(i ->
            finalQuestionnaires.add(createQuestionnaire(dslQuestionnaireCodesMap.get(i),
                savedKit.getActiveVersionId(),
                currentUserId)));
        sameQuestionnairesCodes.forEach(i ->
            finalQuestionnaires.add(updateQuestionnaire(savedKit.getActiveVersionId(),
                savedQuestionnaireCodesMap.get(i),
                dslQuestionnaireCodesMap.get(i),
                currentUserId)));

        Map<String, Long> questionnaireCodeToIdMap = finalQuestionnaires.stream().collect(Collectors.toMap(Questionnaire::getCode, Questionnaire::getId));
        ctx.put(KEY_QUESTIONNAIRES, questionnaireCodeToIdMap);
        log.debug("Final questionnaires: {}", questionnaireCodeToIdMap);

        return new UpdateKitPersisterResult(!newQuestionnairesCodes.isEmpty());
    }

    private Questionnaire createQuestionnaire(QuestionnaireDslModel newQuestionnaire, long kitVersionId, UUID currentUserId) {
        var createParam = new Questionnaire(
            null,
            newQuestionnaire.getCode(),
            newQuestionnaire.getTitle(),
            newQuestionnaire.getIndex(),
            newQuestionnaire.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        long persistedId = createQuestionnairePort.persist(createParam, kitVersionId, currentUserId);
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

    private Questionnaire updateQuestionnaire(long kitVersionId,
                                              Questionnaire savedQuestionnaire,
                                              QuestionnaireDslModel dslQuestionnaire,
                                              UUID currentUserId) {
        if (!savedQuestionnaire.getTitle().equals(dslQuestionnaire.getTitle()) ||
            !savedQuestionnaire.getDescription().equals(dslQuestionnaire.getDescription()) ||
            savedQuestionnaire.getIndex() != dslQuestionnaire.getIndex()) {
            var updateParam = new UpdateQuestionnairePort.Param(
                savedQuestionnaire.getId(),
                kitVersionId,
                dslQuestionnaire.getTitle(),
                generateCode(dslQuestionnaire.getTitle()),
                dslQuestionnaire.getIndex(),
                dslQuestionnaire.getDescription(),
                LocalDateTime.now(),
                currentUserId);

            updateQuestionnairePort.update(updateParam);
            log.debug("Questionnaire[id={}, code={}] updated.", savedQuestionnaire.getId(), savedQuestionnaire.getCode());

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
