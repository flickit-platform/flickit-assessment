package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.UpdateAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAssessmentRestController {

    private final UpdateAssessmentUseCase useCase;

    @PutMapping("/assessments/{id}")
    public ResponseEntity<UpdateAssessmentResponseDto> updateAssessment(@PathVariable("id") UUID id,
                                                                        @RequestBody UpdateAssessmentRequestDto requestDto) {
        UpdateAssessmentResponseDto responseDto = toResponseDto(useCase.updateAssessment(toParam(id, requestDto)));
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    private UpdateAssessmentUseCase.Param toParam(UUID id, UpdateAssessmentRequestDto request) {
        return new UpdateAssessmentUseCase.Param(
            id,
            request.title(),
            request.colorId()
        );
    }

    private UpdateAssessmentResponseDto toResponseDto(UpdateAssessmentUseCase.Result result) {
        return new UpdateAssessmentResponseDto(result.id());
    }
}
