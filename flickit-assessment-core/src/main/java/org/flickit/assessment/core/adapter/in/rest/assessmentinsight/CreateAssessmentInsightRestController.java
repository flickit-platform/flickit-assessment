package org.flickit.assessment.core.adapter.in.rest.assessmentinsight;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentinsight.CreateAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinsight.CreateAssessmentInsightUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateAssessmentInsightRestController {

    private final CreateAssessmentInsightUseCase useCase;
    private final UserContext userContext;

    @PostMapping("assessments/{assessmentId}/insights")
    ResponseEntity<Void> createAssessmentInsightOld(@PathVariable("assessmentId") UUID assessmentId,
                                                    @RequestBody CreateAssessmentInsightRequestDto requestDto) {
        useCase.createAssessmentInsight(toParam(assessmentId, requestDto, userContext.getUser().id()));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("assessments/{assessmentId}/overall-insights")
    ResponseEntity<Void> createAssessmentInsight(@PathVariable("assessmentId") UUID assessmentId,
                                                 @RequestBody CreateAssessmentInsightRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.createAssessmentInsight(toParam(assessmentId, requestDto, currentUserId));

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(UUID assessmentId, CreateAssessmentInsightRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId, requestDto.insight(), currentUserId);
    }
}
