package org.flickit.assessment.kit.adapter.out.persistence.expertgroup;

import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupJpaEntity;
import org.flickit.assessment.kit.application.domain.ExpertGroup;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ExpertGroupMapper {

    public static ExpertGroup mapToDomainModel(ExpertGroupJpaEntity entity) {
        return new ExpertGroup(entity.getId(),
            entity.getTitle(),
            entity.getBio(),
            entity.getAbout(),
            entity.getPicture(),
            entity.getOwnerId());
    }
}
