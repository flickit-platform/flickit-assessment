package org.flickit.assessment.core.adapter.in.rest.assessmentinvite;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentinvite.UpdateAssessmentInviteUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinvite.UpdateAssessmentInviteUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateAssessmentInviteRestController {

    private final UserContext userContext;
    private final UpdateAssessmentInviteUseCase useCase;

    @PutMapping("/assessment-invites/{id}")
    public ResponseEntity<Void> updateInvite(@PathVariable("id") UUID inviteId,
                                             @RequestBody UpdateAssessmentInviteRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.updateInvite(toParam(inviteId, requestDto, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    Param toParam(UUID inviteId, UpdateAssessmentInviteRequestDto requestDto, UUID currentUserId) {
        return new Param(inviteId, requestDto.roleId(), currentUserId);
    }
}
