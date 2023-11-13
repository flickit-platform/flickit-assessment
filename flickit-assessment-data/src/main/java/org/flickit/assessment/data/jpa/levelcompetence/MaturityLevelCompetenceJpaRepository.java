package org.flickit.assessment.data.jpa.levelcompetence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaturityLevelCompetenceJpaRepository extends JpaRepository<MaturityLevelCompetenceJpaEntity, Long> {
    List<MaturityLevelCompetenceJpaEntity> findByMaturityLevelId(Long maturityLevelId);
}
