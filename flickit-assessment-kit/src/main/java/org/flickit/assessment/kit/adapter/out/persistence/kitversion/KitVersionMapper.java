package org.flickit.assessment.kit.adapter.out.persistence.kitversion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kitversion.KitVersionJpaEntity;
import org.flickit.assessment.data.jpa.kit.kitversion.KitVersionStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitVersionMapper {

    public static KitVersionJpaEntity toJpaEntity(Long kitVersionId, Long kitId) {
        return new KitVersionJpaEntity(
            kitVersionId,
            kitId,
            KitVersionStatus.ACTIVE
        );
    }
}
