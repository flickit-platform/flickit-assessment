package org.flickit.assessment.kit.application.service.assessmentkit.update.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.domain.dsl.AssessmentKitDslModel;
import org.flickit.assessment.kit.application.domain.dsl.QuestionImpactDslModel;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.DeleteSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.LoadSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersister;
import org.flickit.assessment.kit.application.service.assessmentkit.update.UpdateKitPersisterResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class SubjectQuestionnaireUpdateKitPersister implements UpdateKitPersister {

    private final LoadSubjectQuestionnairePort loadPort;
    private final DeleteSubjectQuestionnairePort deletePort;
    private final CreateSubjectQuestionnairePort createPort;

    @Override
    public int order() {
        return 6;
    }

    @Override
    public UpdateKitPersisterResult persist(AssessmentKit savedKit, AssessmentKitDslModel dslKit) {
        Map<String, Set<Long>> attributeCodeToSubjectIdMap = attributeCodeToSubjectIdMap(savedKit);
        Map<String, Long> questionnaireCodeToIdMap = questionnaireCodeToIdMap(savedKit);
        Map<Long, Set<Long>> questionnaireIdToSubjectIdMap = questionnaireIdToSubjectIdMap(dslKit, attributeCodeToSubjectIdMap, questionnaireCodeToIdMap);
        List<SubjectQuestionnaire> savedSubjectQuestionnaires = loadPort.loadByKitId(savedKit.getId());
        Map<Long, HashMap<Long, Long>> savedQuestionnaireIdToSubjectIdToIdMap = questionnaireIdToSubjectIdToIdMap(savedSubjectQuestionnaires);

        boolean invalidateCalcResult = updateSubjectQuestionnaires(questionnaireIdToSubjectIdMap, savedQuestionnaireIdToSubjectIdToIdMap);

        return new UpdateKitPersisterResult(invalidateCalcResult);
    }

    private Map<String, Set<Long>> attributeCodeToSubjectIdMap(AssessmentKit savedKit) {
        Map<String, Set<Long>> attributeCodeToSubjectIdMap = new HashMap<>();
        for (Subject subject : savedKit.getSubjects()) {
            long id = subject.getId();
            Set<String> attributeCodes = subject.getAttributes().stream()
                .map(Attribute::getCode)
                .collect(Collectors.toSet());
            for (String code : attributeCodes) {
                if (attributeCodeToSubjectIdMap.containsKey(code))
                    attributeCodeToSubjectIdMap.get(code).add(id);
                else {
                    attributeCodeToSubjectIdMap.put(code, new HashSet<>(List.of(id)));
                }
            }
        }
        return attributeCodeToSubjectIdMap;
    }

    private Map<String, Long> questionnaireCodeToIdMap(AssessmentKit savedKit) {
        return savedKit.getQuestionnaires().stream()
            .collect(Collectors.toMap(Questionnaire::getCode, Questionnaire::getId));
    }

    private Map<Long, Set<Long>> questionnaireIdToSubjectIdMap(AssessmentKitDslModel dslKit,
                                                               Map<String, Set<Long>> attributeCodeToSubjectIdMap,
                                                               Map<String, Long> questionnaireCodeToIdMap) {
        return dslKit.getQuestions().stream()
            .collect(Collectors.toMap(
                    q -> questionnaireCodeToIdMap.get(q.getCode()),
                    q ->
                        q.getQuestionImpacts().stream()
                            .map(QuestionImpactDslModel::getAttributeCode)
                            .map(attributeCodeToSubjectIdMap::get)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toSet())
                )
            );
    }

    private Map<Long, HashMap<Long, Long>> questionnaireIdToSubjectIdToIdMap(List<SubjectQuestionnaire> subjectQuestionnaires) {
        Map<Long, HashMap<Long, Long>> result = new HashMap<>();
        for (SubjectQuestionnaire entity : subjectQuestionnaires) {
            Long questionnaireId = entity.getQuestionnaireId();
            if (result.containsKey(questionnaireId)) {
                result.get(questionnaireId).put(entity.getSubjectId(), entity.getId());
            } else {
                HashMap<Long, Long> subjectIdToIdMap = new HashMap<>();
                subjectIdToIdMap.put(entity.getSubjectId(), entity.getId());
                result.put(questionnaireId, subjectIdToIdMap);
            }
        }
        return result;
    }

    private boolean updateSubjectQuestionnaires(Map<Long, Set<Long>> questionnaireIdToSubjectIdMap,
                                                Map<Long, HashMap<Long, Long>> savedQuestionnaireIdToSubjectIdMap) {
        boolean invalidateCalcResult = false;
        for (Long questionnaireId : questionnaireIdToSubjectIdMap.keySet()) {
            HashMap<Long, Long> savedSubjectIdToIdMap = savedQuestionnaireIdToSubjectIdMap
                .getOrDefault(questionnaireId, new HashMap<>());

            Set<Long> savedSubjectIds = savedSubjectIdToIdMap.keySet();
            Set<Long> subjectIds = questionnaireIdToSubjectIdMap.get(questionnaireId);

            Set<Long> deletedSubjectIds = savedSubjectIds.stream().filter(id -> !subjectIds.contains(id))
                .collect(Collectors.toSet());
            Set<Long> addedSubjectIds = subjectIds.stream().filter(id -> !savedSubjectIds.contains(id))
                .collect(Collectors.toSet());

            invalidateCalcResult = invalidateCalcResult || !deletedSubjectIds.isEmpty() || !addedSubjectIds.isEmpty();

            for (Long subjectId : deletedSubjectIds) {
                deletePort.delete(savedSubjectIdToIdMap.get(subjectId));
            }
            for (Long subjectId : addedSubjectIds) {
                createPort.persist(toCreateParam(questionnaireId, subjectId));
            }
        }
        return invalidateCalcResult;
    }

    private CreateSubjectQuestionnairePort.Param toCreateParam(Long questionnaireId, Long subjectId) {
        return new CreateSubjectQuestionnairePort.Param(subjectId, questionnaireId);
    }
}
