package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class CreateAssessmentRestController {

    private final CreateAssessmentUseCase useCase;

    @PostMapping("/assessments")
    public ResponseEntity<CreateAssessmentResponseDto> createAssessment(@RequestBody CreateAssessmentRequestDto request) {
        CreateAssessmentResponseDto response = toResponseDto(useCase.createAssessment(toCommand(request)));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Param toCommand(CreateAssessmentRequestDto requestDto) {
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
