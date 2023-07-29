package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssessmentJpaRepository extends JpaRepository<AssessmentJpaEntity, UUID> {

    List<AssessmentJpaEntity> findBySpaceIdAndDeletionTimeOrderByLastModificationDateDesc(long spaceId, long deletionTime, Pageable pageable);

}
