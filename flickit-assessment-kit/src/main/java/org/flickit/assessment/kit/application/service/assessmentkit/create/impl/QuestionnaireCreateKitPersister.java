package org.flickit.assessment.kit.application.service.assessmentkit.create.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterContext.KEY_QUESTIONNAIRES;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionnaireCreateKitPersister implements CreateKitPersister {

    private final CreateQuestionnairePort createQuestionnairePort;

    @Override
    public int order() {
        return 3;
    }

    @Override
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId, UUID currentUserId) {
        List<QuestionnaireDslModel> dslQuestionnaires = dslKit.getQuestionnaires();

        Map<String, Long> questionnaireCodeToIdMap = new HashMap<>();
        dslQuestionnaires.forEach(q -> {
            Long persistedQuestionnaireId = createQuestionnaire(q, kitId, currentUserId);
            questionnaireCodeToIdMap.put(q.getCode(), persistedQuestionnaireId);
        });

        ctx.put(KEY_QUESTIONNAIRES, questionnaireCodeToIdMap);
        log.debug("Final questionnaires: {}", questionnaireCodeToIdMap);
    }

    private Long createQuestionnaire(QuestionnaireDslModel newQuestionnaire, long kitId, UUID currentUserId) {
        var createParam = new Questionnaire(
            null,
            newQuestionnaire.getCode(),
            newQuestionnaire.getTitle(),
            newQuestionnaire.getIndex(),
            newQuestionnaire.getDescription(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        long persistedId = createQuestionnairePort.persist(createParam, kitId, currentUserId);
        log.debug("Questionnaire[id={}, code={}] created.", persistedId, newQuestionnaire.getCode());

        return persistedId;
    }
}
