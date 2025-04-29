package org.flickit.assessment.kit.adapter.out.persistence.kitbanner;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.ImageSize;
import org.flickit.assessment.data.jpa.kit.kitbanner.KitBannerJpaEntity;
import org.flickit.assessment.kit.application.domain.KitBanner;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KitBannerMapper {

    public static KitBanner toDomainModel(KitBannerJpaEntity entity) {
        return new KitBanner(entity.getKitId(),
            ImageSize.valueOfById(entity.getSize()),
            entity.getPath());
    }
}
