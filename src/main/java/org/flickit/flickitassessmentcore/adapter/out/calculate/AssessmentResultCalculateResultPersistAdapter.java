package org.flickit.flickitassessmentcore.adapter.out.calculate;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue.SubjectValueJpaRepository;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.UpdateCalculateResultPort;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.flickit.flickitassessmentcore.domain.SubjectValue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssessmentResultCalculateResultPersistAdapter implements UpdateCalculateResultPort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final QualityAttributeValueJpaRepository qualityAttributeValueRepo;

    @Override
    public void updateCalculatedResult(AssessmentResult assessmentResult) {
        assessmentResultRepo.updateMaturityLeveAndIsValidById(assessmentResult.getId(),
            assessmentResult.getMaturityLevel().getId(),
            assessmentResult.isValid());

        List<SubjectValue> subjectValues = assessmentResult.getSubjectValues();
        subjectValues.forEach(s -> subjectValueRepo.updateMaturityLevelById(s.getId(), s.getMaturityLevel().getId()));

        subjectValues.stream()
            .flatMap(x -> x.getQualityAttributeValues().stream())
            .forEach(q -> qualityAttributeValueRepo.updateMaturityLevelById(q.getId(), q.getMaturityLevel().getId()));
    }
}
