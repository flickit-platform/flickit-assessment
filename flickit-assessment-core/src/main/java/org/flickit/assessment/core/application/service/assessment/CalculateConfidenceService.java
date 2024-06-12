package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadConfidenceLevelCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidencePort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CALCULATE_CONFIDENCE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CalculateConfidenceService implements CalculateConfidenceUseCase {

    private final LoadConfidenceLevelCalculateInfoPort loadConfidenceLevelCalculateInfoPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final UpdateCalculatedConfidencePort updateCalculatedConfidenceLevelResultPort;
    private final UpdateAssessmentPort updateAssessmentPort;
    private final LoadKitLastMajorModificationTimePort loadKitLastMajorModificationTimePort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final CreateSubjectValuePort createSubjectValuePort;
    private final CreateAttributeValuePort createAttributeValuePort;

    @Override
    public Result calculate(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_CONFIDENCE))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        AssessmentResult assessmentResult = loadConfidenceLevelCalculateInfoPort.load(param.getAssessmentId());

        if (isAssessmentResultReinitializationRequired(assessmentResult))
            reinitializeAssessmentResult(assessmentResult);

        Double confidenceValue = assessmentResult.calculateConfidenceValue();

        assessmentResult.setConfidenceValue(confidenceValue);
        assessmentResult.setIsConfidenceValid(Boolean.TRUE);
        assessmentResult.setLastModificationTime(LocalDateTime.now());
        assessmentResult.setLastConfidenceCalculationTime(LocalDateTime.now());

        updateCalculatedConfidenceLevelResultPort.updateCalculatedConfidence(assessmentResult);

        updateAssessmentPort.updateLastModificationTime(param.getAssessmentId(), assessmentResult.getLastModificationTime());

        return new Result(confidenceValue);
    }

    private boolean isAssessmentResultReinitializationRequired(AssessmentResult assessmentResult) {
        Long kitId = assessmentResult.getAssessment().getAssessmentKit().getId();
        LocalDateTime kitLastMajorModificationTime = loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(kitId);
        return assessmentResult.getLastConfidenceCalculationTime().isBefore(kitLastMajorModificationTime);
    }

    private void reinitializeAssessmentResult(AssessmentResult assessmentResult) {
        var allSubjects = loadSubjectsPort.loadByKitVersionIdWithAttributes(assessmentResult.getKitVersionId());

        List<SubjectValue> newSubjectValues = createNewSubjectValues(allSubjects, assessmentResult.getSubjectValues(), assessmentResult.getId());
        List<SubjectValue> allSubjectValues = new ArrayList<>(assessmentResult.getSubjectValues());
        allSubjectValues.addAll(newSubjectValues);

        Map<UUID, SubjectValue> idToSubjectValue = allSubjectValues.stream().collect(Collectors.toMap(SubjectValue::getId, a -> a));

        Map<UUID, List<AttributeValue>> subjectValueIdToAttrValues = createNewAttributeValues(allSubjects,
            allSubjectValues, assessmentResult.getId());

        subjectValueIdToAttrValues.forEach((svId, newAttValues) -> {
            SubjectValue subjectValue = idToSubjectValue.get(svId);
            List<AttributeValue> attrValues = new ArrayList<>(subjectValue.getAttributeValues());
            attrValues.addAll(newAttValues);
            subjectValue.setAttributeValues(attrValues);
        });

        assessmentResult.setSubjectValues(allSubjectValues);
    }

    private List<SubjectValue> createNewSubjectValues(List<Subject> kitSubjects, List<SubjectValue> subjectValues, UUID assessmentResultId) {
        var subjectIdsWithValue = subjectValues.stream()
            .map(s -> s.getSubject().getId())
            .collect(Collectors.toSet());

        var newSubjectIds = kitSubjects.stream()
            .map(Subject::getId)
            .filter(s -> !subjectIdsWithValue.contains(s))
            .toList();

        return createSubjectValuePort.persistAll(newSubjectIds, assessmentResultId);
    }

    private Map<UUID, List<AttributeValue>> createNewAttributeValues(List<Subject> kitSubjects, List<SubjectValue> subjectValues, UUID assessmentResultId) {
        List<Attribute> kitAttributes = kitSubjects.stream()
            .flatMap(s -> s.getAttributes().stream())
            .toList();

        List<AttributeValue> attributeValues = subjectValues.stream()
            .filter(s -> s.getAttributeValues() != null)
            .flatMap(s -> s.getAttributeValues().stream())
            .toList();

        var attributesWithValue = attributeValues.stream()
            .map(q -> q.getAttribute().getId())
            .collect(Collectors.toSet());

        var newAttributeIds = kitAttributes.stream()
            .map(Attribute::getId)
            .filter(a -> !attributesWithValue.contains(a))
            .toList();

        Map<Long, Long> attributeIdToSubjectId = new HashMap<>();
        for (Subject subject : kitSubjects) {
            for (Attribute attribute : subject.getAttributes())
                attributeIdToSubjectId.put(attribute.getId(), subject.getId());
        }

        List<AttributeValue> newAttributeValues = createAttributeValuePort.persistAll(newAttributeIds, assessmentResultId);
        Map<Long, SubjectValue> subjectIdToSubjectValue = subjectValues.stream().collect(Collectors.toMap(a -> a.getSubject().getId(), a -> a));

        Map<UUID, List<AttributeValue>> results = new HashMap<>();
        newAttributeValues.forEach(attrValue -> {
            Long subjId = attributeIdToSubjectId.get(attrValue.getAttribute().getId());
            SubjectValue subjectValue = subjectIdToSubjectValue.get(subjId);
            results.compute(subjectValue.getId(), (subjValueId, attrValues) -> {
                List<AttributeValue> list = attrValues != null ? attrValues : new ArrayList<>();
                list.add(attrValue);
                return list;
            });
        });

        return results;
    }
}
