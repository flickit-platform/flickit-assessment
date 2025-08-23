package org.flickit.assessment.data.jpa.kit.levelcompetence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LevelCompetenceJpaRepository extends JpaRepository<LevelCompetenceJpaEntity, LevelCompetenceJpaEntity.EntityId> {

    List<LevelCompetenceJpaEntity> findByAffectedLevelIdAndKitVersionId(long affectedLevelId, long kitVersionId);

    List<LevelCompetenceJpaEntity> findAllByAffectedLevelIdInAndKitVersionId(Iterable<Long> levelIds, Long kitVersionId);

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    void deleteByIdAndKitVersionId(long id, long kitVersionId);

    List<LevelCompetenceJpaEntity> findAllByKitVersionId(long kitVersionId);

    @Modifying
    @Query("""
            DELETE LevelCompetenceJpaEntity l
            WHERE l.effectiveLevelId = :effectiveLevelId
                AND l.affectedLevelId = :affectedLevelId
                AND l.kitVersionId = :kitVersionId
        """)
    void delete(@Param(value = "affectedLevelId") Long affectedLevelId,
                @Param(value = "effectiveLevelId") Long effectiveLevelId,
                @Param(value = "kitVersionId") Long kitVersionId);

    @Modifying
    @Query("""
            UPDATE LevelCompetenceJpaEntity l
            SET l.value = :value,
                l.lastModificationTime = :lastModificationTime,
                l.lastModifiedBy = :lastModifiedBy
            WHERE l.affectedLevelId = :affectedLevelId
                AND l.effectiveLevelId = :effectiveLevelId
                AND l.kitVersionId = :kitVersionId
        """)
    void update(@Param(value = "affectedLevelId") Long affectedLevelId,
                @Param(value = "effectiveLevelId") Long effectiveLevelId,
                @Param(value = "kitVersionId") Long kitVersionId,
                @Param(value = "value") Integer value,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                @Param(value = "lastModifiedBy") UUID lastModifiedBy);

    @Modifying
    @Query("""
            UPDATE LevelCompetenceJpaEntity l
            SET l.value = :value,
                l.lastModificationTime = :lastModificationTime,
                l.lastModifiedBy = :lastModifiedBy
            WHERE l.id = :id AND l.kitVersionId = :kitVersionId
        """)
    void updateById(@Param("id") long id,
                    @Param("kitVersionId") long kitVersionId,
                    @Param("value") int value,
                    @Param("lastModificationTime") LocalDateTime lastModificationTime,
                    @Param("lastModifiedBy") UUID lastModifiedBy);
}
