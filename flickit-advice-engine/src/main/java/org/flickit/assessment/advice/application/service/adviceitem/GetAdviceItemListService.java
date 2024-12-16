package org.flickit.assessment.advice.application.service.adviceitem;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.port.in.adviceitem.GetAdviceItemListUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemListPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.GET_ADVICE_ITEM_LIST_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAdviceItemListService implements GetAdviceItemListUseCase {

    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadAssessmentResultPort loadAssessmentResultPort;
    private final LoadAdviceItemListPort loadAdviceItemListPort;

    @Override
    public PaginatedResponse<AdviceItemListItem> getAdviceItems(Param param) {
        validateUserAccess(param.getAssessmentId(), param.getCurrentUserId());

        var assessmentResult = loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_ADVICE_ITEM_LIST_ASSESSMENT_RESULT_NOT_FOUND));

        var portResult = loadAdviceItemListPort.loadAll(assessmentResult.getId(), param.getPage(), param.getSize());
        var adviceItemList = portResult.getItems()
            .stream()
            .map(GetAdviceItemListService::toAdviceListItem)
            .toList();

        return new PaginatedResponse<>(adviceItemList,
            param.getPage(),
            param.getSize(),
            portResult.getSort(),
            portResult.getOrder(),
            portResult.getTotal());
    }

    private void validateUserAccess(UUID assessmentId, UUID currentUserId) {
        if (!assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_REPORT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);
    }

    private static AdviceItemListItem toAdviceListItem(AdviceItem adviceItem) {
        return new AdviceItemListItem(adviceItem.getId(),
            adviceItem.getTitle(),
            adviceItem.getDescription(),
            adviceItem.getCost().getTitle(),
            adviceItem.getPriority().getTitle(),
            adviceItem.getImpact().getTitle());
    }
}
