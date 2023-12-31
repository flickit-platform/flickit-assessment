package org.flickit.assessment.data.jpa.kit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmailIgnoreCase(String email);

    @Query("""
        SELECT u
        FROM UserJpaEntity u
        RIGHT JOIN ExpertGroupAccessJpaEntity ea on u.id = ea.userId
        RIGHT JOIN ExpertGroupJpaEntity e on e.id = ea.expertGroupId
        """)
    Optional<UserJpaEntity> findUsersOfExpertGroups();

}
