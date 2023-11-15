package org.flickit.assessment.data.jpa.levelcompetence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LevelCompetenceJpaRepository extends JpaRepository<LevelCompetenceJpaEntity, Long> {
    List<LevelCompetenceJpaEntity> findByMaturityLevelId(Long maturityLevelId);
}
