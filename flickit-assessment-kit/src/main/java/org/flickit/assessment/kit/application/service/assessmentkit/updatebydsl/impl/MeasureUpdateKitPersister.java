package org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.port.out.measure.CreateMeasurePort;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext;
import org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.updatebydsl.UpdateKitPersisterContext.KEY_MEASURE;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeasureUpdateKitPersister implements UpdateKitPersister {

    private final CreateMeasurePort createMeasurePort;

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
        var savedMeasures = savedKit.getMeasures();
        var dslQuestionnaires = dslKit.getQuestionnaires();

        var savedMeasureCodesMap = savedMeasures.stream()
            .collect(toMap(Measure::getCode, Function.identity()));
        var dslQuestionnaireCodesMap = dslQuestionnaires.stream()
            .collect(toMap(QuestionnaireDslModel::getCode, Function.identity()));

        var newQuestionnairesCodes = dslQuestionnaireCodesMap.keySet().stream()
            .filter(i -> !savedMeasureCodesMap.containsKey(i))
            .toList();
        var sameMeasuresCodes = savedMeasureCodesMap.keySet().stream()
            .filter(dslQuestionnaireCodesMap::containsKey)
            .toList();

        List<Measure> finalMeasures = new ArrayList<>();

        newQuestionnairesCodes.forEach(i ->
            finalMeasures.add(createMeasure(dslQuestionnaireCodesMap.get(i),
                savedKit.getActiveVersionId(),
                currentUserId)));
        sameMeasuresCodes.forEach(i -> finalMeasures.add(savedMeasureCodesMap.get(i)));

        var measureCodeToIdMap = finalMeasures.stream().collect(toMap(Measure::getCode, Measure::getId));
        ctx.put(KEY_MEASURE, measureCodeToIdMap);
        log.debug("Final measures: {}", measureCodeToIdMap);

        return new UpdateKitPersisterResult(!newQuestionnairesCodes.isEmpty());
    }

    private Measure createMeasure(QuestionnaireDslModel newQuestionnaire, long kitVersionId, UUID currentUserId) {
        var createParam = new Measure(
            null,
            newQuestionnaire.getCode(),
            newQuestionnaire.getTitle(),
            newQuestionnaire.getIndex(),
            newQuestionnaire.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        var persistedId = createMeasurePort.persist(createParam, kitVersionId, currentUserId);
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
}
