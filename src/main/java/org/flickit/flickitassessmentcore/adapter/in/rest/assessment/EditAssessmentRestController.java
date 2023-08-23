package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.EditAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EditAssessmentRestController {

    private final EditAssessmentUseCase useCase;

    @PutMapping("/assessments/{id}")
    public ResponseEntity<EditAssessmentResponseDto> editAssessment(@PathVariable("id") UUID id,
                                                                    @RequestBody EditAssessmentRequestDto request) {
        EditAssessmentUseCase.Param param = toParam(id, request);
        EditAssessmentUseCase.Result result = useCase.editAssessment(param);
        return new ResponseEntity<>(new EditAssessmentResponseDto(result.id()), HttpStatus.OK);
    }

    private EditAssessmentUseCase.Param toParam(UUID id, EditAssessmentRequestDto request) {
        return new EditAssessmentUseCase.Param(
            id,
            request.title(),
            request.assessmentKitId(),
            request.colorId()
        );
    }
}
