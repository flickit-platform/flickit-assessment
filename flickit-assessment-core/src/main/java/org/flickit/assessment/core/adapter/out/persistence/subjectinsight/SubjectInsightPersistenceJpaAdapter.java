package org.flickit.assessment.core.adapter.out.persistence.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.subjectinsight.CheckSubjectInsightExistPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.flickit.assessment.data.jpa.core.subjectinsight.SubjectInsightJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectinsight.SubjectInsightJpaEntity.EntityId;
import org.flickit.assessment.data.jpa.core.subjectinsight.SubjectInsightJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_SUBJECT_INSIGHT_SUBJECT_INSIGHT_NOT_FOUND;

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
        SubjectInsightJpaEntity entity = repository.findById(new EntityId(param.assessmentResultId(), param.subjectId()))
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_SUBJECT_INSIGHT_SUBJECT_INSIGHT_NOT_FOUND));
        entity.setInsight(param.insight());
        entity.setInsightTime(param.insightTime());
        entity.setInsightBy(param.insightBy());
        repository.save(entity);
    }
}
