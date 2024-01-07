package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.kit.application.port.out.expertgroup.CreateExpertGroupPort;

public class ExpertGroupMapper {

    private ExpertGroupMapper(){}
    static ExpertGroupJpaEntity mapCreateParamToJpaEntity(CreateExpertGroupPort.Param param) {
        return new ExpertGroupJpaEntity(
            null,
            param.name(),
            param.about(),
            param.picture(),
            param.website(),
            param.bio(),
            param.currentUserId()
        );
    }
}
