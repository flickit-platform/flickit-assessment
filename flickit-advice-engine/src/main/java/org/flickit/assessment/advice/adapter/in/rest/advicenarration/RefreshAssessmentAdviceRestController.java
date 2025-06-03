package org.flickit.assessment.advice.adapter.in.rest.advicenarration;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advicenarration.RefreshAssessmentAdviceUseCase;
import org.flickit.assessment.advice.application.port.in.advicenarration.RefreshAssessmentAdviceUseCase.Param;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RefreshAssessmentAdviceRestController {

    private final RefreshAssessmentAdviceUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessments/{assessmentId}/refresh-advice")
    ResponseEntity<Void> refreshAssessmentAdvice(@PathVariable("assessmentId") UUID assessmentId,
                                                 @RequestBody RefreshAssessmentAdviceRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.refreshAssessmentAdvice(toParam(assessmentId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, RefreshAssessmentAdviceRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId,
            requestDto.forceRegenerate(),
            currentUserId);
    }
}
