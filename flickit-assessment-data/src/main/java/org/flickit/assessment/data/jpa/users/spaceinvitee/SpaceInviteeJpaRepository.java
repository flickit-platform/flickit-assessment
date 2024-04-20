package org.flickit.assessment.data.jpa.users.spaceinvitee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SpaceInviteeJpaRepository extends JpaRepository<SpaceInviteeJpaEntity, UUID> {
    boolean existsBySpaceIdAndEmail(long l, String s);

    @Modifying
    @Query("""
        UPDATE SpaceInviteeJpaEntity s SET
        s.creationTime = :creationTime,
        s.expirationDate = :expirationDate,
        s.createdBy = :createdBy
        where spaceId = :spaceId AND email = :email
        """)
    void update(@Param("spaceId") long spaceId,
                @Param("email") String email,
                @Param("creationTime") LocalDateTime creationTime,
                @Param("expirationDate") LocalDateTime expirationDate,
                @Param("createdBy") UUID createdBy);
}
