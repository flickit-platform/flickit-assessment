package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitLanguagesUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitLanguagesUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetKitLanguagesRestController {

    private final GetKitLanguagesUseCase getKitLanguagesUseCase;

    @GetMapping("/kit-languages")
    public ResponseEntity<Result> getKitLanguages() {
        return new ResponseEntity<>(getKitLanguagesUseCase.getKitLanguages(), HttpStatus.OK);
    }
}
