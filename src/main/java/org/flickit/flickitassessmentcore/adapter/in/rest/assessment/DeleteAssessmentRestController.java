package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.DeleteAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteAssessmentRestController {

    private final DeleteAssessmentUseCase useCase;

    @DeleteMapping("/assessments/{id}")
    public ResponseEntity<Void> deleteAssessmentById(@PathVariable("id") UUID id) {
        useCase.deleteAssessment(new DeleteAssessmentUseCase.Param(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
