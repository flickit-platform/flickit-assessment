package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.CalculateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CALCULATE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CALCULATE_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CalculateAssessmentService implements CalculateAssessmentUseCase {

    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadCalculateInfoPort loadCalculateInfoPort;
    private final UpdateCalculatedResultPort updateCalculatedResultPort;
    private final UpdateAssessmentPort updateAssessmentPort;
    private final LoadKitLastMajorModificationTimePort loadKitLastMajorModificationTimePort;
    private final LoadSubjectsPort loadSubjectsPort;
    private final CreateSubjectValuePort createSubjectValuePort;
    private final CreateAttributeValuePort createAttributeValuePort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public Result calculateMaturityLevel(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        reinitializeAssessmentResultIfRequired(param.getAssessmentId());

        AssessmentResult assessmentResult = loadCalculateInfoPort.load(param.getAssessmentId());

        if (isCalculationValid(assessmentResult)) {
            return new Result(assessmentResult.getMaturityLevel(), false);
        } else {
            MaturityLevel calcResult = calculate(assessmentResult);
            updateCalculatedResultPort.updateCalculatedResult(assessmentResult);
            updateAssessmentPort.updateLastModificationTime(param.getAssessmentId(), assessmentResult.getLastModificationTime());
            return new Result(calcResult, true);
        }
    }

    boolean isCalculationValid(AssessmentResult assessmentResult) {
        var kitId = assessmentResult.getAssessment().getAssessmentKit().getId();
        var kitLastMajorModificationTime = loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(kitId);

        var calculationTime = assessmentResult.getLastCalculationTime();
        boolean isCalculationTimeValid = calculationTime != null && calculationTime.isAfter(kitLastMajorModificationTime);
        boolean isCalculationValid = Boolean.TRUE.equals(assessmentResult.getIsCalculateValid());

        return isCalculationValid && isCalculationTimeValid;
    }

    private static MaturityLevel calculate(AssessmentResult assessmentResult) {
        MaturityLevel calcResult = assessmentResult.calculate();
        assessmentResult.setMaturityLevel(calcResult);
        assessmentResult.setIsCalculateValid(Boolean.TRUE);
        assessmentResult.setLastModificationTime(LocalDateTime.now());
        assessmentResult.setLastCalculationTime(LocalDateTime.now());
        return calcResult;
    }

    private void reinitializeAssessmentResultIfRequired(UUID assessmentId) {
        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(CALCULATE_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND));
        if (isAssessmentResultReinitializationRequired(assessmentResult))
            reinitializeAssessmentResult(assessmentResult);
    }

    private boolean isAssessmentResultReinitializationRequired(AssessmentResult assessmentResult) {
        Long kitId = assessmentResult.getAssessment().getAssessmentKit().getId();
        LocalDateTime kitLastMajorModificationTime = loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(kitId);
        return assessmentResult.getLastCalculationTime() == null || assessmentResult.getLastCalculationTime().isBefore(kitLastMajorModificationTime);
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
            .filter(s -> s.getAttributes() != null)
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
            .collect(Collectors.toSet());

        Map<Long, Long> attributeIdToSubjectId = new HashMap<>();
        kitSubjects.forEach(subject -> {
            if (subject.getAttributes() != null) {
                subject.getAttributes().forEach(attribute ->
                    attributeIdToSubjectId.put(attribute.getId(), subject.getId()));
            }
        });

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
