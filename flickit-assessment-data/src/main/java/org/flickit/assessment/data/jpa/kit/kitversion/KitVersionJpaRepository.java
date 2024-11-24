package org.flickit.assessment.data.jpa.kit.kitversion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface KitVersionJpaRepository extends JpaRepository<KitVersionJpaEntity, Long> {

    boolean existsByKitIdAndStatus(long kitId, int status);

    List<KitVersionJpaEntity> findAllByIdIn(Set<Long> ids);

    @Modifying
    @Query("""
            UPDATE KitVersionJpaEntity k
            SET k.status = :status
            WHERE k.id = :id
        """)
    void updateStatus(@Param("id") long kitVersionId, @Param("status") int status);

    @Query("""
            SELECT
                (SELECT COUNT(DISTINCT qe.id) FROM QuestionnaireJpaEntity qe WHERE  qe.kitVersionId = :id) AS questionnaireCount,
                (SELECT COUNT(DISTINCT s.id) FROM SubjectJpaEntity s WHERE s.kitVersionId = :id) AS subjectCount,
                (SELECT COUNT(DISTINCT q.id) FROM QuestionJpaEntity q WHERE q.kitVersionId = :id) AS questionCount,
                (SELECT COUNT(DISTINCT m.id) FROM MaturityLevelJpaEntity m WHERE m.kitVersionId = :id) AS maturityLevelCount
            FROM KitVersionJpaEntity k
            WHERE k.id = :id
        """)
    CountKitVersionStatsView countKitVersionStat(@Param("id") long id);
}
