package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.MoveAssessmentUseCase;
import org.flickit.assessment.core.application.port.in.assessment.MoveAssessmentUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MoveAssessmentRestController {

    private final MoveAssessmentUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/move")
    public ResponseEntity<Void> moveAssessment(@PathVariable("assessmentId") UUID assessmentId,
                                               @RequestBody MoveAssessmentRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.moveAssessment(toParam(assessmentId, requestDto, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(UUID assessmentId, MoveAssessmentRequestDto requestDto, UUID currentUserId) {
        return new Param(assessmentId,
            requestDto.targetSpaceId(),
            currentUserId);
    }
}
