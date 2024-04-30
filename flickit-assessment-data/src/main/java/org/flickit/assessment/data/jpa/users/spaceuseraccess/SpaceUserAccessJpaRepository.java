package org.flickit.assessment.data.jpa.users.spaceuseraccess;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface SpaceUserAccessJpaRepository extends JpaRepository<SpaceUserAccessJpaEntity, Long> {

    boolean existsByUserIdAndSpaceId(UUID userId, Long spaceId);

    @Query("""
            SELECT u.id as id,
                   u.email as email,
                   u.displayName as displayName,
                   u.bio as bio,
                   u.picture as picture,
                   u.linkedin as linkedin
            FROM SpaceUserAccessJpaEntity s
            LEFT JOIN UserJpaEntity u on s.userId = u.id
            WHERE s.spaceId = :spaceId
        """)
    Page<SpaceMembersView> findMembers(long spaceId, Pageable pageable);
}
