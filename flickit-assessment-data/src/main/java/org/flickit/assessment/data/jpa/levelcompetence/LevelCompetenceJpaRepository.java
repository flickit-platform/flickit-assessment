package org.flickit.assessment.data.jpa.levelcompetence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LevelCompetenceJpaRepository extends JpaRepository<LevelCompetenceJpaEntity, Long> {
    List<LevelCompetenceJpaEntity> findByMaturityLevelId(Long maturityLevelId);

    @Modifying
    @Query("DELETE LevelCompetenceJpaEntity l WHERE " +
        "l.levelCompetence.id = :effectiveLevelId AND " +
        "l.maturityLevel.id = :maturityLevelId")
    void delete(@Param(value = "effectiveLevelId") Long effectiveLevelId, @Param(value = "maturityLevelId") Long maturityLevelId);

    @Modifying
    @Query("UPDATE LevelCompetenceJpaEntity l SET " +
        "l.value = :value " +
        "WHERE l.maturityLevel.id = :maturityLevelId " +
        "AND l.levelCompetence.id = :competenceId")
    void update(Long maturityLevelId, Long competenceId, Integer value);
}
