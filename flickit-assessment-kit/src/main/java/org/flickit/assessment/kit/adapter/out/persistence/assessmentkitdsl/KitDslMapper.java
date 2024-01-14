package org.flickit.assessment.kit.adapter.out.persistence.assessmentkitdsl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaEntity;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitDslMapper {

    public static KitDslJpaEntity toJpaEntity(String dslPath, String jsonPath) {
        return new KitDslJpaEntity(null, dslPath, jsonPath, null, LocalDateTime.now());
    }
}
