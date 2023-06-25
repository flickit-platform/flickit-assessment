package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EvidenceJpaRepository extends JpaRepository<EvidenceJpaEntity, UUID> {

    @Query("select a from EvidenceJpaEntity a " +
        "where a.questionId = :questionId")
    List<EvidenceJpaEntity> findEvidenceJpaEntitiesByQuestionId(@Param("questionId") Long questionId);


}
