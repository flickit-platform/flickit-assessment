package org.flickit.assessment.core.adapter.in.rest.assessmentinvitee;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.EditAssessmentInviteeRoleUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.EditAssessmentInviteeRoleUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EditAssessmentInviteeRoleRestController {

    private final UserContext userContext;
    private final EditAssessmentInviteeRoleUseCase useCase;

    @PostMapping("/assessments/invitees/{id}")
    public ResponseEntity<Void> inviteSpaceMember(@PathVariable("id") UUID inviteId,
                                                  @RequestBody EditAssessmentInviteeRoleRequestDto request) {
        var currentUserId = userContext.getUser().id();
        useCase.editRole(toParam(inviteId, request, currentUserId));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    Param toParam(UUID inviteId, EditAssessmentInviteeRoleRequestDto requestDto, UUID currentUserId) {
        return new Param(inviteId, requestDto.roleId(), currentUserId);
    }
}
