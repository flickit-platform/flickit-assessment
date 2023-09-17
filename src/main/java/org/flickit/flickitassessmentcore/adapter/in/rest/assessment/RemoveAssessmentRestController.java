package org.flickit.flickitassessmentcore.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.RemoveAssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class RemoveAssessmentRestController {

    private final RemoveAssessmentUseCase useCase;

    @DeleteMapping("/assessments/{id}")
    public ResponseEntity removeAssessmentById(@PathVariable("id") UUID id) {
        useCase.removeAssessment(new RemoveAssessmentUseCase.Param(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
