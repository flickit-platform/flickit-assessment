package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.GetCommentListUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetCommentListService implements GetCommentListUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadEvidencesPort loadEvidencesPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

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
        return items.stream()
            .map(e -> {
            var isCreator = Objects.equals(e.createdBy().id(), param.getCurrentUserId());
                var resolvable = isResolvable(param.getAssessmentId(), e.createdBy().id(), param.getCurrentUserId());

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

    private boolean isResolvable(UUID assessmentId, UUID commenterId, UUID currentUserId) {
        return commenterId.equals(currentUserId)
            ? assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, RESOLVE_OWN_COMMENT)
            : assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, RESOLVE_COMMENT);
    }

    private User addPictureLinkToUser(LoadEvidencesPort.User user) {
        return new User(user.id(),
            user.displayName(),
            createFileDownloadLinkPort.createDownloadLink(user.pictureLink(), EXPIRY_DURATION));
    }
}
