package org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubjectValueJpaRepository extends JpaRepository<SubjectValueJpaEntity, UUID> {

    List<SubjectValueJpaEntity> findByAssessmentResultId(UUID resultId);

}
