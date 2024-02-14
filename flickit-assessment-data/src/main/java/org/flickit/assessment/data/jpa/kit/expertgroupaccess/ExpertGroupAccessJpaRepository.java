package org.flickit.assessment.data.jpa.kit.expertgroupaccess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ExpertGroupAccessJpaRepository extends JpaRepository<ExpertGroupAccessJpaEntity, Long> {

    boolean existsByExpertGroupIdAndUserId(@Param(value = "expertGroupId") long expertGroupId,
                                           @Param(value = "userId") UUID userId);

    @Query("""
        SELECT CASE WHEN (count(a) > 0)  THEN true else false END
        FROM ExpertGroupAccessJpaEntity a
        WHERE a.inviteToken = :inviteToken
        """)
    boolean findByInviteToken(UUID inviteToken);
}
