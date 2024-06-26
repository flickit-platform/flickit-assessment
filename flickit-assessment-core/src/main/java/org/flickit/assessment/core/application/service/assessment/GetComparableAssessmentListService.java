package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.port.in.assessment.GetComparableAssessmentListUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentListPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetComparableAssessmentListService implements GetComparableAssessmentListUseCase {

    private final LoadAssessmentListPort loadAssessmentListPort;

    @Override
    public PaginatedResponse<ComparableAssessmentListItem> getComparableAssessmentList(Param param) {
        var assessmentListItemPaginatedResponse = loadAssessmentListPort.loadComparableAssessments(
            param.getKitId(),
            param.getCurrentUserId(),
            param.getPage(),
            param.getSize()
        );

        List<ComparableAssessmentListItem> items = assessmentListItemPaginatedResponse.getItems().stream()
            .map(e -> new ComparableAssessmentListItem(e.id(),
                e.title(),
                e.kit(),
                e.space(),
                e.lastModificationTime(),
                e.maturityLevel(),
                e.isCalculateValid(),
                e.isConfidenceValid()))
            .toList();

        return new PaginatedResponse<>(items,
            assessmentListItemPaginatedResponse.getPage(),
            assessmentListItemPaginatedResponse.getSize(),
            assessmentListItemPaginatedResponse.getSort(),
            assessmentListItemPaginatedResponse.getOrder(),
            assessmentListItemPaginatedResponse.getTotal());
    }
}
