package org.flickit.assessment.kit.adapter.out.persistence.kitdsl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaEntity;
import org.flickit.assessment.kit.application.domain.AssessmentKitDsl;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitDslMapper {

    public static KitDslJpaEntity toJpaEntity(String dslPath, String jsonPath) {
        return new KitDslJpaEntity(null, dslPath, jsonPath, null, LocalDateTime.now());
    }

    public static AssessmentKitDsl toDomainModel(KitDslJpaEntity entity) {
        return new AssessmentKitDsl(
            entity.getId(),
            entity.getDslPath(),
            entity.getJsonPath(),
            entity.getKitId(),
            entity.getCreationTime()
        );
    }
}
