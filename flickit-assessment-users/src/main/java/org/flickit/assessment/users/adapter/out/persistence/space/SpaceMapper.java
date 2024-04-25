package org.flickit.assessment.users.adapter.out.persistence.space;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.users.space.SpaceWithDetailsView;
import org.flickit.assessment.users.application.port.out.space.LoadSpaceListPort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceMapper {

    public static LoadSpaceListPort.Result mapToPortResult(SpaceWithDetailsView entity) {
        return new LoadSpaceListPort.Result(
            entity.getId(),
            entity.getTitle(),
            entity.getTitle(),
            entity.getOwnerId(),
            entity.getLastModificationTime(),
            entity.getMembersCount(),
            entity.getAssessmentsCount());
    }
}
