package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.kit.application.port.out.expertgroup.CreateExpertGroupPort.Param;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupMapper {

    static ExpertGroupJpaEntity mapCreateParamToJpaEntity(Param param) {
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
