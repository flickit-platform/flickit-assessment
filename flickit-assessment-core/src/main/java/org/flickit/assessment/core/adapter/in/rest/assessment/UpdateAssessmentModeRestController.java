package org.flickit.assessment.core.adapter.in.rest.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessment.UpdateAssessmentModeUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAssessmentModeRestController {

    private final UpdateAssessmentModeUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/assessments/{assessmentId}/mode")
    public ResponseEntity<Void> updateAssessmentMode(@PathVariable("assessmentId") UUID assessmentId,
                                                     @RequestBody UpdateAssessmentModeRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateAssessmentMode(toParam(assessmentId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateAssessmentModeUseCase.Param toParam(UUID assessmentId, UpdateAssessmentModeRequestDto requestDto, UUID currentUserId) {
        return new UpdateAssessmentModeUseCase.Param(assessmentId, requestDto.mode(), currentUserId);
    }
}
