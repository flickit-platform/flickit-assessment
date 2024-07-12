package org.flickit.assessment.core.application.service.evidence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceListUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencesPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_EVIDENCE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceListService implements GetEvidenceListUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadEvidencesPort loadEvidencesPort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public PaginatedResponse<EvidenceListItem> getEvidenceList(GetEvidenceListUseCase.Param param) {
        if (!assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_EVIDENCE_LIST))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var portResult = loadEvidencesPort.loadNotDeletedEvidences(
            param.getQuestionId(),
            param.getAssessmentId(),
            param.getPage(),
            param.getSize()
        );

        return new PaginatedResponse<>(
            addPictureLink(portResult.getItems()),
            param.getPage(),
            param.getSize(),
            portResult.getSort(),
            portResult.getOrder(),
            portResult.getTotal()
        );
    }

    private List<EvidenceListItem> addPictureLink(List<EvidenceListItem> items) {
        return items.stream().map(e -> new EvidenceListItem(
            e.id(),
            e.description(),
            e.type(),
            e.lastModificationTime(),
            e.attachmentsCount(),
            addPictureLinkToUser(e.createdBy())
        )).toList();
    }

    private User addPictureLinkToUser(User user) {
        return new User(user.id(),
            user.displayName(),
            createFileDownloadLinkPort.createDownloadLink(user.pictureLink(), EXPIRY_DURATION));
    }
}
