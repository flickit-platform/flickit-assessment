package org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue;

import org.flickit.flickitassessmentcore.adapter.out.persistence.answer.AnswerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QualityAttributeValueRepository extends JpaRepository<QualityAttributeValueJpaEntity, UUID> {

    @Query("select a from QualityAttributeValueJpaEntity a " +
        "where a.assessmentResult.id = :resultId")
    List<QualityAttributeValueJpaEntity> findQualityAttributeValueByResultId(@Param("resultId") UUID resultId);
}
