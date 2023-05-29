package org.flickit.flickitassessmentcore.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.mapper.AssessmentMapper;
import org.flickit.flickitassessmentcore.adapter.out.persistence.repository.AssessmentRepository;
import org.flickit.flickitassessmentcore.application.port.out.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class AssessmentPersistenceAdapter implements LoadAssessmentPort {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentMapper assessmentMapper;

    @Override
    public Assessment loadAssessment(UUID assessmentId) {
        AssessmentEntity assessmentEntity = assessmentRepository.getReferenceById(assessmentId);
        return assessmentMapper.mapToDomainModel(assessmentEntity);
    }
}
