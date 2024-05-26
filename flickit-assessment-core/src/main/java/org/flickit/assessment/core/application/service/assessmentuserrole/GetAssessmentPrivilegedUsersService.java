package org.flickit.assessment.core.application.service.assessmentuserrole;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessmentuserrole.GetAssessmentPrivilegedUsersUseCase;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadAssessmentPrivilegedUsersPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GET_ASSESSMENT_PRIVILEGED_USERS;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentPrivilegedUsersService implements GetAssessmentPrivilegedUsersUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final AssessmentPermissionChecker assessmentPermissionChecker;
    private final LoadAssessmentPrivilegedUsersPort port;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public PaginatedResponse<AssessmentPrivilegedUser> getAssessmentPrivilegedUsers(Param param) {
        if (!assessmentPermissionChecker.isAuthorized(param.getAssessmentId(),
            param.getCurrentUserId(),
            GET_ASSESSMENT_PRIVILEGED_USERS))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentPrivilegedUserPaginatedResponse = port.loadAssessmentPrivilegedUsers(toParam(param));
        List<AssessmentPrivilegedUser> items = assessmentPrivilegedUserPaginatedResponse.getItems().stream()
            .map(e -> {
                String pictureLink = null;
                if (e.picturePath() != null && !e.picturePath().trim().isBlank()) {
                    pictureLink = createFileDownloadLinkPort.createDownloadLink(e.picturePath(), EXPIRY_DURATION);
                }
                AssessmentPrivilegedUser.Role role = new AssessmentPrivilegedUser.Role(e.role().id(), e.role().title());
                return new AssessmentPrivilegedUser(e.id(),
                    e.email(),
                    e.displayName(),
                    pictureLink,
                    role);
            }).toList();

        return new PaginatedResponse<>(items,
            assessmentPrivilegedUserPaginatedResponse.getPage(),
            assessmentPrivilegedUserPaginatedResponse.getSize(),
            assessmentPrivilegedUserPaginatedResponse.getSort(),
            assessmentPrivilegedUserPaginatedResponse.getOrder(),
            assessmentPrivilegedUserPaginatedResponse.getTotal());
    }

    private LoadAssessmentPrivilegedUsersPort.Param toParam(Param param) {
        return new LoadAssessmentPrivilegedUsersPort.Param(param.getAssessmentId(), param.getSize(), param.getPage());
    }
}
