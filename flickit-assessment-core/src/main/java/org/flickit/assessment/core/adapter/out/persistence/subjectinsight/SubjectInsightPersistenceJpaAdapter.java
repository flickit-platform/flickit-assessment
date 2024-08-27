package org.flickit.assessment.core.adapter.out.persistence.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.subjectinsight.CheckSubjectInsightExistPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.flickit.assessment.data.jpa.core.subjectinsight.SubjectInsightJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubjectInsightPersistenceJpaAdapter implements
    CheckSubjectInsightExistPort,
    CreateSubjectInsightPort,
    UpdateSubjectInsightPort {

    private final SubjectInsightJpaRepository repository;

    @Override
    public boolean exists(UUID assessmentResultId, Long subjectId) {
        return repository.existsByAssessmentResultIdAndSubjectId(assessmentResultId, subjectId);
    }

    @Override
    public void persist(CreateSubjectInsightPort.Param param) {
        repository.save(SubjectInsightMapper.mapToJpaEntity(param));
    }

    @Override
    public void update(UpdateSubjectInsightPort.Param param) {
        repository.updateByAssessmentResultIdAndSubjectId(param.assessmentResultId(),
            param.subjectId(),
            param.insight(),
            param.insightTime(),
            param.insightBy());
    }
}
