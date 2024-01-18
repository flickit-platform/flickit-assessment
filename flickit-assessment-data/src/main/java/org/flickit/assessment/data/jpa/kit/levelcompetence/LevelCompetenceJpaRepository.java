package org.flickit.assessment.data.jpa.kit.levelcompetence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LevelCompetenceJpaRepository extends JpaRepository<LevelCompetenceJpaEntity, Long> {

    List<LevelCompetenceJpaEntity> findByAffectedLevelId(Long affectedLevelId);

    @Modifying
    @Query("DELETE LevelCompetenceJpaEntity l WHERE " +
        "l.effectiveLevel.id = :effectiveLevelId AND " +
        "l.affectedLevel.id = :affectedLevelId")
    void delete(@Param(value = "affectedLevelId") Long affectedLevelId,
                @Param(value = "effectiveLevelId") Long effectiveLevelId);

    @Modifying
    @Query("""
           UPDATE LevelCompetenceJpaEntity l SET
            l.value = :value,
            l.lastModificationTime = :lastModificationTime,
            l.lastModifiedBy = :lastModifiedBy
           WHERE l.affectedLevel.id = :affectedLevelId AND l.effectiveLevel.id = :effectiveLevelId
           """)
    void update(@Param(value = "affectedLevelId") Long affectedLevelId,
                @Param(value = "effectiveLevelId") Long effectiveLevelId,
                @Param(value = "value") Integer value,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                @Param(value = "lastModifiedBy") UUID lastModifiedBy);
}
