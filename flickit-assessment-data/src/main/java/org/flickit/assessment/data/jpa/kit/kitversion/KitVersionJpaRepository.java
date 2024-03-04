package org.flickit.assessment.data.jpa.kit.kitversion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KitVersionJpaRepository extends JpaRepository<KitVersionJpaEntity, Long> {

    @Query(value = """
            SELECT last_value FROM fak_kit_version_id_seq
        """, nativeQuery = true)
    Long getKitVersionSequenceLastValue();
}
