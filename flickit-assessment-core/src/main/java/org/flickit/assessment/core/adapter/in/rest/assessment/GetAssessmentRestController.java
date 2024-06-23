package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.adapter.in.rest.assessment.GetAssessmentResponseDto.UserResponseDto;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentRestController {

    private final GetAssessmentUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}")
    public ResponseEntity<GetAssessmentResponseDto> getAssessment(@PathVariable("assessmentId") UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
        var response = toResponse(useCase.getAssessment(new Param(assessmentId, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private GetAssessmentResponseDto toResponse(Result result) {
        return new GetAssessmentResponseDto(
            result.id(),
            result.title(),
            new GetAssessmentResponseDto.SpaceResponseDto(result.space().getId(), result.space().getTitle()),
            new GetAssessmentResponseDto.KitResponseDto(result.kit().getId(), result.kit().getTitle()),
            result.creationTime(),
            result.lastModificationTime(),
            new UserResponseDto(result.createdBy().getId(), result.createdBy().getDisplayName()),
            result.maturityLevel(),
            result.isCalculateValid());
    }
}
