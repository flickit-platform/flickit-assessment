package org.flickit.assessment.core.adapter.in.rest.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.InviteAssessmentUserUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.InviteAssessmentUserUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InviteAssessmentUserRestController {

    private final UserContext userContext;
    private final InviteAssessmentUserUseCase useCase;

    @PostMapping("/assessments/{id}/invite")
    public ResponseEntity<Void> inviteSpaceMember(@PathVariable("id") UUID id,
                                                  @RequestBody InviteAssessmentUserRequestDto request) {
        var currentUserId = userContext.getUser().id();
        useCase.inviteUser(toParam(id, request, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    Param toParam(UUID id, InviteAssessmentUserRequestDto requestDto, UUID currentUserId) {
        return new Param(id, requestDto.email(), requestDto.roleId() , currentUserId);
    }
}
