package org.flickit.assessment.kit.adapter.out.persistence.assessmentkitversion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitversion.AssessmentKitVersionJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkitversion.KitVersionStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentKitVersionMapper {

    public static AssessmentKitVersionJpaEntity toJpaEntity(Long kitVersionId, Long kitId) {
        return new AssessmentKitVersionJpaEntity(
            kitVersionId,
            kitId,
            KitVersionStatus.ACTIVE
        );
    }
}
