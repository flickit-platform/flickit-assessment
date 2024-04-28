package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.space.SpaceWithDetailsView;
import org.flickit.assessment.users.application.domain.Space;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceMapper {

    static Space mapToDomainModel(SpaceWithDetailsView entity) {
        return new Space(entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getOwnerId(),
            entity.getLastModificationTime(),
            entity.getMembersCount(),
            entity.getAssessmentsCount());
    }
}
