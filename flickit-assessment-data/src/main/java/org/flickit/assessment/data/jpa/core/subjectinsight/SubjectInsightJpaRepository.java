package org.flickit.assessment.data.jpa.core.subjectinsight;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubjectInsightJpaRepository extends JpaRepository<SubjectInsightJpaEntity, SubjectInsightJpaEntity.EntityId> {

    Optional<SubjectInsightJpaEntity> findByAssessmentResultIdAndSubjectId(UUID assessmentResultId, Long subjectId);
}
