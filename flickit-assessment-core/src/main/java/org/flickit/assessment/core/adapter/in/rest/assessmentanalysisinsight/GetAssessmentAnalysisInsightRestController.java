package org.flickit.assessment.core.adapter.in.rest.assessmentanalysisinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentanalysisinsight.GetAssessmentAnalysisInsightUseCase;
import org.flickit.assessment.core.application.port.in.assessmentanalysisinsight.GetAssessmentAnalysisInsightUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentAnalysisInsightRestController {

    private final GetAssessmentAnalysisInsightUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/analysis")
    public ResponseEntity<Result> getAssessmentAnalysisInsight(@PathVariable("assessmentId")UUID assessmentId,
                                                               @RequestParam("type") String type) {
        UUID currentUserId = userContext.getUser().id();
        var result = useCase.getAssessmentAnalysisInsight(toParam(assessmentId, type, currentUserId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private GetAssessmentAnalysisInsightUseCase.Param toParam(UUID assessmentId, String type, UUID currentUserId) {
        return new GetAssessmentAnalysisInsightUseCase.Param(assessmentId, type, currentUserId);
    }
}
