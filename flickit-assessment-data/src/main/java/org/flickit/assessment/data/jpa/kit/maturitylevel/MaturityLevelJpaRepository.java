package org.flickit.assessment.data.jpa.kit.maturitylevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaturityLevelJpaRepository extends JpaRepository<MaturityLevelJpaEntity, Long> {

    List<MaturityLevelJpaEntity> findAllByKitVersionIdOrderByIndex(Long kitVersionId);

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
            SELECT
                a.id as id,
                a.index as index,
                a.title as title,
                COUNT(DISTINCT CASE WHEN qi.maturityLevel.id = a.id THEN qi.questionId ELSE NULL END) as questionCount
            FROM MaturityLevelJpaEntity a
            LEFT JOIN KitVersionJpaEntity kv On kv.id = a.kitVersionId
            LEFT JOIN QuestionImpactJpaEntity qi ON qi.attributeId = :attributeId
            WHERE kv.kit.id = :kitId
            GROUP BY a.id
            ORDER BY a.index
        """)
    List<MaturityQuestionCountView> loadAttributeLevels(@Param("kitId") Long kitId, @Param("attributeId") Long attributeId);

    @Query("""
              SELECT COUNT(m) > 0
              FROM MaturityLevelJpaEntity m
              LEFT JOIN KitVersionJpaEntity kv ON m.kitVersionId = kv.id
              WHERE  m.id = :id AND kv.kit.id = :kitId
        """)
    boolean existsByIdAndKitId(@Param("id") long id, @Param("kitId") long kitId);
}
