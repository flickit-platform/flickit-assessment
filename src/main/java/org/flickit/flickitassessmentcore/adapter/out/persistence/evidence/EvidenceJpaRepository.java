package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EvidenceJpaRepository extends JpaRepository<EvidenceJpaEntity, UUID> {

}
