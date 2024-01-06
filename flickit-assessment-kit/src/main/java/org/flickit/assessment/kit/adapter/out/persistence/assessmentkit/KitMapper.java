package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitsListUseCase.KitsListItem;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitsListUseCase.KitsListItemTag;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitsListUseCase.KitsListItemExpertGroup;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitMapper {

    public static KitsListItem mapToKitListItem(AssessmentKitJpaEntity entity,
                                                List<KitsListItemTag> kitsListItemTags,
                                                KitsListItemExpertGroup kitsListItemExpertGroup,
                                                int likeNumber,
                                                int numberOfAssessments) {

        return new KitsListItem(
            entity.getId(),
            entity.getTitle(),
            entity.getSummary(),
            kitsListItemTags,
            kitsListItemExpertGroup,
            likeNumber,
            numberOfAssessments,
            entity.getIsPrivate()
        );
    }

}
