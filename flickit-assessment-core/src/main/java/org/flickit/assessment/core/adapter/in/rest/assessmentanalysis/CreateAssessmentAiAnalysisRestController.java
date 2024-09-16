package org.flickit.assessment.core.adapter.in.rest.assessmentanalysis;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.CreateAssessmentAiAnalysisUseCase;
import org.flickit.assessment.core.application.port.in.assessmentanalysis.CreateAssessmentAiAnalysisUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAssessmentAiAnalysisRestController {

    private final CreateAssessmentAiAnalysisUseCase createAssessmentAiAnalysisUseCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/analysis")
    public ResponseEntity<Void> createAssessmentAiAnalysis(@PathVariable("assessmentId") UUID assessmentId,
                                                             @RequestBody CreateAssessmentAiAnalysisRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        createAssessmentAiAnalysisUseCase.createAiAnalysis(toParam(assessmentId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, CreateAssessmentAiAnalysisRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.type(), currentUserId);
    }
}
