package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitListUseCase.KitListItemExpertGroup;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpertGroupMapper {
    public static KitListItemExpertGroup mapToKitListItemExpertGroup(ExpertGroupJpaEntity expertGroupEntity) {
        return new KitListItemExpertGroup(
                expertGroupEntity.getId(),
                expertGroupEntity.getName(),
                expertGroupEntity.getPicture()
        );
    }
}
