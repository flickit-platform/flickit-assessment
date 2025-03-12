package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.Measure;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.port.out.measure.CreateMeasurePort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_MEASURE;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeasureCreateKitPersister implements CreateKitPersister {

    private final CreateMeasurePort createMeasurePort;

    @Override
    public int order() {
        return 4;
    }

    @Override
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitVersionId, UUID currentUserId) {
        List<QuestionnaireDslModel> dslQuestionnaires = dslKit.getQuestionnaires();

        Map<String, Long> measureCodeToIdMap = new HashMap<>();
        dslQuestionnaires.forEach(q -> {
            Long persistedMeasureId = createMeasure(q, kitVersionId, currentUserId);
            measureCodeToIdMap.put(q.getCode(), persistedMeasureId);
        });

        ctx.put(KEY_MEASURE, measureCodeToIdMap);
        log.debug("Final measures: {}", measureCodeToIdMap);
    }

    private Long createMeasure(QuestionnaireDslModel newQuestionnaire, long kitVersionId, UUID currentUserId) {
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

        return persistedId;
    }
}
