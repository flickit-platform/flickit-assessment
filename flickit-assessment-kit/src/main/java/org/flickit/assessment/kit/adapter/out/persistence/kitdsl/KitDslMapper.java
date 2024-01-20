package org.flickit.assessment.kit.adapter.out.persistence.kitdsl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaEntity;
import org.flickit.assessment.kit.application.domain.KitDsl;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitDslMapper {

    public static KitDslJpaEntity toJpaEntity(String dslPath, String jsonPath) {
        return new KitDslJpaEntity(null, dslPath, jsonPath, null, LocalDateTime.now());
    }

    public static KitDsl toDomainModel(KitDslJpaEntity entity) {
        return new KitDsl(
            entity.getId(),
            entity.getDslPath(),
            entity.getJsonPath(),
            entity.getKitId(),
            entity.getCreationTime()
        );
    }
}
