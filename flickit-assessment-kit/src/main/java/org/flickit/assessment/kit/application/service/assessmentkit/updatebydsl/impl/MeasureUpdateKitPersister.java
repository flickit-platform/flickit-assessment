package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.port.out.measure.CreateMeasurePort;
import org.flickit.assessment.kit.application.port.out.measure.UpdateMeasurePort;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_MEASURE;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeasureUpdateKitPersister implements UpdateKitPersister {

    private final CreateMeasurePort createMeasurePort;
    private final UpdateMeasurePort updateMeasurePort;

    @Override
    public int order() {
        return 4;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public UpdateKitPersisterResult persist(UpdateKitPersisterContext ctx,
                                            AssessmentKit savedKit,
                                            AssessmentKitDslModel dslKit,
                                            UUID currentUserId) {
        List<Measure> savedMeasures = savedKit.getMeasures();
        List<QuestionnaireDslModel> dslQuestionnaires = dslKit.getQuestionnaires();

        Map<String, Measure> savedMeasureCodesMap = savedMeasures.stream().collect(Collectors.toMap(Measure::getCode, i -> i));
        Map<String, QuestionnaireDslModel> dslQuestionnaireCodesMap = dslQuestionnaires.stream().collect(Collectors.toMap(BaseDslModel::getCode, i -> i));

        List<String> newQuestionnairesCodes = dslQuestionnaireCodesMap.keySet().stream()
            .filter(i -> !savedMeasureCodesMap.containsKey(i))
            .toList();
        List<String> sameMeasuresCodes = savedMeasureCodesMap.keySet().stream()
            .filter(dslQuestionnaireCodesMap::containsKey)
            .toList();

        List<Measure> finalMeasures = new ArrayList<>();

        newQuestionnairesCodes.forEach(i ->
            finalMeasures.add(createMeasureFromQuestionnaire(dslQuestionnaireCodesMap.get(i),
                savedKit.getActiveVersionId(),
                currentUserId)));
        sameMeasuresCodes.forEach(i ->
            finalMeasures.add(updateMeasure(savedKit.getActiveVersionId(),
                savedMeasureCodesMap.get(i),
                dslQuestionnaireCodesMap.get(i),
                currentUserId)));

        Map<String, Long> measureCodeToIdMap = finalMeasures.stream().collect(Collectors.toMap(Measure::getCode, Measure::getId));
        ctx.put(KEY_MEASURE, measureCodeToIdMap);
        log.debug("Final measures: {}", measureCodeToIdMap);

        return new UpdateKitPersisterResult(!newQuestionnairesCodes.isEmpty());
    }

    private Measure createMeasureFromQuestionnaire(QuestionnaireDslModel newQuestionnaire, long kitVersionId, UUID currentUserId) {
        var createParam = new Measure(
            null,
            newQuestionnaire.getCode(),
            newQuestionnaire.getTitle(),
            newQuestionnaire.getIndex(),
            newQuestionnaire.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        long persistedId = createMeasurePort.persist(createParam, kitVersionId, currentUserId);
        log.debug("Measure[id={}, code={}] created.", persistedId, newQuestionnaire.getCode());

        return new Measure(
            persistedId,
            createParam.getCode(),
            createParam.getTitle(),
            createParam.getIndex(),
            createParam.getDescription(),
            createParam.getCreationTime(),
            createParam.getLastModificationTime()
        );
    }

    private Measure updateMeasure(long kitVersionId,
                                  Measure savedMeasure,
                                  QuestionnaireDslModel dslQuestionnaire,
                                  UUID currentUserId) {
        if (!savedMeasure.getTitle().equals(dslQuestionnaire.getTitle()) ||
            !savedMeasure.getDescription().equals(dslQuestionnaire.getDescription()) ||
            savedMeasure.getIndex() != dslQuestionnaire.getIndex()) {
            var updateParam = new UpdateMeasurePort.Param(
                savedMeasure.getId(),
                kitVersionId,
                dslQuestionnaire.getTitle(),
                dslQuestionnaire.getCode(),
                dslQuestionnaire.getIndex(),
                dslQuestionnaire.getDescription(),
                LocalDateTime.now(),
                currentUserId);

            updateMeasurePort.update(updateParam);
            log.debug("Measure[id={}, code={}] updated.", savedMeasure.getId(), savedMeasure.getCode());

            return new Measure(updateParam.id(),
                savedMeasure.getCode(),
                updateParam.title(),
                updateParam.index(),
                updateParam.description(),
                savedMeasure.getCreationTime(),
                updateParam.lastModificationTime()
            );
        }
        return savedMeasure;
    }
}
