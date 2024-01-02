package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitsListUseCase.KitsListItemExpertGroup;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupMapper {
    public static KitsListItemExpertGroup mapToKitListItemExpertGroup(ExpertGroupJpaEntity expertGroupEntity) {
        return new KitsListItemExpertGroup(
                expertGroupEntity.getId(),
                expertGroupEntity.getName(),
                expertGroupEntity.getPicture()
        );
    }
}
