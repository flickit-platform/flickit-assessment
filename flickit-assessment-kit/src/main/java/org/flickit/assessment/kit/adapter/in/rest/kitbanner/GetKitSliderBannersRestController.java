package org.flickit.assessment.kit.adapter.in.rest.kitbanner;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.kitbanner.GetKitSliderBannersUseCase;
import org.flickit.assessment.kit.application.port.in.kitbanner.GetKitSliderBannersUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GetKitSliderBannersRestController {

    private final GetKitSliderBannersUseCase useCase;

    @GetMapping("/assessment-kits-banners")
    public ResponseEntity<List<Result>> getBanners(@RequestParam("lang") String lang) {
        List<Result> banners = useCase.getSliderBanners(new Param(lang));
        return new ResponseEntity<>(banners, HttpStatus.OK);
    }
}
