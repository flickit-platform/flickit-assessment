package org.flickit.assessment.core.application.service.assessmentinvite;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentInvite;
import org.flickit.assessment.core.application.port.in.assessmentinvite.GetAssessmentInviteeListUseCase;
import org.flickit.assessment.core.application.port.in.assessmentinvite.GetAssessmentInviteeListUseCase.Result.Role;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentInviteeListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_INVITEE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentInviteeListService implements GetAssessmentInviteeListUseCase {

    private final LoadAssessmentInviteeListPort loadAssessmentInviteeListPort;
    private final AssessmentAccessChecker assessmentAccessChecker;

    @Override
    public PaginatedResponse<Result> getInvitees(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INVITEE_LIST))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var inviteePaginatedResponse = loadAssessmentInviteeListPort.loadByAssessmentId(param.getAssessmentId(), param.getSize(), param.getPage());

        var items = inviteePaginatedResponse.getItems().stream()
            .map(this::toResult)
            .toList();

        return new PaginatedResponse<>(items,
            inviteePaginatedResponse.getPage(),
            inviteePaginatedResponse.getSize(),
            inviteePaginatedResponse.getSort(),
            inviteePaginatedResponse.getOrder(),
            inviteePaginatedResponse.getTotal());
    }

    private Result toResult(AssessmentInvite invitee) {
        return new Result(invitee.getId(),
            invitee.getEmail(),
            new Role(invitee.getRole().getId(), invitee.getRole().getTitle()),
            invitee.getExpirationTime(),
            invitee.getCreationTime());
    }
}
