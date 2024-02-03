package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidencePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadConfidenceLevelCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitLastEffectiveModificationTimePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional
@RequiredArgsConstructor
public class CalculateConfidenceService implements CalculateConfidenceUseCase {

    private final LoadConfidenceLevelCalculateInfoPort loadConfidenceLevelCalculateInfoPort;
    private final UpdateCalculatedConfidencePort updateCalculatedConfidenceLevelResultPort;
    private final UpdateAssessmentPort updateAssessmentPort;
    private final LoadKitLastEffectiveModificationTimePort loadKitLastEffectiveModificationTimePort;
    private final LoadSubjectPort loadSubjectPort;
    private final CreateSubjectValuePort createSubjectValuePort;
    private final CreateQualityAttributeValuePort createQualityAttributeValuePort;

    @Override
    public Result calculate(Param param) {
        AssessmentResult assessmentResult = loadConfidenceLevelCalculateInfoPort.load(param.getAssessmentId());

        initializeBeforeConfidenceCalculationBasedOnKitChanges(assessmentResult);

        Double confidenceValue = assessmentResult.calculateConfidenceValue();

        assessmentResult.setConfidenceValue(confidenceValue);
        assessmentResult.setConfidenceValid(Boolean.TRUE);
        assessmentResult.setLastModificationTime(LocalDateTime.now());
        assessmentResult.setLastConfidenceCalculationTime(LocalDateTime.now());

        updateCalculatedConfidenceLevelResultPort.updateCalculatedConfidence(assessmentResult);

        updateAssessmentPort.updateLastModificationTime(param.getAssessmentId(), assessmentResult.getLastModificationTime());

        return new Result(confidenceValue);
    }

    private void initializeBeforeConfidenceCalculationBasedOnKitChanges(AssessmentResult assessmentResult) {
        Long kitId = assessmentResult.getAssessment().getAssessmentKit().getId();
        LocalDateTime kitLastEffectiveModificationTime = loadKitLastEffectiveModificationTimePort.load(kitId);
        if (assessmentResult.getLastConfidenceCalculationTime().isBefore(kitLastEffectiveModificationTime)) {
            var allSubjects = loadSubjectPort.loadByKitIdWithAttributes(kitId);
            Map<Long, List<QualityAttributeValue>> subjectIdToAttributeValueMap = createCalculateConfidenceNewAttributeValues(allSubjects, assessmentResult);
            createCalculateConfidenceNewSubjectValues(allSubjects, assessmentResult, subjectIdToAttributeValueMap);
        }
    }

    private Map<Long, List<QualityAttributeValue>> createCalculateConfidenceNewAttributeValues(List<Subject> allSubjects, AssessmentResult assessmentResult) {
        var attributesWithValue = assessmentResult.getSubjectValues().stream()
            .flatMap(s -> s.getQualityAttributeValues().stream())
            .distinct()
            .map(q -> q.getQualityAttribute().getId())
            .collect(Collectors.toSet());

        var allAttributes = allSubjects.stream()
            .flatMap(s -> s.getQualityAttributes().stream())
            .distinct()
            .toList();

        var newAttributeIds = allAttributes.stream()
            .map(QualityAttribute::getId)
            .filter(a -> !attributesWithValue.contains(a))
            .toList();

        var newAttributeValues = createQualityAttributeValuePort.persistAll(newAttributeIds, assessmentResult.getId());

        assessmentResult.getSubjectValues().forEach(s -> {
            List<QualityAttributeValue> newAttributeValueList = new ArrayList<>(s.getQualityAttributeValues());
            newAttributeValueList.addAll(newAttributeValues);
            s.setQualityAttributeValues(newAttributeValueList);
        });

        var attributeIdToAttributeValueMap = newAttributeValues.stream()
            .collect(groupingBy(qav -> qav.getQualityAttribute().getId()));

        return allSubjects.stream()
            .collect(toMap(Subject::getId,
                s -> s.getQualityAttributes().stream()
                    .filter(q -> !attributesWithValue.contains(q.getId()))
                    .flatMap(qa -> attributeIdToAttributeValueMap.get(qa.getId()).stream()).toList()));
    }

    private void createCalculateConfidenceNewSubjectValues(List<Subject> subjects, AssessmentResult assessmentResult, Map<Long, List<QualityAttributeValue>> subjectIdToAttributeValueMap) {
        var subjectsWithValue = assessmentResult.getSubjectValues().stream()
            .map(s -> s.getSubject().getId())
            .collect(Collectors.toSet());

        var newSubjectIds = subjects.stream()
            .map(Subject::getId)
            .filter(s -> !subjectsWithValue.contains(s))
            .toList();

        var newSubjectValues = createSubjectValuePort.persistAll(newSubjectIds, assessmentResult.getId());

        newSubjectValues.forEach(sv -> {
            List<QualityAttributeValue> newAttributeValueList = new ArrayList<>(sv.getQualityAttributeValues());
            newAttributeValueList.addAll(subjectIdToAttributeValueMap.get(sv.getSubject().getId()));
            sv.setQualityAttributeValues(newAttributeValueList);
        });

        List<SubjectValue> newSubjectValueList = new ArrayList<>(assessmentResult.getSubjectValues());
        newSubjectValueList.addAll(newSubjectValues);
        assessmentResult.setSubjectValues(newSubjectValueList);
    }
}
