package org.flickit.assessment.core.adapter.out.persistence.subjectinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.port.out.subjectinsight.*;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectinsight.SubjectInsightJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.APPROVE_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.SUBJECT_INSIGHT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SubjectInsightPersistenceJpaAdapter implements
    LoadSubjectInsightPort,
    CreateSubjectInsightPort,
    UpdateSubjectInsightPort,
    LoadSubjectInsightsPort,
    ApproveSubjectInsightPort {

    private final SubjectInsightJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;

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
    public void persistAll(List<SubjectInsight> subjectInsights) {
        var entities = subjectInsights.stream()
            .map(SubjectInsightMapper::mapToJpaEntity)
            .toList();
        repository.saveAll(entities);
    }

    @Override
    public void update(SubjectInsight subjectInsight) {
        repository.update(subjectInsight.getAssessmentResultId(),
            subjectInsight.getSubjectId(),
            subjectInsight.getInsight(),
            subjectInsight.getInsightTime(),
            subjectInsight.getLastModificationTime(),
            subjectInsight.getInsightBy(),
            subjectInsight.isApproved());
    }

    @Override
    public List<SubjectInsight> loadSubjectInsights(UUID assessmentResultId) {
        return repository.findByAssessmentResultId(assessmentResultId)
            .stream()
            .map(SubjectInsightMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public void approve(UUID assessmentId, long subjectId, LocalDateTime lastModificationTime) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(APPROVE_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        if (!repository.existsByAssessmentResultIdAndSubjectId(assessmentResult.getId(), subjectId))
            throw new ResourceNotFoundException(SUBJECT_INSIGHT_ID_NOT_FOUND);

        repository.approve(assessmentResult.getId(), subjectId, lastModificationTime);
    }

    @Override
    public void approveAll(UUID assessmentId, LocalDateTime lastModificationTime) {
        var assessmentResult = assessmentResultRepository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));

        repository.approveAll(assessmentResult.getId(), lastModificationTime);
    }
}
