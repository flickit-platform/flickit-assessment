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
        "l.levelCompetence.id = :competenceLevelId AND " +
        "l.maturityLevel.id = :maturityLevelId")
    void delete(@Param(value = "competenceLevelId") Long competenceLevelId, @Param(value = "maturityLevelId") Long maturityLevelId);

    @Modifying
    @Query("UPDATE LevelCompetenceJpaEntity l SET " +
        "l.levelCompetence.id = :competenceId, " +
        "l.value = :value " +
        "WHERE l.id = :id")
    void update(Long id, Long competenceId, Integer value);
}
