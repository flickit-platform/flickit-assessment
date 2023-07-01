package org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubjectValuePersistenceJpaAdaptor implements CreateSubjectValuePort {

    private final SubjectValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

    @Override
    public void persistAllWithAssessmentResultId(List<Param> params, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId).get();

        List<SubjectValueJpaEntity> entities = params.stream().map(param -> {
            SubjectValueJpaEntity assessmentSubjectValue = SubjectValueMapper.mapToJpaEntity(param);
            assessmentSubjectValue.setAssessmentResult(assessmentResult);
            return assessmentSubjectValue;
        }).toList();

        repository.saveAll(entities);
    }
}
