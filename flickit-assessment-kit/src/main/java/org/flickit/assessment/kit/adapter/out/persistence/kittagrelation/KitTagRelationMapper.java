package org.flickit.assessment.kit.adapter.out.persistence.kittagrelation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kittagrelation.KitTagRelationJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitTagRelationMapper {

    public static KitTagRelationJpaEntity toJpaEntity(Long tagId, Long kitId) {
        return new KitTagRelationJpaEntity(tagId, kitId);
    }
}
