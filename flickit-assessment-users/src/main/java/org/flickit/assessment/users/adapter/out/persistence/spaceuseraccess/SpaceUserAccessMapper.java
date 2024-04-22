package org.flickit.assessment.users.adapter.out.persistence.spaceuseraccess;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceUserAccessMapper {
    
    public static SpaceUserAccessJpaEntity paramsToEntity(CreateSpaceUserAccessPort.Param param) {
        return new SpaceUserAccessJpaEntity(param.spaceId(), param.userid(), param.creationTime(), param.createdBy());
    }
}
