package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentInvite;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.AssessmentUserRoleItem;
import org.flickit.assessment.core.application.domain.FullUser;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetGraphicalReportUsersUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvite.LoadAssessmentInviteeListPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentUsersPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Override
    public Result getGraphicalReportUsers(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var roleIds = Stream.of(AssessmentUserRole.values())
            .filter(e -> e.hasAccess(AssessmentPermission.VIEW_GRAPHICAL_REPORT))
            .map(AssessmentUserRole::getId)
            .toList();

        var fullUsers = loadAssessmentUsersPort.loadAll(param.getAssessmentId(), roleIds);
        var invitees = loadAssessmentInviteeListPort.loadAll(param.getAssessmentId(), roleIds);

        var fullUserIds = fullUsers.stream()
            .map(FullUser::getId)
            .toList();

        var users = Collections.<Result.GraphicalReportUser>emptyList();
        if (!fullUsers.isEmpty()) {
            var UserIdToUserRoleItemMap = buildUserIdToUserRoleItemMap(param, fullUserIds);
            users = fullUsers.stream()
                .map(e -> toGraphicalReportUser(param, e, UserIdToUserRoleItemMap.get(e.getId())))
                .toList();
        }

        var inviteeUsers = Collections.<Result.GraphicalReportInvitee>emptyList();
        if (!invitees.isEmpty()) {
            inviteeUsers = invitees.stream()
                .filter(AssessmentInvite::isNotExpired)
                .map(e -> toGraphicalReportInvitee(param, e))
                .toList();
        }

        return new Result(users, inviteeUsers);
    }

    private Map<UUID, AssessmentUserRoleItem> buildUserIdToUserRoleItemMap(Param param, List<UUID> fullUserIds) {
        var allUsersRoleItems = loadUserRoleForAssessmentPort.loadRoleItems(param.getAssessmentId(), fullUserIds);
        return allUsersRoleItems.stream()
            .collect(Collectors.toMap(AssessmentUserRoleItem::getUserId, Function.identity()));
    }

    private Result.GraphicalReportUser toGraphicalReportUser(Param param, FullUser e, AssessmentUserRoleItem assessmentUserRoleItem) {
        String pictureLink = null;
        if (e.getPicturePath() != null && !e.getPicturePath().trim().isBlank())
            pictureLink = createFileDownloadLinkPort.createDownloadLink(e.getPicturePath(), EXPIRY_DURATION);

        return new Result.GraphicalReportUser(e.getId(),
            e.getEmail(),
            e.getDisplayName(),
            pictureLink,
            isDeletable(param, assessmentUserRoleItem.getCreatedBy(), assessmentUserRoleItem.getRole()));
    }

    private Result.GraphicalReportInvitee toGraphicalReportInvitee(Param param, AssessmentInvite invite) {
        return new Result.GraphicalReportInvitee(invite.getEmail(),
            isDeletable(param, invite.getCreatedBy(), invite.getRole()));
    }

    private boolean isDeletable(Param param, UUID roleCreator, AssessmentUserRole role) {
        return (role.equals(AssessmentUserRole.REPORT_VIEWER)
            && (roleCreator.equals(param.getCurrentUserId())
            || assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.DELETE_USER_ASSESSMENT_ROLE)));
    }
}
