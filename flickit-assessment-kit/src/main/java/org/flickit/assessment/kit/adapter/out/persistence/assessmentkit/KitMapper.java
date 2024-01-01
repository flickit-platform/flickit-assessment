package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitListUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitListUseCase.AssessmentKitListItem;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitListUseCase.KitListItemTag;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitListUseCase.KitListItemExpertGroup;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitMapper {

    public static AssessmentKitListItem mapToKitListItem(AssessmentKitJpaEntity entity,
                                                         List<KitListItemTag> kitListItemTags,
                                                         KitListItemExpertGroup kitListItemExpertGroup,
                                                         int likeNumber,
                                                         int numberOfAssessments) {

        return new AssessmentKitListItem(
            entity.getId(),
            entity.getTitle(),
            entity.getSummary(),
            kitListItemTags,
            kitListItemExpertGroup,
            likeNumber,
            numberOfAssessments,
            entity.getIsPrivate()
        );
    }

}
