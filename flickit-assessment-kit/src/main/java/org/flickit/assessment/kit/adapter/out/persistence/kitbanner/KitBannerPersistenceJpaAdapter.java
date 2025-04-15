package org.flickit.assessment.kit.adapter.out.persistence.kitbanner;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.data.jpa.kit.kitbanner.KitBannerJpaRepository;
import org.flickit.assessment.kit.application.domain.KitBanner;
import org.flickit.assessment.kit.application.port.out.kitbanner.LoadKitBannersPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KitBannerPersistenceJpaAdapter implements LoadKitBannersPort {

    private final KitBannerJpaRepository kitBannerRepository;

    @Override
    public List<KitBanner> loadSliderBanners(KitLanguage kitLanguage) {
        return kitBannerRepository.findAllByLangIdAndInSliderIsTrue(kitLanguage.getId())
            .stream()
            .map(KitBannerMapper::toDomainModel)
            .toList();
    }
}
