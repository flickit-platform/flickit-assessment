package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateAssessmentMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateAssessmentSubjectMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateMaturityLevelCommand;
import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateQualityAttributeMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultByAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.SaveAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadAssessmentSubjectByAssessmentKitPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubPort;
import org.flickit.flickitassessmentcore.application.service.exception.NoAnswerFoundException;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.flickit.flickitassessmentcore.Constants.ANSWER_OPTION_IMPACT_VALUE4;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CalculateMaturityLevelServiceTest {
    private final LoadAssessmentPort loadAssessment = Mockito.mock(LoadAssessmentPort.class);
    private final LoadAssessmentResultByAssessmentPort loadAssessmentResultByAssessment = Mockito.mock(LoadAssessmentResultByAssessmentPort.class);
    private final LoadAssessmentSubjectByAssessmentKitPort loadSubjectByKit = Mockito.mock(LoadAssessmentSubjectByAssessmentKitPort.class);
    private final LoadQualityAttributeBySubPort loadQualityAttributeBySubject = Mockito.mock(LoadQualityAttributeBySubPort.class);
    private final SaveAssessmentResultPort saveAssessmentResult = Mockito.mock(SaveAssessmentResultPort.class);
    private final CalculateQualityAttributeMaturityLevelUseCase calculateQualityAttributeMaturityLevel = Mockito.mock(CalculateQualityAttributeMaturityLevelUseCase.class);
    private final CalculateAssessmentSubjectMaturityLevelUseCase calculateAssessmentSubjectMaturityLevel = Mockito.mock(CalculateAssessmentSubjectMaturityLevelUseCase.class);
    private final CalculateAssessmentMaturityLevelUseCase calculateAssessmentMaturityLevel = Mockito.mock(CalculateAssessmentMaturityLevel.class);
    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    private final CalculateMaturityLevelService calculateMaturityLevelService = new CalculateMaturityLevelService(
        loadAssessment,
        loadAssessmentResultByAssessment,
        loadSubjectByKit,
        loadQualityAttributeBySubject,
        saveAssessmentResult,
        calculateQualityAttributeMaturityLevel,
        calculateAssessmentSubjectMaturityLevel,
        calculateAssessmentMaturityLevel);

    private final CalculateMaturityLevelCommand command = new CalculateMaturityLevelCommand(context.getAssessment().getId());

    @Disabled
    @Test
    public void calculateQualityAttributeMaturityLevelWith2QuestionsResultsInMaturityLevel2_WillSucceed() {
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        AssessmentResult maturityLevel = calculateMaturityLevelService.calculateMaturityLevel(command);
        // assert
    }

    @Disabled
    @Test
    public void calculateQualityAttributeMaturityLevelWith2QuestionsResultsInMaturityLevel1_WillSucceed() {
        context.getOptionImpact1Q2().setValue(new BigDecimal(0));
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        AssessmentResult maturityLevel = calculateMaturityLevelService.calculateMaturityLevel(command);
        // assert
        // Return to former state
        context.getOptionImpact1Q2().setValue(ANSWER_OPTION_IMPACT_VALUE4);
    }

    @Disabled
    @Test
    public void calculateQualityAttributeMaturityLevelWith2QuestionsResultsInNoAnswerException_WillFail() {
        context.getAnswer2().setQuestion(null);
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        assertThrows(NoAnswerFoundException.class, () -> calculateMaturityLevelService.calculateMaturityLevel(command));
        // Return to former state
        context.getAnswer2().setQuestion(context.getQuestion2());
    }

    private void doMocks() {
//        doReturn(context.getQualityAttribute()).when(loadQA).loadQualityAttribute(context.getQualityAttribute().getId());
//        doNothing().when(saveQAValue).saveQualityAttributeValue(context.getQualityAttributeValue());
    }


}
