package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.GetSpaceAssessmentListUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentListPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSpaceAssessmentListService implements GetSpaceAssessmentListUseCase {

    private final LoadAssessmentListPort loadAssessmentsBySpace;
    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final AssessmentPermissionChecker assessmentPermissionChecker;

    @Override
    public PaginatedResponse<SpaceAssessmentListItem> getAssessmentList(Param param) {
        if (!checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var assessmentListItemPaginatedResponse = loadAssessmentsBySpace.loadSpaceAssessments(
            param.getSpaceId(),
            param.getCurrentUserId(),
            param.getPage(),
            param.getSize()
        );

        List<SpaceAssessmentListItem> items = assessmentListItemPaginatedResponse.getItems().stream()
            .map(e -> {
                boolean viewable = assessmentPermissionChecker.isAuthorized(e.id(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT);
                boolean canViewDashboard = assessmentPermissionChecker.isAuthorized(e.id(), param.getCurrentUserId(), VIEW_DASHBOARD);
                boolean canViewQuestionnaires = assessmentPermissionChecker.isAuthorized(e.id(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST);
                return new SpaceAssessmentListItem(e.id(),
                    e.title(),
                    e.kit(),
                    e.lastModificationTime(),
                    viewable ? e.maturityLevel() : null,
                    viewable ? e.confidenceValue() : null,
                    e.isCalculateValid(),
                    e.isConfidenceValid(),
                    e.hasReport(),
                    new SpaceAssessmentListItem.Permissions(e.manageable(),
                        viewable,
                        canViewDashboard,
                        canViewQuestionnaires));
            }).toList();

        return new PaginatedResponse<>(items,
            assessmentListItemPaginatedResponse.getPage(),
            assessmentListItemPaginatedResponse.getSize(),
            assessmentListItemPaginatedResponse.getSort(),
            assessmentListItemPaginatedResponse.getOrder(),
            assessmentListItemPaginatedResponse.getTotal());
    }
}
