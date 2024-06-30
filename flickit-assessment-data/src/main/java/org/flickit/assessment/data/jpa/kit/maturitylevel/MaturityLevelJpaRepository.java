package org.flickit.assessment.data.jpa.kit.maturitylevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MaturityLevelJpaRepository extends JpaRepository<MaturityLevelJpaEntity, MaturityLevelJpaEntity.EntityId> {

    List<MaturityLevelJpaEntity> findAllByKitVersionIdOrderByIndex(Long kitVersionId);

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    List<MaturityLevelJpaEntity> findAllByKitVersionIdIn(List<Long> kitVersionIds);

    void deleteByIdAndKitVersionId(Long id, Long kitVersionId);

    Optional<MaturityLevelJpaEntity> findByIdAndKitVersionId(Long id, long kitVersionId);

    @Query("""
            SELECT l as maturityLevel,
                c as levelCompetence
            FROM MaturityLevelJpaEntity l
            LEFT JOIN LevelCompetenceJpaEntity c ON l.id = c.affectedLevelId
            WHERE l.kitVersionId = :kitVersionId
        """)
    List<MaturityJoinCompetenceView> findAllByKitVersionIdWithCompetence(@Param(value = "kitVersionId") Long kitVersionId);

    @Query("""
            FROM MaturityLevelJpaEntity ml
            WHERE ml.kitVersionId = (SELECT l.kitVersionId FROM MaturityLevelJpaEntity AS l WHERE l.id = :id)
        """)
    List<MaturityLevelJpaEntity> findAllInKitVersionWithOneId(@Param(value = "id") Long id);

    @Query("""
            SELECT
                a.id as id,
                a.index as index,
                a.title as title,
                COUNT(DISTINCT (CASE WHEN qi.maturityLevelId = a.id THEN qi.questionId ELSE NULL END)) as questionCount
            FROM MaturityLevelJpaEntity a
            LEFT JOIN QuestionImpactJpaEntity qi ON qi.attributeId = :attributeId
            WHERE a.kitVersionId = :kitVersionId
            GROUP BY a.id, a.index, a.title
            ORDER BY a.index
        """)
    List<MaturityQuestionCountView> loadAttributeLevels(@Param("attributeId") Long attributeId, @Param("kitVersionId") Long kitVersionId);
}
