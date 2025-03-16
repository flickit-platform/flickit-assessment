package org.flickit.assessment.core.adapter.out.persistence.space;

import org.flickit.assessment.common.application.domain.space.SpaceStatus;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;

public class SpaceMapper {

    public static Space mapToDomain(SpaceJpaEntity entity) {
        return new Space(entity.getId(),
            entity.getTitle(),
            SpaceStatus.valueOfById(entity.getStatus()));
    }
}
