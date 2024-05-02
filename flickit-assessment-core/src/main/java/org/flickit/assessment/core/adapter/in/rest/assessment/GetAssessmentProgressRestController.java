package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentProgressUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentProgressRestController {

    private final GetAssessmentProgressUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/progress")
    public ResponseEntity<GetAssessmentProgressResponseDto> getAssessmentProgress(@PathVariable("assessmentId") UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponse(useCase.getAssessmentProgress(toParam(assessmentId, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetAssessmentProgressUseCase.Param toParam(UUID assessmentId, UUID currentUserId) {
        return new GetAssessmentProgressUseCase.Param(assessmentId, currentUserId);
    }

    private GetAssessmentProgressResponseDto toResponse(GetAssessmentProgressUseCase.Result result) {
        return new GetAssessmentProgressResponseDto(result.id(), result.answersCount(), result.questionsCount());
    }
}
