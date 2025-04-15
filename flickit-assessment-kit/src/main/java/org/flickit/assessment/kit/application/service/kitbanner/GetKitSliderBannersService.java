package org.flickit.assessment.kit.application.service.kitbanner;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.kitbanner.GetKitSliderBannersUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitSliderBannersService implements GetKitSliderBannersUseCase {

    @Override
    public List<Result> getSliderBanners(Param param) {
        return List.of();
    }
}
