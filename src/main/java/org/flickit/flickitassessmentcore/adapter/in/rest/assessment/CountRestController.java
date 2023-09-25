package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CountUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CountUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CountUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CountRestController {

    private final CountUseCase useCase;

    @GetMapping("/assessments/counters")
    public ResponseEntity<CountResponseDto> count(
        @PathVariable("assessmentKitId") Long assessmentKitId,
        @PathVariable("includeDeleted") Boolean includeDeleted,
        @PathVariable("includeNotDeleted") Boolean includeNotDeleted
    ) {
        var result = useCase.count(toParam(assessmentKitId, includeDeleted, includeNotDeleted));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private Param toParam(Long assessmentKitId, Boolean includeDeleted, Boolean includeNotDeleted) {
        return new Param(assessmentKitId, includeDeleted, includeNotDeleted);
    }

    private CountResponseDto toResponseDto(Result result) {
        return new CountResponseDto(result.count());
    }

}
