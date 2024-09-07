package org.flickit.assessment.core.adapter.in.rest.assessmentanalysis;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.CreateAssessmentAnalysisUseCase;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.CreateAssessmentAnalysisUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAssessmentAnalysisRestController {

    private final CreateAssessmentAnalysisUseCase createAssessmentAnalysisUseCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/analysis")
    public ResponseEntity<Result> createAssessmentAiAnalysis(@PathVariable("assessmentId") UUID assessmentId,
                                                             @RequestBody CreateAssessmentAnalysisRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        createAssessmentAnalysisUseCase.createAiAnalysis(toParam(assessmentId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, CreateAssessmentAnalysisRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.type(), currentUserId);
    }
}
