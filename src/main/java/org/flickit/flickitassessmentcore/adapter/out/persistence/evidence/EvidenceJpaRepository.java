package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EvidenceJpaRepository extends JpaRepository<EvidenceJpaEntity, UUID> {


    Page<EvidenceJpaEntity> findByQuestionIdOrderByLastModificationTimeDesc(Long questionId, Pageable pageable);


}
