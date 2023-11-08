package org.flickit.assessment.core.adapter.out.calculate;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.attributematurityscore.AttributeMaturityScorePersistenceJpaAdapter;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidenceLevelResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.assessment.data.jpa.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.subjectvalue.SubjectValueJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssessmentCalculateResultPersistAdapter implements
    UpdateCalculatedResultPort,
    UpdateCalculatedConfidenceLevelResultPort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final QualityAttributeValueJpaRepository attributeValueRepo;
    private final AttributeMaturityScorePersistenceJpaAdapter attributeMaturityScoreAdapter;

    @Override
    public void updateCalculatedResult(AssessmentResult assessmentResult) {
        assessmentResultRepo.updateAfterCalculate(assessmentResult.getId(),
            assessmentResult.getMaturityLevel().getId(),
            assessmentResult.isCalculateValid(),
            assessmentResult.getLastModificationTime());

        List<SubjectValue> subjectValues = assessmentResult.getSubjectValues();
        subjectValues.forEach(s -> subjectValueRepo.updateMaturityLevelById(s.getId(), s.getMaturityLevel().getId()));

        subjectValues.stream()
            .flatMap(x -> x.getQualityAttributeValues().stream())
            .forEach(qav -> {
                attributeValueRepo.updateMaturityLevelById(qav.getId(), qav.getMaturityLevel().getId());
                qav.getMaturityScores().forEach(maturityScore ->
                    attributeMaturityScoreAdapter.saveOrUpdate(qav.getId(), maturityScore)
                );
            });
    }

    @Override
    public void updateCalculatedConfidenceLevelResult(AssessmentResult assessmentResult) {
        assessmentResultRepo.updateAfterCalculateConfidence(
            assessmentResult.getId(),
            assessmentResult.getConfidenceValue(),
            assessmentResult.isConfidenceValid(),
            assessmentResult.getLastModificationTime());

        List<SubjectValue> subjectValues = assessmentResult.getSubjectValues();
        subjectValues.forEach(s -> subjectValueRepo.updateConfidenceLevelById(s.getId(), s.getConfidenceValue()));

        subjectValues.stream()
            .flatMap(x -> x.getQualityAttributeValues().stream())
            .forEach(qav -> attributeValueRepo.updateConfidenceLevelById(qav.getId(), qav.getConfidenceValue()));
    }
}
