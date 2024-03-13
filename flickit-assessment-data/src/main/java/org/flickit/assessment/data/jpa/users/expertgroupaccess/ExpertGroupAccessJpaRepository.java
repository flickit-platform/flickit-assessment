package org.flickit.assessment.data.jpa.users.expertgroupaccess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ExpertGroupAccessJpaRepository extends JpaRepository<ExpertGroupAccessJpaEntity, Long> {

    boolean existsByExpertGroupIdAndUserId(@Param(value = "expertGroupId") long expertGroupId,
                                           @Param(value = "userId") UUID userId);
}
