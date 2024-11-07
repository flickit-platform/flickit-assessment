package org.flickit.assessment.data.jpa.users.expertgroup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpertGroupJpaRepository extends JpaRepository<ExpertGroupJpaEntity, Long> {

    Optional<ExpertGroupJpaEntity> findByIdAndDeletedFalse(long id);

    boolean existsByIdAndDeletedFalse(@Param(value = "id") long id);

    @Query("SELECT e.ownerId FROM ExpertGroupJpaEntity as e where e.id = :id and deleted=false")
    Optional<UUID> loadOwnerIdById(@Param("id") Long id);

    @Query("""
            SELECT e
            FROM ExpertGroupJpaEntity e
            LEFT JOIN AssessmentKitJpaEntity a On e.id = a.expertGroupId
            WHERE a.id = :kitId AND e.deleted = false
        """)
    Optional<ExpertGroupJpaEntity> findByKitId(@Param("kitId") long kitId);

    @Query("""
            SELECT
                e.id as id,
                e.title as title,
                e.picture as picture,
                e.bio as bio,
                e.ownerId as ownerId,
                COUNT(DISTINCT CASE WHEN ak.published = true THEN ak.id ELSE NULL END) as publishedKitsCount
            FROM ExpertGroupJpaEntity e
            LEFT JOIN AssessmentKitJpaEntity ak on e.id = ak.expertGroupId
            LEFT JOIN ExpertGroupAccessJpaEntity ac on e.id = ac.expertGroupId
            WHERE ac.userId = :userId AND e.deleted = FALSE
            GROUP BY
                e.id,
                e.title,
                e.picture,
                e.bio,
                e.ownerId,
                ac.lastSeen
            ORDER BY ac.lastSeen DESC
        """)
    Page<ExpertGroupWithDetailsView> findByUserId(@Param(value = "userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT
                a.expertGroupId as id,
                COUNT(DISTINCT userId) as membersCount
            FROM ExpertGroupAccessJpaEntity a
            WHERE a.expertGroupId IN :expertGroupIdList AND a.status = 1
            GROUP BY a.expertGroupId
        """)
    List<ExpertGroupMembersCountView> expertGroupMembersCount(List<Long> expertGroupIdList);

    @Query("""
            SELECT
                u.id as id,
                u.displayName as displayName,
                e.id as expertGroupId
            FROM ExpertGroupJpaEntity e
            JOIN ExpertGroupAccessJpaEntity a on a.expertGroupId = e.id
            JOIN UserJpaEntity u on a.userId = u.id
            WHERE a.status = 1 AND a.expertGroupId in :expertGroupIds
        """)
    List<ExpertGroupMembersView> findMembersByExpertGroupId(@Param("expertGroupIds") List<Long> expertGroupIds);

    @Query("""
            SELECT
                e.userId as userId
            FROM ExpertGroupAccessJpaEntity e
            WHERE e.expertGroupId = :expertGroupId and e.status = 1
        """)
    List<UUID> findMemberIdsByExpertGroupId(@Param(value = "expertGroupId") Long expertGroupId);

    @Modifying
    @Query("""
            UPDATE ExpertGroupJpaEntity e
            SET e.deleted = true,
                e.deletionTime = :deletionTime
            WHERE e.id = :expertGroupId
        """)
    void delete(@Param("expertGroupId") Long expertGroupId,
                @Param("deletionTime") long deletionTime);

    @Query("""
            SELECT
                COUNT(DISTINCT CASE WHEN ak.published = true THEN ak.id ELSE NULL END) as publishedKitsCount,
                COUNT(DISTINCT CASE WHEN ak.published = false THEN ak.id ELSE NULL END) as unPublishedKitsCount
            FROM ExpertGroupJpaEntity e
            LEFT JOIN AssessmentKitJpaEntity ak on e.id = ak.expertGroupId
            WHERE e.id = :expertGroupId
        """)
    KitsCountView countKits(@Param("expertGroupId") long expertGroupId);

    @Modifying
    @Query("""
            UPDATE ExpertGroupJpaEntity e
            SET e.code = :code,
                e.title = :title,
                e.bio = :bio,
                e.about = :about,
                e.website = :website,
                e.lastModificationTime = :lastModificationTime,
                e.lastModifiedBy = :lastModifiedBy
            WHERE e.id = :id AND e.deleted = FALSE
        """)
    void update(@Param("id") long id,
                @Param("code") String code,
                @Param("title") String title,
                @Param("bio") String bio,
                @Param("about") String about,
                @Param("website") String website,
                @Param("lastModificationTime") LocalDateTime localDateTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    @Modifying
    @Query("""
            UPDATE ExpertGroupJpaEntity e
            SET e.picture = :picture
            WHERE e.id = :id
        """)
    void updatePicture(@Param("id") long expertGroupId, @Param("picture") String picture);
}
