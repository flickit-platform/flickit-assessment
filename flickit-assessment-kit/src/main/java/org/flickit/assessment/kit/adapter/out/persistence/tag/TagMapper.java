package org.flickit.assessment.kit.adapter.out.persistence.tag;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.tag.AssessmentKitTagJpaEntity;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetAssessmentKitListUseCase.KitListItemTag;

import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagMapper {
    public static List<KitListItemTag> mapToKitListItemTags(Set<AssessmentKitTagJpaEntity> tagJpaEntities) {
        return tagJpaEntities.stream()
                .map(tag -> new KitListItemTag(tag.getId(), tag.getCode(), tag.getTitle()))
                .toList();
    }
}
