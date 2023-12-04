package org.flickit.assessment.data.jpa.kit.levelcompetence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LevelCompetenceJpaRepository extends JpaRepository<LevelCompetenceJpaEntity, Long> {

    List<LevelCompetenceJpaEntity> findByAffectedLevelId(Long affectedLevelId);

    @Modifying
    @Query("DELETE LevelCompetenceJpaEntity l WHERE " +
        "l.effectiveLevel.id = :effectiveLevelId AND " +
        "l.affectedLevel.id = :affectedLevelId")
    void delete(@Param(value = "affectedLevelId") Long affectedLevelId, @Param(value = "effectiveLevelId") Long effectiveLevelId);

    @Modifying
    @Query("UPDATE LevelCompetenceJpaEntity l SET " +
        "l.value = :value " +
        "WHERE l.affectedLevel.id = :affectedLevelId " +
        "AND l.effectiveLevel.id = :effectiveLevelId")
    void update(Long affectedLevelId, Long effectiveLevelId, Integer value);
}
