package org.flickit.assessment.kit.adapter.out.persistence.kitlike;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kitlike.KitLikeJpaEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitLikeMapper {

    public static KitLikeJpaEntity toJpaEntity(Long kitId, UUID userId){
        return new KitLikeJpaEntity(kitId, userId);
    }
}
