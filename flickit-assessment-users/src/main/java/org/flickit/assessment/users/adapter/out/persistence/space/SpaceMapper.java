package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceStatus;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.users.application.domain.Space;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceMapper {

    public static Space mapToDomain(SpaceJpaEntity entity) {
        return new Space(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            SpaceType.valueOfById(entity.getType()),
            entity.getOwnerId(),
            SpaceStatus.valueOfById(entity.getStatus()),
            entity.getSubscriptionExpiry(),
            entity.isDefault(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy());
    }
}
