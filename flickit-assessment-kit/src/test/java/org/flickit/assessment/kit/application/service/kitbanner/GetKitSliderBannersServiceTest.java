package org.flickit.assessment.kit.application.service.kitbanner;

import org.flickit.assessment.common.application.domain.kit.ImageSize;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.port.in.kitbanner.GetKitSliderBannersUseCase;
import org.flickit.assessment.kit.application.port.out.kitbanner.LoadKitBannersPort;
import org.flickit.assessment.kit.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.kit.test.fixture.application.KitBannerMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetKitSliderBannersServiceTest {

    @InjectMocks
    private GetKitSliderBannersService service;

    @Mock
    private LoadKitBannersPort loadKitBannersPort;

    @Mock
    private CreateFileDownloadLinkPort createDownloadLinkPort;

    @Test
    void getKitSliderBanners_whenParamsAreValid_thenReturnSliderBanners() {
        var param = createParam(GetKitSliderBannersUseCase.Param.ParamBuilder::build);
        var sliderBanners = List.of(KitBannerMother.createWithIdAndSize(1L, ImageSize.SMALL),
            KitBannerMother.createWithIdAndSize(1L, ImageSize.LARGE),
            KitBannerMother.createWithIdAndSize(2L, ImageSize.SMALL));

        when(loadKitBannersPort.loadSliderBanners(KitLanguage.valueOf(param.getLang()))).thenReturn(sliderBanners);
        when(createDownloadLinkPort.createDownloadLink(anyString(), any())).thenReturn("path/to/minio");

        var result = service.getSliderBanners(param);
        int size = result.size();

        assertThat(size).isEqualTo(2);
        var banner1 = result.stream()
            .filter(b -> b.kitId() == 1)
            .findFirst().orElseThrow();
        assertNotNull(banner1.smallBanner());
        assertNotNull(banner1.largeBanner());

        var banner2 = result.stream()
            .filter(b -> b.kitId() == 2)
            .findFirst().orElseThrow();
        assertNotNull(banner2.smallBanner());
        assertNull(banner2.largeBanner());

        verify(createDownloadLinkPort, times(sliderBanners.size())).createDownloadLink(anyString(), any());
    }

    private GetKitSliderBannersUseCase.Param createParam(Consumer<GetKitSliderBannersUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetKitSliderBannersUseCase.Param.ParamBuilder paramBuilder() {
        return GetKitSliderBannersUseCase.Param.builder()
            .lang("FA");
    }
}
