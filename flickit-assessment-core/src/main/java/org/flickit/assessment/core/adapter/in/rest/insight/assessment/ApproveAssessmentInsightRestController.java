package org.flickit.assessment.core.adapter.in.rest.insight.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.insight.assessment.ApproveAssessmentInsightUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApproveAssessmentInsightRestController {

    private final ApproveAssessmentInsightUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/approve-overall-insight")
    public ResponseEntity<Void> approveAssessmentInsight(@PathVariable("assessmentId") UUID assessmentId) {
        var currentUserId = userContext.getUser().id();
        useCase.approveAssessmentInsight(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ApproveAssessmentInsightUseCase.Param toParam(UUID assessmentId, UUID currentUserId) {
        return new ApproveAssessmentInsightUseCase.Param(assessmentId, currentUserId);
    }
}
