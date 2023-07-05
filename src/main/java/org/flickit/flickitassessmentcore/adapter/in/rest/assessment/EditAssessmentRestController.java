package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.EditAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EditAssessmentRestController {

    private final EditAssessmentUseCase useCase;

    @PutMapping("/assessments")
    public ResponseEntity<EditAssessmentResponseDto> editAssessment(@RequestBody EditAssessmentRequestDto request) {
        EditAssessmentUseCase.Result result = useCase.editAssessment(toParam(request));
        return new ResponseEntity<>(new EditAssessmentResponseDto(result.id()), HttpStatus.OK);
    }

    private EditAssessmentUseCase.Param toParam(EditAssessmentRequestDto request) {
        return new EditAssessmentUseCase.Param(
            request.id(),
            request.title(),
            request.assessmentKitId(),
            request.colorId()
        );
    }
}
