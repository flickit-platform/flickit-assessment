package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.CalculateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitLastEffectiveModificationTimePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CalculateAssessmentService implements CalculateAssessmentUseCase {

    private final LoadCalculateInfoPort loadCalculateInfoPort;
    private final UpdateCalculatedResultPort updateCalculatedResultPort;
    private final UpdateAssessmentPort updateAssessmentPort;
    private final LoadKitLastEffectiveModificationTimePort loadKitLastEffectiveModificationTimePort;
    private final LoadSubjectPort loadSubjectPort;
    private final CreateSubjectValuePort createSubjectValuePort;
    private final CreateQualityAttributeValuePort createQualityAttributeValuePort;

    @Override
    public Result calculateMaturityLevel(Param param) {
        AssessmentResult assessmentResult = loadCalculateInfoPort.load(param.getAssessmentId());

        initializeBasedOnKitChanges(assessmentResult);

        MaturityLevel calcResult = assessmentResult.calculate();
        assessmentResult.setMaturityLevel(calcResult);
        assessmentResult.setCalculateValid(true);
        assessmentResult.setLastModificationTime(LocalDateTime.now());
        assessmentResult.setLastCalculationTime(LocalDateTime.now());

        updateCalculatedResultPort.updateCalculatedResult(assessmentResult);

        updateAssessmentPort.updateLastModificationTime(param.getAssessmentId(), assessmentResult.getLastModificationTime());


        return new Result(calcResult);
    }

    private void initializeBasedOnKitChanges(AssessmentResult assessmentResult) {
        Long kitId = assessmentResult.getAssessment().getAssessmentKit().getId();
        LocalDateTime kitLastEffectiveModificationTime = loadKitLastEffectiveModificationTimePort.load(kitId);
        if (assessmentResult.getLastCalculationTime().isBefore(kitLastEffectiveModificationTime)) {

            var subjectsWithValue = assessmentResult.getSubjectValues().stream()
                .map(s -> s.getSubject().getId())
                .collect(Collectors.toSet());

            var subjects = loadSubjectPort.loadByKitIdWithAttributes(kitId);

            var subjectIdsWithNoValue = subjects.stream()
                .map(Subject::getId)
                .filter(id -> !subjectsWithValue.contains(id))
                .toList();


            var attributesWithValue = assessmentResult.getSubjectValues().stream()
                .flatMap(s -> s.getQualityAttributeValues().stream())
                .distinct()
                .map(q -> q.getQualityAttribute().getId())
                .collect(Collectors.toSet());

            var attributes = subjects.stream()
                .flatMap(s -> s.getQualityAttributes().stream())
                .distinct()
                .toList();

            var attributeIdsWithNoValue = attributes.stream()
                .map(QualityAttribute::getId)
                .filter(id -> !attributesWithValue.contains(id))
                .toList();

            List<UUID> newAttributeValueIds = createQualityAttributeValuePort.persistAll(attributeIdsWithNoValue, assessmentResult.getId());
            List<UUID> newSubjectValues = createSubjectValuePort.persistAll(subjectIdsWithNoValue, assessmentResult.getId());

            // TODO: save these values in result
        }
    }
}
