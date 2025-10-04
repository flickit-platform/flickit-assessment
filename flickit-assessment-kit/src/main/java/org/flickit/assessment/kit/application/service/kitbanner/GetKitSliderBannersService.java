package org.flickit.assessment.kit.application.service.kitbanner;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.ImageSize;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.domain.KitBanner;
import org.flickit.assessment.kit.application.port.in.kitbanner.GetKitSliderBannersUseCase;
import org.flickit.assessment.kit.application.port.out.kitbanner.LoadKitBannersPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitSliderBannersService implements GetKitSliderBannersUseCase {

    private final LoadKitBannersPort loadKitBannersPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    @Override
    public List<Result> getSliderBanners(Param param) {
        var portResult = loadKitBannersPort.loadSliderBanners(KitLanguage.valueOf(param.getLang()));

        var kitBannersMap = portResult.stream()
            .collect(Collectors.groupingBy(KitBanner::getKitId));

        return kitBannersMap.entrySet().stream()
            .map(entry -> {
                Long kitId = entry.getKey();
                List<KitBanner> banners = entry.getValue();

                String smallBanner = findPathBySize(banners, ImageSize.SMALL);
                String largeBanner = findPathBySize(banners, ImageSize.LARGE);

                return new Result(kitId,
                    createFileDownloadLinkPort.createDownloadLinkSafe(smallBanner, EXPIRY_DURATION),
                    createFileDownloadLinkPort.createDownloadLinkSafe(largeBanner, EXPIRY_DURATION));
            })
            .filter(b -> b.largeBanner() != null || b.smallBanner() != null)
            .toList();
    }

    private String findPathBySize(List<KitBanner> banners, ImageSize size) {
        return banners.stream()
            .filter(b -> b.getSize() == size)
            .findFirst()
            .map(KitBanner::getPath)
            .orElse(null);
    }
}
