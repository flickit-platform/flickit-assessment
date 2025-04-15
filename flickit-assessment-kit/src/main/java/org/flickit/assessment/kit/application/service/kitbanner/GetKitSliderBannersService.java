package org.flickit.assessment.kit.application.service.kitbanner;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.domain.KitBanner;
import org.flickit.assessment.kit.application.port.in.kitbanner.GetKitSliderBannersUseCase;
import org.flickit.assessment.kit.application.port.out.kitbanner.LoadKitBannerPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitSliderBannersService implements GetKitSliderBannersUseCase {

    private final LoadKitBannerPort loadKitBannerPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    @Override
    public List<Result> getSliderBanners(Param param) {
        var portResult = loadKitBannerPort.loadSliderBanners(KitLanguage.valueOf(param.getLang()));

        return portResult.stream()
                .map(this::toResult)
                .toList();
    }

    Result toResult(KitBanner kitBanner) {
        return new Result(kitBanner.getKitId(),
                createFileDownloadLinkPort.createDownloadLink(kitBanner.getPath(), EXPIRY_DURATION)
        );
    }
}
