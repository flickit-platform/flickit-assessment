package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
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
        UUID response = useCase.createAssessment(toCommand(request));
        return new ResponseEntity<>(toResponseDto(response), HttpStatus.CREATED);
    }

    private CreateAssessmentCommand toCommand(CreateAssessmentRequestDto requestDto) {
        return new CreateAssessmentCommand(
            requestDto.spaceId(),
            requestDto.title(),
            requestDto.assessmentKitId(),
            requestDto.colorId()
        );
    }

    private CreateAssessmentResponseDto toResponseDto(UUID id) {
        return new CreateAssessmentResponseDto(id);
    }
}
