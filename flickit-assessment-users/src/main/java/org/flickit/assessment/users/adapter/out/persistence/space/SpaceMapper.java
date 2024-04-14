package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.space.SpaceJpaEntity;
import org.flickit.assessment.users.application.domain.Space;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceMapper {

    public static Space mapToDomainModel(SpaceJpaEntity entity) {
        return new Space(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getOwnerId()
        );
    }
}
