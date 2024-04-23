package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaEntity;
import org.flickit.assessment.users.application.port.out.space.CreateSpacePort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceMapper {

    public static SpaceJpaEntity mapCreateParamToJpaEntity(CreateSpacePort.Param param) {
        return new SpaceJpaEntity(
            null,
            param.code(),
            param.title(),
            param.ownerId(),
            param.creationTime(),
            param.lastModificationTime(),
            param.createdBy(),
            param.lastModifiedBy());
    }
}
