package org.flickit.assessment.kit.adapter.out.persistence.kitversion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitversion.KitVersionJpaEntity;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitVersionMapper {

    public static KitVersionJpaEntity toJpaEntity(AssessmentKitJpaEntity kit) {
        return new KitVersionJpaEntity(
            null,
            kit,
            KitVersionStatus.ACTIVE.ordinal()
        );
    }
}
