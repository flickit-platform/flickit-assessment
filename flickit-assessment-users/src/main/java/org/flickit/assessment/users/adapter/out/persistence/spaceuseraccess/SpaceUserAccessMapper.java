package org.flickit.assessment.users.adapter.out.persistence.spaceuseraccess;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.users.application.domain.SpaceUserAccess;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceUserAccessMapper {

    public static SpaceUserAccessJpaEntity mapToJpaEntity(SpaceUserAccess access) {
        return new SpaceUserAccessJpaEntity(access.getSpaceId(), access.getUserId(),
            access.getCreatedBy(), access.getCreationTime(),access.getCreationTime());
    }
}
