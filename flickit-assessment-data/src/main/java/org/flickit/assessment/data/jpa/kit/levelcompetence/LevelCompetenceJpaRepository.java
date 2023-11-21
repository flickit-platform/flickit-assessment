package org.flickit.assessment.data.jpa.kit.levelcompetence;

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
        "l.maturityLevel.id = :affectedLevelId")
    void delete(@Param(value = "affectedLevelId") Long affectedLevelId, @Param(value = "effectiveLevelId") Long effectiveLevelId);

    @Modifying
    @Query("UPDATE LevelCompetenceJpaEntity l SET " +
        "l.value = :value " +
        "WHERE l.maturityLevel.id = :affectedLevelId " +
        "AND l.levelCompetence.id = :effectiveLevelId")
    void update(Long affectedLevelId, Long effectiveLevelId, Integer value);
}
