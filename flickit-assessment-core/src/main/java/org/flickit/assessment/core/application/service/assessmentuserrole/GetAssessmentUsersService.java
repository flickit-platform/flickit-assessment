package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentUsersUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentUsersPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_USERS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentUsersService implements GetAssessmentUsersUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final AssessmentPermissionChecker assessmentPermissionChecker;
    private final LoadAssessmentUsersPort loadAssessmentUsersPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public PaginatedResponse<AssessmentUser> getAssessmentUsers(Param param) {
        if (!assessmentPermissionChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_USERS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentUserPaginatedResponse = loadAssessmentUsersPort.loadAssessmentUsers(toParam(param));
        List<AssessmentUser> items = assessmentUserPaginatedResponse.getItems().stream()
            .map(e -> {
                String pictureLink = null;
                if (e.picturePath() != null && !e.picturePath().trim().isBlank()) {
                    pictureLink = createFileDownloadLinkPort.createDownloadLink(e.picturePath(), EXPIRY_DURATION);
                }
                AssessmentUser.Role role = new AssessmentUser.Role(e.role().id(), e.role().title());
                return new AssessmentUser(e.id(),
                    e.email(),
                    e.displayName(),
                    pictureLink,
                    role,
                    e.editable());
            }).toList();

        return new PaginatedResponse<>(items,
            assessmentUserPaginatedResponse.getPage(),
            assessmentUserPaginatedResponse.getSize(),
            assessmentUserPaginatedResponse.getSort(),
            assessmentUserPaginatedResponse.getOrder(),
            assessmentUserPaginatedResponse.getTotal());
    }

    private LoadAssessmentUsersPort.Param toParam(Param param) {
        return new LoadAssessmentUsersPort.Param(param.getAssessmentId(), param.getSize(), param.getPage());
    }
}
