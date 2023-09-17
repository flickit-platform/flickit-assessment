package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentListItemsBySpacePort;
import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.application.service.assessment.CreateAssessmentService.NOT_DELETED_DELETION_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentListServiceTest {

    @InjectMocks
    private GetAssessmentListService service;

    @Mock
    private LoadAssessmentListItemsBySpacePort loadAssessmentPort;

    @Test
    void getAssessmentList_ResultsFound_ItemsReturned() {
        Long spaceId = 1L;
        AssessmentListItem assessment1S1 = createAssessment();
        AssessmentListItem assessment2S1 = createAssessment();

        PaginatedResponse<AssessmentListItem> paginatedResponse = new PaginatedResponse<>(
            List.of(assessment1S1, assessment2S1),
            0,
            2,
            "lastModificationTime",
            "DESC",
            2);
        when(loadAssessmentPort.loadAssessments(spaceId, 0, 10)).thenReturn(paginatedResponse);

        PaginatedResponse<AssessmentListItem> result = service.getAssessmentList(new GetAssessmentListUseCase.Param(spaceId, 10, 0));
        assertEquals(paginatedResponse, result);
    }

    @Test
    void getAssessmentList_NoResultsFound_NoItemReturned() {
        Long spaceId = 2L;

        PaginatedResponse<AssessmentListItem> paginatedResponse = new PaginatedResponse<>(
            new ArrayList<>(),
            0,
            0,
            "lastModificationTime",
            "DESC",
            2);
        when(loadAssessmentPort.loadAssessments(spaceId, 0, 10)).thenReturn(paginatedResponse);

        PaginatedResponse<AssessmentListItem> result = service.getAssessmentList(new GetAssessmentListUseCase.Param(spaceId, 10, 0));
        assertEquals(paginatedResponse, result);
    }

    private AssessmentListItem createAssessment() {
        return new AssessmentListItem(
            UUID.randomUUID(),
            "title",
            1L,
            AssessmentColor.BLUE.getId(),
            LocalDateTime.now(),
            1L,
            true,
            NOT_DELETED_DELETION_TIME
        );
    }
}
