package org.flickit.assessment.advice.adapter.in.rest.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemCostLevelsUseCase;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemCostLevelsUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetAdviceItemCostLevelsRestController {

    private final GetAdviceItemCostLevelsUseCase useCase;

    @GetMapping("/advice-item-cost-levels")
    public ResponseEntity<Result> getCostLevels() {
        return new ResponseEntity<>(useCase.getCostLevels(), HttpStatus.OK);
    }
}
