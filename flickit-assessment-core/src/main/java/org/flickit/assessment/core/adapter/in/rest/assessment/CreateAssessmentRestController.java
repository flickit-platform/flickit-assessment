package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CreateAssessmentUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CreateAssessmentRestController {

    private final CreateAssessmentUseCase useCase;

    @PostMapping("/assessments")
    public ResponseEntity<CreateAssessmentResponseDto> createAssessment(@RequestBody CreateAssessmentRequestDto request) {
        CreateAssessmentResponseDto response = toResponseDto(useCase.createAssessment(toParam(request)));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toParam(CreateAssessmentRequestDto requestDto) {
        return new Param(
            requestDto.spaceId(),
            requestDto.title(),
            requestDto.assessmentKitId(),
            requestDto.colorId()
        );
    }

    private CreateAssessmentResponseDto toResponseDto(CreateAssessmentUseCase.Result result) {
        return new CreateAssessmentResponseDto(result.id());
    }
}
