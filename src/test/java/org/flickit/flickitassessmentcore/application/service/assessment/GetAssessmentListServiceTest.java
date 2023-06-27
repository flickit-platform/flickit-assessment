package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.GetAssessmentListUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentBySpacePort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetAssessmentListServiceTest {

    private final LoadAssessmentBySpacePort loadAssessmentBySpace = Mockito.mock(LoadAssessmentBySpacePort.class);

    private final GetAssessmentListService service = new GetAssessmentListService(
        loadAssessmentBySpace
    );

    private final Long space1 = 1L;
    private final Long space2 = 2L;

    private final Assessment assessment1S1 = createAssessment(space1);
    private final Assessment assessment2S1 = createAssessment(space1);

    @BeforeEach
    public void init() {
        when(loadAssessmentBySpace.loadAssessmentBySpaceId(space1, 0, 10)).thenReturn(List.of(assessment1S1, assessment2S1));
        when(loadAssessmentBySpace.loadAssessmentBySpaceId(space2, 0, 10)).thenReturn(new ArrayList<>());
    }

    private Assessment createAssessment(Long spaceId) {
        return new Assessment(
            UUID.randomUUID(),
            "code",
            "title",
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            AssessmentColor.BLUE.getId(),
            spaceId
        );
    }


    @Test
    void getAssessmentList_ResultsFound_ItemsReturned() {
        GetAssessmentListUseCase.Result result = service.getAssessmentList(new GetAssessmentListUseCase.Param(space1, 10, 0));
        assertEquals(2, result.assessments().size());
    }

    @Test
    void getAssessmentList_NoResultsFound_NoItemReturned() {
        GetAssessmentListUseCase.Result result = service.getAssessmentList(new GetAssessmentListUseCase.Param(space2, 10, 0));
        assertEquals(0, result.assessments().size());
    }

}
