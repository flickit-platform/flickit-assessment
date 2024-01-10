package org.flickit.assessment.kit.application.service.assessmentkit.create.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.BaseDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.create.CreateKitPersisterContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitId) {
        List<QuestionnaireDslModel> dslQuestionnaires = dslKit.getQuestionnaires();

        Map<String, QuestionnaireDslModel> dslQuestionnaireCodesMap = dslQuestionnaires.stream().collect(Collectors.toMap(BaseDslModel::getCode, i -> i));

        List<Questionnaire> finalQuestionnaires = new ArrayList<>();

        dslQuestionnaireCodesMap.keySet().forEach(i -> finalQuestionnaires.add(createQuestionnaire(dslQuestionnaireCodesMap.get(i), kitId)));

        Map<String, Questionnaire> questionnaireCodeToIdMap = finalQuestionnaires.stream()
            .collect(Collectors.toMap(Questionnaire::getCode, i -> i));
        ctx.put(KEY_QUESTIONNAIRES, questionnaireCodeToIdMap);
        log.debug("Final questionnaires: {}", questionnaireCodeToIdMap);
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
}
