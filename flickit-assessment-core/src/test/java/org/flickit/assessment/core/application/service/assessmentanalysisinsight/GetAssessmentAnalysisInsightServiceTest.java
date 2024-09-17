package org.flickit.assessment.core.application.service.assessmentanalysisinsight;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.InvalidContentException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AnalysisType;
import org.flickit.assessment.core.application.domain.AssessmentAnalysisInsight;
import org.flickit.assessment.core.application.port.in.assessmentanalysisinsight.GetAssessmentAnalysisInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentanalysis.LoadAssessmentAnalysisPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentAnalysisMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_ANALYSIS_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_ANALYSIS_UNABLE_TO_PARSE_JSON;
import static org.flickit.assessment.core.common.MessageKey.ASSESSMENT_ANALYSIS_AI_IS_DISABLED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentAnalysisInsightServiceTest {

    @InjectMocks
    private GetAssessmentAnalysisInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAssessmentAnalysisPort loadAssessmentAnalysisPort;

    @Mock
    private AppAiProperties appAiProperties;

    @Test
    void testGetAssessmentAnalysisInsight_WhenUserDoeNotHaveViewAssessmentReportPermission_ThenThrowAccessDeniedException() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        String type = "CODE_QUALITY";
        var param = new GetAssessmentAnalysisInsightUseCase.Param(assessmentId, type, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentAnalysisInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetAssessmentAnalysisInsight_WhenAssessmentDoesNotHaveAnyResult_ThenThrowResourceNotFoundException() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        String type = "CODE_QUALITY";
        var param = new GetAssessmentAnalysisInsightUseCase.Param(assessmentId, type, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentAnalysisInsight(param));
        assertEquals(GET_ASSESSMENT_ANALYSIS_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testGetAssessmentAnalysisInsight_WhenThereIsNoAssessmentAnalysisInsightAndAiIsDisabled_ThenAiAnalysisContainsAiIsNotEnabledMessageAndIsNotEditable() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        String type = "CODE_QUALITY";
        int typeOrdinal = AnalysisType.valueOf(type).getId();
        var param = new GetAssessmentAnalysisInsightUseCase.Param(assessmentId, type, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_AI_ANALYSIS)).thenReturn(false);
        when(loadAssessmentAnalysisPort.load(assessmentResult.getId(), typeOrdinal)).thenReturn(Optional.empty());
        when(appAiProperties.isEnabled()).thenReturn(false);

        var result = service.getAssessmentAnalysisInsight(param);

        assertNotNull(result);
        var analysis = (GetAssessmentAnalysisInsightUseCase.Result.AnalysisInsight.Message) result.aiAnalysis().analysis();
        assertEquals(MessageBundle.message(ASSESSMENT_ANALYSIS_AI_IS_DISABLED), analysis.msg());
        assertFalse(result.editable());
        assertNull(result.aiAnalysis().analysisTime());
        assertNull(result.assessorAnalysis());
    }

    @Test
    void testGetAssessmentAnalysisInsight_WhereThereIsNoAssessmentAnalysisInsightAndAiIsEnabled_ThenAiAnalysisAndAssessorAnalysisAreNull() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        String type = "CODE_QUALITY";
        int typeOrdinal = AnalysisType.valueOf(type).getId();
        var param = new GetAssessmentAnalysisInsightUseCase.Param(assessmentId, type, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_AI_ANALYSIS)).thenReturn(false);
        when(loadAssessmentAnalysisPort.load(assessmentResult.getId(), typeOrdinal)).thenReturn(Optional.empty());
        when(appAiProperties.isEnabled()).thenReturn(true);

        var result = service.getAssessmentAnalysisInsight(param);

        assertNotNull(result);
        assertNull(result.aiAnalysis());
        assertNull(result.assessorAnalysis());
        assertFalse(result.editable());
    }

    @Test
    @SneakyThrows
    void testGetAssessmentAnalysisInsight_WhenAssessmentAnalysisInsightIsNotEmptyAndJustAssessorAnalysisIsNull_ThenReturnAiAnalysisAsResult() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        String type = "CODE_QUALITY";
        int typeOrdinal = AnalysisType.valueOf(type).getId();
        var param = new GetAssessmentAnalysisInsightUseCase.Param(assessmentId, type, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessmentAnalysis = AssessmentAnalysisMother.assessmentAnalysisWithAiAnalysis();
        ObjectMapper objectMapper = new ObjectMapper();
        var assessmentAnalysisInsight = objectMapper.readValue(assessmentAnalysis.getAiAnalysis(), AssessmentAnalysisInsight.class);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_AI_ANALYSIS)).thenReturn(false);
        when(loadAssessmentAnalysisPort.load(assessmentResult.getId(), typeOrdinal)).thenReturn(Optional.of(assessmentAnalysis));

        var result = service.getAssessmentAnalysisInsight(param);

        var analysis = (GetAssessmentAnalysisInsightUseCase.Result.AnalysisInsight.AiInsight) result.aiAnalysis().analysis();
        assertNotNull(result);
        assertNotNull(result.aiAnalysis());
        assertEquals(assessmentAnalysisInsight.automatedTestCoverage().figureCaption(),
            analysis.insight().automatedTestCoverage().figureCaption());
        assertEquals(assessmentAnalysisInsight.automatedTestCoverage().text(),
            analysis.insight().automatedTestCoverage().text());
        assertEquals(assessmentAnalysisInsight.codeComplexity().figureCaption(),
            analysis.insight().codeComplexity().figureCaption());
        assertEquals(assessmentAnalysisInsight.codeComplexity().text(),
            analysis.insight().codeComplexity().text());
        assertEquals(assessmentAnalysisInsight.codeDuplication().text(),
            analysis.insight().codeDuplication().text());
        assertEquals(assessmentAnalysisInsight.codeOrganization().text(),
            analysis.insight().codeOrganization().text());
        assertEquals(assessmentAnalysisInsight.codeSecurity().figureCaption(),
            analysis.insight().codeSecurity().figureCaption());
        assertEquals(assessmentAnalysisInsight.codeSecurity().text(),
            analysis.insight().codeSecurity().text());
        assertEquals(assessmentAnalysisInsight.codeReliability().figureCaption(),
            analysis.insight().codeReliability().figureCaption());
        assertEquals(assessmentAnalysisInsight.codeReliability().text(),
            analysis.insight().codeReliability().text());
        assertEquals(assessmentAnalysisInsight.codeSmell().figureCaption(),
            analysis.insight().codeSmell().figureCaption());
        assertEquals(assessmentAnalysisInsight.codeSmell().text(),
            analysis.insight().codeSmell().text());
        assertEquals(assessmentAnalysisInsight.overallCodeQuality().text(),
            analysis.insight().overallCodeQuality().text());
        assertEquals(assessmentAnalysisInsight.programmingLanguages().info(),
            analysis.insight().programmingLanguages().info());
        assertEquals(assessmentAnalysisInsight.programmingLanguages().usage(),
            analysis.insight().programmingLanguages().usage());
        assertEquals(assessmentAnalysisInsight.thirdPartyLibraries().text(),
            analysis.insight().thirdPartyLibraries().text());
        assertEquals(assessmentAnalysis.getAiAnalysisTime(), result.aiAnalysis().analysisTime());
        assertNull(result.assessorAnalysis());
        assertFalse(result.editable());
    }

    @Test
    @SneakyThrows
    void testGetAssessmentAnalysisInsight_WhenAssessmentAnalysisInsightIsNotEmptyAndJustAiAnalysisIsNull_ThenReturnAssessorAnalysisAsResult() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        String type = "CODE_QUALITY";
        int typeOrdinal = AnalysisType.valueOf(type).getId();
        var param = new GetAssessmentAnalysisInsightUseCase.Param(assessmentId, type, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessmentAnalysis = AssessmentAnalysisMother.assessmentAnalysisWithAssessorAnalysis();
        ObjectMapper objectMapper = new ObjectMapper();
        var assessmentAnalysisInsight = objectMapper.readValue(assessmentAnalysis.getAssessorAnalysis(), AssessmentAnalysisInsight.class);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_AI_ANALYSIS)).thenReturn(false);
        when(loadAssessmentAnalysisPort.load(assessmentResult.getId(), typeOrdinal)).thenReturn(Optional.of(assessmentAnalysis));

        var result = service.getAssessmentAnalysisInsight(param);

        var analysis = (GetAssessmentAnalysisInsightUseCase.Result.AnalysisInsight.AssessorInsight) result.assessorAnalysis().analysis();
        assertNotNull(result);
        assertNotNull(result.assessorAnalysis());
        assertEquals(assessmentAnalysisInsight.automatedTestCoverage().figureCaption(),
            analysis.insight().automatedTestCoverage().figureCaption());
        assertEquals(assessmentAnalysisInsight.automatedTestCoverage().text(),
            analysis.insight().automatedTestCoverage().text());
        assertEquals(assessmentAnalysisInsight.codeComplexity().figureCaption(),
            analysis.insight().codeComplexity().figureCaption());
        assertEquals(assessmentAnalysisInsight.codeComplexity().text(),
            analysis.insight().codeComplexity().text());
        assertEquals(assessmentAnalysisInsight.codeDuplication().text(),
            analysis.insight().codeDuplication().text());
        assertEquals(assessmentAnalysisInsight.codeOrganization().text(),
            analysis.insight().codeOrganization().text());
        assertEquals(assessmentAnalysisInsight.codeSecurity().figureCaption(),
            analysis.insight().codeSecurity().figureCaption());
        assertEquals(assessmentAnalysisInsight.codeSecurity().text(),
            analysis.insight().codeSecurity().text());
        assertEquals(assessmentAnalysisInsight.codeReliability().figureCaption(),
            analysis.insight().codeReliability().figureCaption());
        assertEquals(assessmentAnalysisInsight.codeReliability().text(),
            analysis.insight().codeReliability().text());
        assertEquals(assessmentAnalysisInsight.codeSmell().figureCaption(),
            analysis.insight().codeSmell().figureCaption());
        assertEquals(assessmentAnalysisInsight.codeSmell().text(),
            analysis.insight().codeSmell().text());
        assertEquals(assessmentAnalysisInsight.overallCodeQuality().text(),
            analysis.insight().overallCodeQuality().text());
        assertEquals(assessmentAnalysisInsight.programmingLanguages().info(),
            analysis.insight().programmingLanguages().info());
        assertEquals(assessmentAnalysisInsight.programmingLanguages().usage(),
            analysis.insight().programmingLanguages().usage());
        assertEquals(assessmentAnalysisInsight.thirdPartyLibraries().text(),
            analysis.insight().thirdPartyLibraries().text());
        assertEquals(assessmentAnalysis.getAssessorAnalysisTime(), result.assessorAnalysis().analysisTime());
        assertNull(result.aiAnalysis());
        assertFalse(result.editable());
    }

    @Test
    void testGetAssessmentAnalysisInsight_WhenAnalysisInsightJsonContentIsNotCorrect_ThenThrowInvalidContentException() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        String type = "CODE_QUALITY";
        int typeOrdinal = AnalysisType.valueOf(type).getId();
        var param = new GetAssessmentAnalysisInsightUseCase.Param(assessmentId, type, currentUserId);
        var assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        var assessmentAnalysis = AssessmentAnalysisMother.assessmentAnalysisWithInCorrectAiAnalysis();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ASSESSMENT_AI_ANALYSIS)).thenReturn(false);
        when(loadAssessmentAnalysisPort.load(assessmentResult.getId(), typeOrdinal)).thenReturn(Optional.of(assessmentAnalysis));

        InvalidContentException invalidContentException = assertThrows(InvalidContentException.class, () -> service.getAssessmentAnalysisInsight(param));
        assertEquals(GET_ASSESSMENT_ANALYSIS_UNABLE_TO_PARSE_JSON, invalidContentException.getMessage());
    }
}
