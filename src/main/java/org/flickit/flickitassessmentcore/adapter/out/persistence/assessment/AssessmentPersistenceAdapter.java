package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AssessmentPersistenceAdapter implements LoadAssessmentPort {

    private final AssessmentRepository assessmentRepository;

    @Override
    public Assessment loadAssessment(UUID assessmentId) {
        AssessmentJpaEntity assessmentEntity = assessmentRepository.getReferenceById(assessmentId);
        return AssessmentMapper.mapToDomainModel(assessmentEntity);
    }
}
