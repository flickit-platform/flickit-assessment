package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentCommand;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CreateAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("{spaceId}/assessments")
public class CreateAssessmentRestController {
    private final CreateAssessmentUseCase useCase;

    @PostMapping
    public ResponseEntity<CreateAssessmentResponseDto> createAssessment(@RequestBody CreateAssessmentRequestDto requestDto,
                                                                        @PathVariable("spaceId") Long spaceId) {

        CreateAssessmentCommand createAssessmentCommand = CreateAssessmentRequestMapper.mapWebModelToCommand(requestDto, spaceId);

        CreateAssessmentResponseDto responseDto =
            CreateAssessmentResponseMapper.mapToResponseDto(
                useCase.createAssessment(createAssessmentCommand)
            );

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
