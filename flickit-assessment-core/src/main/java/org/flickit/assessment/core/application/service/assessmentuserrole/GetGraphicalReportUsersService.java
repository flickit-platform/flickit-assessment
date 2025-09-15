package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentInvite;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetGraphicalReportUsersUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentInviteeListPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentUsersPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_ACCESS_TO_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class GetGraphicalReportUsersService implements GetGraphicalReportUsersUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentUsersPort loadAssessmentUsersPort;
    private final LoadAssessmentInviteeListPort loadAssessmentInviteeListPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public Result getGraphicalReportUsers(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();

        var reportUsers = loadAssessmentUsersPort.loadAll(param.getAssessmentId(), roleIds);
        var invitees = loadAssessmentInviteeListPort.loadAll(param.getAssessmentId(), roleIds);

        if (reportUsers.isEmpty() && invitees.isEmpty())
            return new Result(List.of(), List.of() );

        var isAuthorized = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.DELETE_USER_ASSESSMENT_ROLE);

        var users = Collections.<Result.GraphicalReportUser>emptyList();
        if (!reportUsers.isEmpty()) {
            users = reportUsers.stream()
                .map(e -> toGraphicalReportUser(e, param.getCurrentUserId(), isAuthorized))
                .toList();
        }

        var inviteeUsers = Collections.<Result.GraphicalReportInvitee>emptyList();
        if (!invitees.isEmpty()) {
            inviteeUsers = invitees.stream()
                .filter(AssessmentInvite::isNotExpired)
                .map(e -> toGraphicalReportInvitee(e, param.getCurrentUserId(), isAuthorized))
                .toList();
        }

        return new Result(users, inviteeUsers);
    }

    private Result.GraphicalReportUser toGraphicalReportUser(LoadAssessmentUsersPort.ReportUser user, UUID currentUserId, boolean isAuthorized) {
        String pictureLink = null;
        if (user.picturePath() != null && !user.picturePath().trim().isBlank())
            pictureLink = createFileDownloadLinkPort.createDownloadLink(user.picturePath(), EXPIRY_DURATION);

        return new Result.GraphicalReportUser(user.id(),
            user.email(),
            user.displayName(),
            pictureLink,
            isDeletable(user.role(), user.createdBy(), currentUserId, isAuthorized));
    }

    private Result.GraphicalReportInvitee toGraphicalReportInvitee(AssessmentInvite invite, UUID currentUserId, boolean isAuthorized) {
        return new Result.GraphicalReportInvitee(invite.getEmail(),
            isDeletable(invite.getRole(), invite.getCreatedBy(), currentUserId, isAuthorized));
    }

    private boolean isDeletable(AssessmentUserRole role, UUID roleCreator, UUID currentUserId, boolean isAuthorized) {
        return (role.equals(AssessmentUserRole.REPORT_VIEWER)
            && (roleCreator.equals(currentUserId) || isAuthorized));
    }
}
