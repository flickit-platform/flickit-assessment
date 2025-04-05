package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceType;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.users.application.domain.Space;
import org.flickit.assessment.users.application.domain.SpaceStatus;

import static org.flickit.assessment.users.application.service.constant.SpaceConstants.NOT_DELETED_DELETION_TIME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceMapper {

    public static SpaceJpaEntity mapToJpaEntity(Space space) {
        return new SpaceJpaEntity(
            null,
            space.getCode(),
            space.getTitle(),
            space.getType().getId(),
            space.getOwnerId(),
            space.getStatus().getId(),
            space.getSubscriptionExpiry(),
            space.getCreationTime(),
            space.getLastModificationTime(),
            space.getCreatedBy(),
            space.getLastModifiedBy(),
            false,
            NOT_DELETED_DELETION_TIME);
    }

    public static Space mapToDomain(SpaceJpaEntity entity) {
        return new Space(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            SpaceType.valueOfById(entity.getType()),
            entity.getOwnerId(),
            SpaceStatus.valueOfById(entity.getStatus()),
            entity.getSubscriptionExpiry(),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy());
    }
}
