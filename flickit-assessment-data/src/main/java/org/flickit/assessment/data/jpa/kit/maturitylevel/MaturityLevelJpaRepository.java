package org.flickit.assessment.data.jpa.kit.maturitylevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaturityLevelJpaRepository extends JpaRepository<MaturityLevelJpaEntity, Long> {

    List<MaturityLevelJpaEntity> findAllByKitVersionId(Long kitVersionId);

    @Query("""
            SELECT l as maturityLevel, c as levelCompetence
            FROM MaturityLevelJpaEntity l
            LEFT JOIN LevelCompetenceJpaEntity c ON l.id = c.affectedLevel.id
            WHERE l.kitVersionId = :kitVersionId
        """)
    List<MaturityJoinCompetenceView> findAllByKitVersionIdWithCompetence(@Param(value = "kitVersionId") Long kitVersionId);

    @Query("""
            FROM MaturityLevelJpaEntity ml WHERE
            ml.kitVersionId = (SELECT l.kitVersionId FROM MaturityLevelJpaEntity AS l WHERE l.id = :id)
        """)
    List<MaturityLevelJpaEntity> findAllInKitVersionWithOneId(@Param(value = "id") Long id);

    @Query("""
        SELECT CASE WHEN EXISTS
            (SELECT 1
             FROM MaturityLevelJpaEntity m
                JOIN KitVersionJpaEntity kv ON m.kitVersionId = kv.id
                JOIN AssessmentKitJpaEntity k ON kv.kit.id = k.id
             WHERE m.id = :levelId AND k.id = :kitId)
        THEN TRUE ELSE FALSE END
    """)
    boolean existsByLevelIdAndKitId(@Param("levelId")Long levelId, @Param("kitId")Long kitId);
}
