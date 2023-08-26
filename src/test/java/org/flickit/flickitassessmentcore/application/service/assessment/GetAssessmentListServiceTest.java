package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentListItemsBySpacePort;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAssessmentListServiceTest {

    @InjectMocks
    private GetAssessmentListService service;

    @Mock
    private LoadAssessmentListItemsBySpacePort loadAssessmentPort;

    @Test
    void getAssessmentList_ResultsFound_ItemsReturned() {
        Long spaceId = 1L;
        AssessmentListItem assessment1S1 = createAssessment(spaceId);
        AssessmentListItem assessment2S1 = createAssessment(spaceId);

        when(loadAssessmentPort.loadAssessmentListItemBySpaceId(spaceId, 0, 10)).thenReturn(List.of(assessment1S1, assessment2S1));

        GetAssessmentListUseCase.Result result = service.getAssessmentList(new GetAssessmentListUseCase.Param(spaceId, 10, 0));
        assertEquals(2, result.assessments().size());
    }

    @Test
    void getAssessmentList_NoResultsFound_NoItemReturned() {
        Long spaceId = 2L;

        when(loadAssessmentPort.loadAssessmentListItemBySpaceId(spaceId, 0, 10)).thenReturn(new ArrayList<>());

        GetAssessmentListUseCase.Result result = service.getAssessmentList(new GetAssessmentListUseCase.Param(spaceId, 10, 0));
        assertEquals(0, result.assessments().size());
    }

    private AssessmentListItem createAssessment(Long spaceId) {
        return new AssessmentListItem(
            UUID.randomUUID(),
            "title",
            1L,
            AssessmentColor.BLUE.getId(),
            LocalDateTime.now(),
            1L
        );
    }
}
