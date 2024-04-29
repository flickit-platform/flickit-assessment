package org.flickit.assessment.kit.adapter.out.persistence.kittag;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kittag.KitTagJpaEntity;
import org.flickit.assessment.kit.application.domain.KitTag;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitTagMapper {

    public static KitTag toDomainModel(KitTagJpaEntity entity) {
        return new KitTag(entity.getId(), entity.getCode(), entity.getTitle());

    }
}
