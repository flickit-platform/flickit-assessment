package org.flickit.assessment.data.jpa.kit.maturitylevel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface MaturityLevelJpaRepository extends JpaRepository<MaturityLevelJpaEntity, MaturityLevelJpaEntity.EntityId> {

    List<MaturityLevelJpaEntity> findAllByKitVersionIdOrderByIndex(Long kitVersionId);

    Page<MaturityLevelJpaEntity> findByKitVersionId(Long kitVersionId, Pageable pageable);

    List<MaturityLevelJpaEntity> findAllByIdInAndKitVersionId(Collection<Long> ids, long kitVersionId);

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    List<MaturityLevelJpaEntity> findAllByKitVersionIdIn(Set<Long> kitVersionIds);

    void deleteByIdAndKitVersionId(Long id, Long kitVersionId);

    Optional<MaturityLevelJpaEntity> findByIdAndKitVersionId(Long id, long kitVersionId);

    List<MaturityLevelJpaEntity> findAllByKitVersionId(long kitVersionId);

    int countByKitVersionId(long kitVersionId);

    @Query("""
            SELECT l as maturityLevel,
                c as levelCompetence
            FROM MaturityLevelJpaEntity l
            LEFT JOIN LevelCompetenceJpaEntity c ON l.id = c.affectedLevelId AND l.kitVersionId = c.kitVersionId
            WHERE l.kitVersionId = :kitVersionId
        """)
    List<MaturityJoinCompetenceView> findAllByKitVersionIdWithCompetence(@Param(value = "kitVersionId") Long kitVersionId);

    @Modifying
    @Query("""
            UPDATE MaturityLevelJpaEntity ml SET
                ml.code = :code,
                ml.index = :index,
                ml.title = :title,
                ml.description = :description,
                ml.value = :value,
                ml.lastModificationTime = :lastModificationTime,
                ml.lastModifiedBy = :lastModifiedBy
            WHERE ml.id = :id AND ml.kitVersionId = :kitVersionId
        """)
    void update(@Param("id") Long id,
                @Param("kitVersionId") Long kitVersionId,
                @Param("code") String code,
                @Param("index") Integer index,
                @Param("title") String title,
                @Param("description") String description,
                @Param("value") Integer value,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    @Query("""
            SELECT
                a.id as id,
                a.index as index,
                a.title as title,
                COUNT(DISTINCT (CASE WHEN qi.maturityLevelId = a.id THEN qi.questionId ELSE NULL END)) as questionCount
            FROM MaturityLevelJpaEntity a
            LEFT JOIN QuestionImpactJpaEntity qi ON qi.attributeId = :attributeId AND qi.kitVersionId = a.kitVersionId
            WHERE a.kitVersionId = :kitVersionId
            GROUP BY a.id, a.index, a.title
            ORDER BY a.index
        """)
    List<MaturityQuestionCountView> loadAttributeLevels(@Param("attributeId") Long attributeId, @Param("kitVersionId") Long kitVersionId);
}
