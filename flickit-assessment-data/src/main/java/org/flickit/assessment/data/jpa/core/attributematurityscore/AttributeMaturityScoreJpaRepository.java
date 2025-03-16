package org.flickit.assessment.data.jpa.core.attributematurityscore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttributeMaturityScoreJpaRepository extends
    JpaRepository<AttributeMaturityScoreJpaEntity, AttributeMaturityScoreJpaEntity.EntityId> {

    Optional<AttributeMaturityScoreJpaEntity> findByAttributeValueIdAndMaturityLevelId(UUID attributeValueId, long maturityLevelId);

    List<AttributeMaturityScoreJpaEntity> findByAttributeValueIdIn(Collection<UUID> attributeValueIds);

    @Query("""
            SELECT ams.score as score,
                av.attributeId as attributeId,
                ml as maturityLevel
            FROM AttributeMaturityScoreJpaEntity ams
            JOIN AttributeValueJpaEntity av ON av.id = ams.attributeValueId
            JOIN AssessmentResultJpaEntity ar ON ar.id = av.assessmentResult.id
            JOIN MaturityLevelJpaEntity ml ON ml.id = ams.maturityLevelId AND ml.kitVersionId = ar.kitVersionId
            WHERE ar.id = :assessmentResultId
        """)
    List<AttributeMaturityScoreView> findByAssessmentResultId(@Param("assessmentResultId") UUID assessmentResultId);
}
