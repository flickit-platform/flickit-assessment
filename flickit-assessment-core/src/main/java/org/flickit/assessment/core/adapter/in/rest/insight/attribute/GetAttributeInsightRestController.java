package org.flickit.assessment.core.adapter.in.rest.insight.attribute;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.adapter.in.rest.insight.attribute.GetAttributeInsightResponseDto.InsightDetail;
import org.flickit.assessment.core.application.domain.insight.Insight;
import org.flickit.assessment.core.application.port.in.insight.attribute.GetAttributeInsightUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAttributeInsightRestController {

    private final GetAttributeInsightUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/attributes/{attributeId}/insight")
    public ResponseEntity<GetAttributeInsightResponseDto> getAttributeInsight(
        @PathVariable("assessmentId") UUID assessmentId,
        @PathVariable("attributeId") Long attributeId) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponse(useCase.getInsight(toParam(assessmentId, attributeId, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetAttributeInsightUseCase.Param toParam(UUID assessmentId, Long attributeId, UUID currentUserId) {
        return new GetAttributeInsightUseCase.Param(assessmentId, attributeId, currentUserId);
    }

    private GetAttributeInsightResponseDto toResponse(Insight insight) {
        return new GetAttributeInsightResponseDto(toInsightDetail(insight.getDefaultInsight()),
            toInsightDetail(insight.getAssessorInsight()),
            insight.isEditable(),
            insight.getApproved());
    }

    private InsightDetail toInsightDetail(Insight.InsightDetail insightDetail) {
        return insightDetail != null
            ? new InsightDetail(insightDetail.getInsight(), insightDetail.getCreationTime(), insightDetail.isValid())
            : null;
    }
}
