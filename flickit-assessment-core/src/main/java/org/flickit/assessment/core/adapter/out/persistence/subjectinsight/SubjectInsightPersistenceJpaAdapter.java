package org.flickit.assessment.core.adapter.out.persistence.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.flickit.assessment.data.jpa.core.subjectinsight.SubjectInsightJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubjectInsightPersistenceJpaAdapter implements
    LoadSubjectInsightPort,
    CreateSubjectInsightPort,
    UpdateSubjectInsightPort {

    private final SubjectInsightJpaRepository repository;

    @Override
    public Optional<SubjectInsight> load(UUID assessmentResultId, Long subjectId) {
        return repository.findByAssessmentResultIdAndSubjectId(assessmentResultId, subjectId)
            .map(SubjectInsightMapper::mapToDomainModel);
    }

    @Override
    public void persist(SubjectInsight subjectInsight) {
        repository.save(SubjectInsightMapper.mapToJpaEntity(subjectInsight));
    }

    @Override
    public void update(SubjectInsight subjectInsight) {
        repository.updateByAssessmentResultIdAndSubjectId(subjectInsight.getAssessmentResultId(),
                subjectInsight.getSubjectId(),
                subjectInsight.getInsight(),
                subjectInsight.getInsightTime(),
                subjectInsight.getInsightBy());
    }
}
