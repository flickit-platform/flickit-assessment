package org.flickit.assessment.kit.adapter.out.persistence.kituseraccess;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kituseraccess.KitUserAccessJpaEntity;
import org.flickit.assessment.kit.application.domain.KitUser;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitUserAccessMapper {
    public static KitUser mapToDomainModel(KitUserAccessJpaEntity entity) {
        return new KitUser(
            entity.getId().getKitId(),
            entity.getId().getUserId()
        );
    }
}
