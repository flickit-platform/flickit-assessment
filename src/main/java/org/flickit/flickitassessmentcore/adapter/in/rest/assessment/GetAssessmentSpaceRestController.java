package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentSpaceUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentSpaceUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentSpaceUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentSpaceRestController {

    private final GetAssessmentSpaceUseCase useCase;

    @GetMapping("/assessment/{assessmentId}/space")
    public ResponseEntity<GetAssessmentSpaceResponseDto> getAssessmentSpace(@PathVariable("assessmentId") UUID assessmentId) {
        var response = toResponse(useCase.getAssessmentSpace(new Param(assessmentId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetAssessmentSpaceResponseDto toResponse(Result result) {
        return new GetAssessmentSpaceResponseDto(result.spaceId());
    }
}
