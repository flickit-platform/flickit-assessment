package org.flickit.assessment.kit.adapter.out.persistence.users.expertgroup;

import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.kit.application.domain.ExpertGroup;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ExpertGroupMapper {

    public static ExpertGroup mapToDomainModel(ExpertGroupJpaEntity expertGroup) {
        return new ExpertGroup(expertGroup.getId(),
            expertGroup.getTitle(),
            expertGroup.getPicture(),
            expertGroup.getOwnerId());
    }
}
