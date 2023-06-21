package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadQAValuesByQAIdsPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.AssessmentSubjectValue;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
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
public class CalculateAssessmentSubjectMaturityLevelTest {
    private final LoadQualityAttributeBySubPort loadQABySubId = Mockito.mock(LoadQualityAttributeBySubPort.class);
    private final LoadQAValuesByQAIdsPort loadQAValuesByQAIds = Mockito.mock(LoadQAValuesByQAIdsPort.class);
    private final LoadMaturityLevelByKitPort loadMaturityLevelByKitPort = Mockito.mock(LoadMaturityLevelByKitPort.class);
    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    private final CalculateAssessmentSubjectMaturityLevel service = new CalculateAssessmentSubjectMaturityLevel(
        loadQABySubId,
        loadQAValuesByQAIds,
        loadMaturityLevelByKitPort);

    @Test
    public void calculateSubjectMaturityLevelWith2QuestionsResultsInMaturityLevel2_WillSucceed() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel2());
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        AssessmentSubjectValue subjectValue = service.calculateAssessmentSubjectMaturityLevel(context.getSubject());
        assertEquals(2, subjectValue.getMaturityLevel().getValue());
    }

    @Test
    public void calculateSubjectMaturityLevelWith2QuestionsResultsInMaturityLevel1_WillSucceed() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel1());
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        AssessmentSubjectValue subjectValue = service.calculateAssessmentSubjectMaturityLevel(context.getSubject());
        assertEquals(1, subjectValue.getMaturityLevel().getValue());
    }

    @Test
    public void calculateSubjectMaturityLevelWith2QuestionsResultsInNoAnswerException_WillFail() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel3());
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        assertThrows(ResourceNotFoundException.class, () -> service.calculateAssessmentSubjectMaturityLevel(context.getSubject()));
    }

    private void doMocks() {
        doReturn(List.of(context.getQualityAttribute())).when(loadQABySubId).loadQABySubId(context.getSubject().getId());
        doReturn(List.of(context.getQualityAttributeValue())).when(loadQAValuesByQAIds).loadQAValuesByQAIds(Set.of(context.getQualityAttribute().getId()));
        doReturn(Set.of(context.getMaturityLevel1(), context.getMaturityLevel2())).when(loadMaturityLevelByKitPort).loadMaturityLevelByKitId(context.getKit().getId());
    }


}
