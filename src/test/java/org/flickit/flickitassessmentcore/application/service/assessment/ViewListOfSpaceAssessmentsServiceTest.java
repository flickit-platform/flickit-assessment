package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.port.in.assessment.ViewListOfSpaceAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentBySpacePort;
import org.flickit.flickitassessmentcore.domain.Assessment;
import org.flickit.flickitassessmentcore.domain.AssessmentColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)

public class ViewListOfSpaceAssessmentsServiceTest {

    private final LoadAssessmentBySpacePort loadAssessmentBySpace = Mockito.mock(LoadAssessmentBySpacePort.class);

    private final ViewListOfSpaceAssessmentsService service = new ViewListOfSpaceAssessmentsService(
        loadAssessmentBySpace
    );

    private final Long space1 = 1L;
    private final Long space2 = 2L;
    private final Long space3 = 3L;

    private final Assessment assessment1S1 = createAssessment(space1);
    private final Assessment assessment2S1 = createAssessment(space1);
    private final Assessment assessment1S2 = createAssessment(space2);
    private final Assessment assessment2S2 = createAssessment(space2);
    private final Assessment assessment3S2 = createAssessment(space2);

    @BeforeAll
    public static void init() {

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
            spaceId,
            new ArrayList<>(),
            new ArrayList<>()
        );
    }

    @Test
    public void loadAssessmentsOfSpace1_returnsIts2Assessments_success() {
        doMocks();
        ViewListOfSpaceAssessmentsUseCase.Result result = service.viewListOfSpaceAssessments(new ViewListOfSpaceAssessmentsUseCase.Param(space1));
        assertEquals(2, result.assessments().size());
    }

    @Test
    public void loadAssessmentsOfSpace2_returnsIts3Assessments_success() {
        doMocks();
        ViewListOfSpaceAssessmentsUseCase.Result result = service.viewListOfSpaceAssessments(new ViewListOfSpaceAssessmentsUseCase.Param(space2));
        assertEquals(3, result.assessments().size());
    }

    @Test
    public void loadAssessmentsOfSpace3_returnsIts0_success() {
        doMocks();
        ViewListOfSpaceAssessmentsUseCase.Result result = service.viewListOfSpaceAssessments(new ViewListOfSpaceAssessmentsUseCase.Param(space3));
        assertEquals(0, result.assessments().size());
    }

    public void doMocks() {
        doReturn(List.of(assessment1S1, assessment2S1)).when(loadAssessmentBySpace).loadAssessmentBySpaceId(space1);
        doReturn(List.of(assessment1S2, assessment2S2, assessment3S2)).when(loadAssessmentBySpace).loadAssessmentBySpaceId(space2);
        doReturn(new ArrayList<>()).when(loadAssessmentBySpace).loadAssessmentBySpaceId(space3);
    }

}
