package org.flickit.assessment.data.jpa.kit.maturitylevel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MaturityLevelJpaRepository extends JpaRepository<MaturityLevelJpaEntity, Long> {

    List<MaturityLevelJpaEntity> findAllByKitVersionId(Long kitVersionId);


    @Query("""
            SELECT l as maturityLevel, c as levelCompetence
            FROM MaturityLevelJpaEntity l
            LEFT JOIN LevelCompetenceJpaEntity c ON l.id = c.affectedLevel.id
            WHERE l.kitVersionId = (SELECT k.kitVersionId FROM AssessmentKitJpaEntity k WHERE k.id = :kitId)
        """)
    List<MaturityJoinCompetenceView> findAllByKitIdWithCompetence(Long kitId);
}
