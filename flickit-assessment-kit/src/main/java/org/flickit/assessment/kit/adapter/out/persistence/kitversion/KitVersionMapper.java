package org.flickit.assessment.kit.adapter.out.persistence.kitversion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitversion.KitVersionJpaEntity;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitVersionMapper {

    public static KitVersionJpaEntity toJpaEntity(AssessmentKitJpaEntity kit, KitVersionStatus status) {
        return new KitVersionJpaEntity(
            null,
            kit,
            status.ordinal(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            kit.getCreatedBy(),
            kit.getLastModifiedBy()
        );
    }
}
