package org.flickit.assessment.core.adapter.out.calculate;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.attributematurityscore.AttributeMaturityScorePersistenceJpaAdapter;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidencePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssessmentCalculateResultPersistAdapter implements
    UpdateCalculatedResultPort,
    UpdateCalculatedConfidencePort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final AttributeValueJpaRepository attributeValueRepo;
    private final AttributeMaturityScorePersistenceJpaAdapter attributeMaturityScoreAdapter;

    @Override
    public void updateCalculatedResult(AssessmentResult assessmentResult) {
        assessmentResultRepo.updateAfterCalculate(assessmentResult.getId(),
            assessmentResult.getMaturityLevel().getId(),
            assessmentResult.getIsCalculateValid(),
            assessmentResult.getLastModificationTime(),
            assessmentResult.getLastCalculationTime());

        List<SubjectValue> subjectValues = assessmentResult.getSubjectValues();
        subjectValues.forEach(s -> subjectValueRepo.updateMaturityLevelById(s.getId(), s.getMaturityLevel().getId()));

        subjectValues.stream()
            .flatMap(x -> x.getAttributeValues().stream())
            .forEach(qav -> {
                attributeValueRepo.updateMaturityLevelById(qav.getId(), qav.getMaturityLevel().getId());
                qav.getMaturityScores().forEach(maturityScore ->
                    attributeMaturityScoreAdapter.saveOrUpdate(qav.getId(), maturityScore)
                );
            });
    }

    @Override
    public void updateCalculatedConfidence(AssessmentResult assessmentResult) {
        assessmentResultRepo.updateAfterCalculateConfidence(
            assessmentResult.getId(),
            assessmentResult.getConfidenceValue(),
            assessmentResult.getIsConfidenceValid(),
            assessmentResult.getLastModificationTime(),
            assessmentResult.getLastConfidenceCalculationTime());

        List<SubjectValue> subjectValues = assessmentResult.getSubjectValues();
        subjectValues.forEach(s -> subjectValueRepo.updateConfidenceValueById(s.getId(), s.getConfidenceValue()));

        subjectValues.stream()
            .flatMap(x -> x.getAttributeValues().stream())
            .forEach(qav -> attributeValueRepo.updateConfidenceValueById(qav.getId(), qav.getConfidenceValue()));
    }
}
