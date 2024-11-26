package org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_QUESTIONNAIRES;
import static org.flickit.assessment.kit.application.service.assessmentkit.createbydsl.CreateKitPersisterContext.KEY_SUBJECTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectQuestionnaireCreateKitPersister implements CreateKitPersister {

    private final CreateSubjectQuestionnairePort createSubjectQuestionnairePort;

    @Override
    public int order() {
        return 6;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void persist(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit, Long kitVersionId, UUID currentUserId) {
        var questionnaireIdToSubjectIdsMap = extractQuestionnaireIdToSubjectIdMap(ctx, dslKit);
        createSubjectQuestionnairePort.persistAll(questionnaireIdToSubjectIdsMap, kitVersionId);
        log.debug("{} SubjectQuestionnaires created.", questionnaireIdToSubjectIdsMap.size());
    }

    private Map<Long, Set<Long>> extractQuestionnaireIdToSubjectIdMap(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit) {
        var attributeCodeToSubjectIdMap = attributeCodeToSubjectIdMap(ctx, dslKit);
        Map<String, Long> questionnaireCodeToIdMap = ctx.get(KEY_QUESTIONNAIRES);

        Map<Long, Set<Long>> resultMap = new HashMap<>();
        for (QuestionDslModel q : dslKit.getQuestions()) {
            Long questionnaireId = questionnaireCodeToIdMap.get(q.getQuestionnaireCode());
            Set<Long> subjectIds = q.getQuestionImpacts().stream()
                .map(QuestionImpactDslModel::getAttributeCode)
                .map(attributeCodeToSubjectIdMap::get)
                .collect(Collectors.toSet());
            resultMap.computeIfAbsent(questionnaireId, key -> new HashSet<>());
            resultMap.get(questionnaireId).addAll(subjectIds);
        }
        return resultMap;
    }

    private Map<String, Long> attributeCodeToSubjectIdMap(CreateKitPersisterContext ctx, AssessmentKitDslModel dslKit) {
        Map<String, Long> subjects = ctx.get(KEY_SUBJECTS);
        List<AttributeDslModel> dslAttributes = dslKit.getAttributes();
        return dslAttributes.stream().collect(toMap(AttributeDslModel::getCode, a -> subjects.get(a.getSubjectCode())));
    }
}
