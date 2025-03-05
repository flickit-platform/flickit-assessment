package org.flickit.assessment.core.adapter.in.rest.insight.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.adapter.in.rest.insight.assessment.GetAssessmentOverallInsightResponseDto.InsightDetail;
import org.flickit.assessment.core.application.domain.insight.Insight;
import org.flickit.assessment.core.application.port.in.insight.assessment.GetAssessmentOverallInsightUseCase;
import org.flickit.assessment.core.application.port.in.insight.assessment.GetAssessmentOverallInsightUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentOverallInsightRestController {

    private final GetAssessmentOverallInsightUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessments/{assessmentId}/overall-insight")
    ResponseEntity<GetAssessmentOverallInsightResponseDto> getAssessmentInsight(@PathVariable("assessmentId") UUID assessmentId) {
        var currentUserId = userContext.getUser().id();
        var result = toResponse(useCase.getAssessmentInsight(toParam(assessmentId, currentUserId)));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }

    private GetAssessmentOverallInsightResponseDto toResponse(Insight insight) {
        return new GetAssessmentOverallInsightResponseDto(toInsightDetail(insight.defaultInsight()),
            toInsightDetail(insight.assessorInsight()),
            insight.editable(),
            insight.approved());
    }

    private InsightDetail toInsightDetail(Insight.InsightDetail insightDetail) {
        return insightDetail != null
            ? new InsightDetail(insightDetail.insight(), insightDetail.creationTime(), insightDetail.isValid())
            : null;
    }
}
