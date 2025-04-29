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
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

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

        Map<Long, List<KitBanner>> banners = portResult.stream()
            .collect(groupingBy(KitBanner::getKitId));

        return toResult(banners);
    }

    List<Result> toResult(Map<Long, List<KitBanner>> map) {
        return map.entrySet().stream()
            .map(entry -> {
                Long kitId = entry.getKey();
                List<KitBanner> banners = entry.getValue();

                String smallBanner = banners.stream()
                    .filter(b -> b.getSize() == ImageSize.SMALL)
                    .findFirst()
                    .map(KitBanner::getPath)
                    .orElse(null);

                String largeBanner = banners.stream()
                    .filter(b -> b.getSize() == ImageSize.LARGE)
                    .findFirst()
                    .map(KitBanner::getPath)
                    .orElse(null);

                return new Result(kitId,
                    createFileDownloadLinkPort.createDownloadLink(smallBanner, EXPIRY_DURATION),
                    createFileDownloadLinkPort.createDownloadLink(largeBanner, EXPIRY_DURATION));
            })
            .toList();
    }
}
