package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.evidence.GetCommentListUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_COMMENT_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCommentListService implements GetCommentListUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadEvidencesPort loadEvidencesPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;
    private final LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Override
    public PaginatedResponse<CommentListItem> getCommentList(Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_COMMENT_LIST))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var portResult = loadEvidencesPort.loadNotDeletedComments(
            param.getQuestionId(),
            param.getAssessmentId(),
            param.getPage(),
            param.getSize());

        return new PaginatedResponse<>(
            enrichEvidenceItems(portResult.getItems(), param),
            param.getPage(),
            param.getSize(),
            portResult.getSort(),
            portResult.getOrder(),
            portResult.getTotal());
    }

    private List<CommentListItem> enrichEvidenceItems(List<LoadEvidencesPort.EvidenceListItem> items, Param param) {
        var role = loadUserRoleForAssessmentPort.load(param.getAssessmentId(), param.getCurrentUserId())
            .orElseThrow(() -> new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED));

        return items.stream().map(e -> {
            var isCreator = Objects.equals(e.createdBy().id(), param.getCurrentUserId());
            var resolvable = AssessmentUserRole.MANAGER.equals(role) || AssessmentUserRole.ASSESSOR.equals(role) ||
                (AssessmentUserRole.ASSOCIATE.equals(role) && isCreator);

            return new CommentListItem(
                e.id(),
                e.description(),
                e.lastModificationTime(),
                e.attachmentsCount(),
                addPictureLinkToUser(e.createdBy()),
                isCreator,
                isCreator,
                resolvable);
        }).toList();
    }

    private User addPictureLinkToUser(LoadEvidencesPort.User user) {
        return new User(user.id(),
            user.displayName(),
            createFileDownloadLinkPort.createDownloadLink(user.pictureLink(), EXPIRY_DURATION));
    }
}
