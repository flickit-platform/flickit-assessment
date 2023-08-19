package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase.AssessmentWithMaturityLevelId;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentsWithMaturityLevelIdBySpacePort;
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
    private LoadAssessmentsWithMaturityLevelIdBySpacePort loadAssessmentPort;

    @Test
    void getAssessmentList_ResultsFound_ItemsReturned() {
        Long spaceId = 1L;
        AssessmentWithMaturityLevelId assessment1S1 = createAssessment(spaceId);
        AssessmentWithMaturityLevelId assessment2S1 = createAssessment(spaceId);

        when(loadAssessmentPort.loadAssessmentsWithLastResultMaturityLevelIdBySpaceId(spaceId, 0, 10)).thenReturn(List.of(assessment1S1, assessment2S1));

        GetAssessmentListUseCase.Result result = service.getAssessmentList(new GetAssessmentListUseCase.Param(spaceId, 10, 0));
        assertEquals(2, result.assessments().size());
    }

    @Test
    void getAssessmentList_NoResultsFound_NoItemReturned() {
        Long spaceId = 2L;

        when(loadAssessmentPort.loadAssessmentsWithLastResultMaturityLevelIdBySpaceId(spaceId, 0, 10)).thenReturn(new ArrayList<>());

        GetAssessmentListUseCase.Result result = service.getAssessmentList(new GetAssessmentListUseCase.Param(spaceId, 10, 0));
        assertEquals(0, result.assessments().size());
    }

    private AssessmentWithMaturityLevelId createAssessment(Long spaceId) {
        return new AssessmentWithMaturityLevelId(
            UUID.randomUUID(),
            "code",
            "title",
            1L,
            AssessmentColor.BLUE.getId(),
            spaceId,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L
        );
    }
}
