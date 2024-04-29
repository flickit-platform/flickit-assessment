package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentListItemsBySpacePort;
import org.flickit.assessment.core.application.port.out.space.CheckSpaceAccessPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceIdsByUserIdPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAssessmentListService implements GetAssessmentListUseCase {

    private final LoadAssessmentListItemsBySpacePort loadAssessmentsBySpace;
    private final CheckSpaceAccessPort checkSpaceAccessPort;
    private final LoadSpaceIdsByUserIdPort loadSpaceIdsByUserIdPort;

    @Override
    public PaginatedResponse<AssessmentListItem> getAssessmentList(GetAssessmentListUseCase.Param param) {
        List<Long> spaceIds = param.getSpaceIds();
        UUID currentUserId = param.getCurrentUserId();

        if (spaceIds == null || spaceIds.isEmpty())
            spaceIds = loadSpaceIdsByUserIdPort.loadSpaceIdsByUserId(currentUserId);
        else if (spaceIds.size() == 1 && !checkSpaceAccessPort.checkIsMember(spaceIds.get(0), currentUserId))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return loadAssessmentsBySpace.loadNotDeletedAssessments(
            spaceIds,
            param.getKitId(),
            param.getPage(),
            param.getSize()
        );
    }
}
