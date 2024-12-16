package org.flickit.assessment.advice.adapter.in.rest.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemImpactLevelsUseCase.Result;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemImpactLevelsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetAdviceItemImpactLevelsRestController {

    private final GetAdviceItemImpactLevelsUseCase useCase;

    @GetMapping("/advice-item-impact-levels")
    public ResponseEntity<Result> getImpactLevels() {
        return new ResponseEntity<>(useCase.getImpactLevels(), HttpStatus.OK);
    }
}
