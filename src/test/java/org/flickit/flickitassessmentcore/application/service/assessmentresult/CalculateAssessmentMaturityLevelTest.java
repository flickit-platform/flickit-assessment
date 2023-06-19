package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadQAValuesByQAIdsPort;
import org.flickit.flickitassessmentcore.application.service.exception.NoMaturityLevelFound;
import org.flickit.flickitassessmentcore.domain.AssessmentSubjectValue;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CalculateAssessmentMaturityLevelTest {
    private final LoadMaturityLevelByKitPort loadMaturityLevelByKitPort = Mockito.mock(LoadMaturityLevelByKitPort.class);
    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    private final CalculateAssessmentMaturityLevel service = new CalculateAssessmentMaturityLevel(loadMaturityLevelByKitPort);

    @Test
    public void calculateSubjectMaturityLevelWith2QuestionsResultsInMaturityLevel2_WillSucceed() {
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel2());
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        MaturityLevel ml = service.calculateAssessmentMaturityLevel(List.of(context.getSubjectValue()), context.getAssessment());
        assertEquals(2, ml.getValue());
    }

    @Test
    public void calculateSubjectMaturityLevelWith2QuestionsResultsInMaturityLevel1_WillSucceed() {
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel1());
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        MaturityLevel ml = service.calculateAssessmentMaturityLevel(List.of(context.getSubjectValue()), context.getAssessment());
        assertEquals(1, ml.getValue());
    }

    @Test
    public void calculateSubjectMaturityLevelWith2QuestionsResultsInNoAnswerException_WillFail() {
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel3());
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        assertThrows(NoMaturityLevelFound.class, () -> service.calculateAssessmentMaturityLevel(List.of(context.getSubjectValue()), context.getAssessment()));
    }

    private void doMocks() {
        doReturn(Set.of(context.getMaturityLevel1(), context.getMaturityLevel2())).when(loadMaturityLevelByKitPort).loadMaturityLevelByKitId(context.getKit().getId());
    }


}
