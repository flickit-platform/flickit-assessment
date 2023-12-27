package org.flickit.assessment.data.jpa.kit.expertgroup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ExpertGroupJpaRepository extends JpaRepository<ExpertGroupJpaEntity, Long> {

    @Query("SELECT e.ownerId FROM ExpertGroupJpaEntity as e where e.id = :id")
    UUID loadOwnerIdById(@Param("id") Long id);

    @Query("SELECT e FROM ExpertGroupJpaEntity as e")
    Page<ExpertGroupJpaEntity> findAllExpertGroup(Pageable pageable);
}
