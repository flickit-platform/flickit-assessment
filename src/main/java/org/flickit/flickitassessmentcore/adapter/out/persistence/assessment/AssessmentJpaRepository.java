package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentJpaRepository extends JpaRepository<AssessmentJpaEntity, UUID> {
    boolean existsByCodeAndSpaceId(String code, Long spaceId);

    boolean existsByTitleAndSpaceId(String title, Long spaceId);
}
