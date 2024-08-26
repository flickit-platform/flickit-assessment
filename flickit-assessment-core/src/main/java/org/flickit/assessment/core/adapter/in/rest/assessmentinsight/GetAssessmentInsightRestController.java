package org.flickit.assessment.core.adapter.in.rest.assessmentinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentinsight.GetAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinsight.GetAssessmentInsightUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentInsightRestController {

    private final GetAssessmentInsightUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessments/{assessment_id}/insights")
    ResponseEntity<GetAssessmentInsightResponseDto> getAssessmentInsights(@PathVariable("assessment_id") UUID assessmentId) {
        var result = useCase.getAssessmentInsight(toParam(assessmentId, userContext.getUser().id()));
        return new ResponseEntity<>(toResponse(result), HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, UUID currentUserId) {
        return new Param(assessmentId, currentUserId);
    }

    private GetAssessmentInsightResponseDto toResponse(Result result) {
        return new GetAssessmentInsightResponseDto(
            new GetAssessmentInsightResponseDto.DefaultInsight(result.defaultInsight().insight()),
            (result.assessorInsight() != null) ? new GetAssessmentInsightResponseDto.AssessorInsight(result.assessorInsight().insight(),
                result.assessorInsight().creationTime(),
                result.assessorInsight().isValid()) : null,
            result.editable()
        );
    }
}
