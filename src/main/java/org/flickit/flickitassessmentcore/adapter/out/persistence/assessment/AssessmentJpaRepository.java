package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AssessmentJpaRepository extends JpaRepository<AssessmentJpaEntity, UUID> {

    @Query("select a from AssessmentJpaEntity a where a.spaceId = :spaceId")
    public List<AssessmentJpaEntity> loadAssessmentBySpaceId(@Param("spaceId") long spaceId);

}
