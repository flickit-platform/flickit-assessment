package org.flickit.assessment.core.adapter.in.rest.assessmentanalysis;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.CreateAssessmentAnalysisUseCase;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.CreateAssessmentAnalysisUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAssessmentAnalysisRestController {

    private final CreateAssessmentAnalysisUseCase createAssessmentAnalysisUseCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/analysis")
    public ResponseEntity<Result> createAssessmentAiAnalysis(@PathVariable("assessmentId") UUID assessmentId, @RequestParam("type") Integer type) {
        var currentUserId = userContext.getUser().id();
        var result = createAssessmentAnalysisUseCase.createAiAnalysis(toParam(assessmentId, type, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, Integer type, UUID currentUserId) {
        return new Param(assessmentId, type, currentUserId);
    }
}
