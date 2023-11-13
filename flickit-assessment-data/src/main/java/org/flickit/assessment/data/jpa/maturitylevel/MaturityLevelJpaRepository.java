package org.flickit.assessment.data.jpa.maturitylevel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaturityLevelJpaRepository extends JpaRepository<MaturityLevelJpaEntity, Long> {

    List<MaturityLevelJpaEntity> findByAssessmentKitId(Long assessmentKitId);
}
