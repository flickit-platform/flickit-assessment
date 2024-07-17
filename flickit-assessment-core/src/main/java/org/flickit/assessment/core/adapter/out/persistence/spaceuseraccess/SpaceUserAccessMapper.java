package org.flickit.assessment.core.adapter.out.persistence.spaceuseraccess;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CreateAssessmentSpaceUserAccessPort;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceUserAccessMapper {

    public static SpaceUserAccessJpaEntity toJpaEntity(CreateAssessmentSpaceUserAccessPort.Param param, Long spaceId) {
        return new SpaceUserAccessJpaEntity(spaceId, param.userId(), param.createdBy(), param.creationTime(), param.creationTime());
    }
}
