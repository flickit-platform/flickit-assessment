package org.flickit.assessment.data.jpa.users.spaceuseraccess;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpaceUserAccessJpaRepository extends JpaRepository<SpaceUserAccessJpaEntity, Long> {

    boolean existsBySpaceIdAndUserId(@Param("spaceId") Long spaceId, @Param("userId") UUID userId);

    void deleteBySpaceIdAndUserId(@Param("spaceId") long spaceId, @Param("userId") UUID userId);

    @Query("""
            SELECT u.id as id,
                   u.email as email,
                   u.displayName as displayName,
                   u.bio as bio,
                   u.picture as picture,
                   u.linkedin as linkedin
            FROM SpaceUserAccessJpaEntity s
            LEFT JOIN UserJpaEntity u ON s.userId = u.id
            WHERE s.spaceId = :spaceId
        """)
    Page<SpaceMembersView> findMembers(@Param("spaceId") long spaceId, Pageable pageable);

    @Query("""
            SELECT COUNT(u)
            FROM SpaceUserAccessJpaEntity AS u
            JOIN SpaceJpaEntity AS s ON s.id = u.spaceId
            WHERE u.spaceId = :spaceId
        """)
    int countBySpaceId(long spaceId);
}
