package org.flickit.assessment.data.jpa.kit.assessmentkitversion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AssessmentKitVersionJpaRepository extends JpaRepository<AssessmentKitVersionJpaEntity, Long> {

    @Query(value = """
            SELECT last_value FROM fak_assessment_kit_version_id_seq
        """, nativeQuery = true)
    Long getKitVersionSequenceLastValue();
}
