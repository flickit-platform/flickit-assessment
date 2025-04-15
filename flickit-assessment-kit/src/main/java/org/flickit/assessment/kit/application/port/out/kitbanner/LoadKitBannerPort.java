package org.flickit.assessment.kit.application.port.out.kitbanner;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.domain.KitBanner;

import java.util.List;

public interface LoadKitBannerPort {

    List<KitBanner> loadSliderBanners(KitLanguage kitLanguage);
}
