package org.flickit.assessment.kit.application.service.kitbanner;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.kit.application.port.in.kitbanner.GetKitSliderBannersUseCase;
import org.flickit.assessment.kit.application.port.out.kitbanner.LoadKitBannerPort;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetKitSliderBannersServiceTest {

    @InjectMocks
    private GetKitSliderBannersService service;

    @Mock
    private LoadKitBannerPort loadKitBannersPort;

    @Mock
    private CreateFileDownloadLinkPort createDownloadLinkPort;

    @Test
    void getKitSliderBanners_whenParamsAreValid_thenReturnSliderBanners() {
        var param = createParam(GetKitSliderBannersUseCase.Param.ParamBuilder::build);
        var sliderBanners = List.of(KitBannerMother.create(), KitBannerMother.create());

        when(loadKitBannersPort.loadSliderBanners(KitLanguage.valueOf(param.getLang()))).thenReturn(sliderBanners);
        when(createDownloadLinkPort.createDownloadLink(anyString(), any())).thenReturn("path/to/minio");

        var result = service.getSliderBanners(param);

        assertThat(result)
            .zipSatisfy(sliderBanners, (actual, expected) -> {
                assertEquals(expected.getKitId(), actual.kitId());
                assertEquals("path/to/minio", actual.banner());
            });

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
