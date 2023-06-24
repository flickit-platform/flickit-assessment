package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentsubjectvalue;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubjectvalue.CreateAssessmentSubjectValuePort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentSubjectValuePersistenceJpaAdaptor implements CreateAssessmentSubjectValuePort {

    private final AssessmentSubjectValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public void persistAllWithAssessmentResultId(List<Param> params, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId).get();

        List<AssessmentSubjectValueJpaEntity> entities = params.stream().map(param -> {
            AssessmentSubjectValueJpaEntity assessmentSubjectValue = AssessmentSubjectValueMapper.mapToJpaEntity(param);
            assessmentSubjectValue.setAssessmentResult(assessmentResult);
            return assessmentSubjectValue;
        }).toList();

        repository.saveAll(entities);
    }
}
