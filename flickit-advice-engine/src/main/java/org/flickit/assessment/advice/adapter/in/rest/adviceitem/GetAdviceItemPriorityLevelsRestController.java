package org.flickit.assessment.advice.adapter.in.rest.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemPriorityLevelsUseCase.Result;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemPriorityLevelsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetAdviceItemPriorityLevelsRestController {

    private final GetAdviceItemPriorityLevelsUseCase useCase;

    @GetMapping("/advice-item-priority-levels")
    public ResponseEntity<Result> getPriorityLevels() {
        return new ResponseEntity<>(useCase.getPriorityLevels(), HttpStatus.OK);
    }
}
