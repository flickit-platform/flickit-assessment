package org.flickit.assessment.data.jpa.core.subjectinsight;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubjectInsightJpaRepository extends JpaRepository<SubjectInsightJpaEntity, SubjectInsightJpaEntity.EntityId> {

    boolean existsByAssessmentResultIdAndSubjectId(UUID assessmentResultId, Long subjectId);
}
