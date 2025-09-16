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
import java.util.UUID;
import java.util.stream.Stream;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_USER_ASSESSMENT_ROLE;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GRANT_ACCESS_TO_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.REPORT_VIEWER;

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

        var hasDeletePermission = assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), DELETE_USER_ASSESSMENT_ROLE);

        var users = loadAssessmentUsersPort.loadAll(param.getAssessmentId(), roleIds).stream()
            .map(e -> toGraphicalReportUser(e, param.getCurrentUserId(), hasDeletePermission))
            .toList();

        var inviteeUsers = loadAssessmentInviteeListPort.loadAll(param.getAssessmentId(), roleIds).stream()
            .filter(AssessmentInvite::isNotExpired)
            .map(e -> {
                boolean deletable = isDeletable(e.getRole(), e.getCreatedBy(), param.getCurrentUserId(), hasDeletePermission);
                return new Result.GraphicalReportInvitee(e.getEmail(), deletable);
            })
            .toList();

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

    private boolean isDeletable(AssessmentUserRole role, UUID roleCreator, UUID currentUserId, boolean isAuthorized) {
        return (REPORT_VIEWER == role && (roleCreator.equals(currentUserId) || isAuthorized));
    }
}
