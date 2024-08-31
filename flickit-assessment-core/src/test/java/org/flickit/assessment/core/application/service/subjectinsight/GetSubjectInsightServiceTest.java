package org.flickit.assessment.core.application.service.subjectinsight;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.domain.report.SubjectAttributeReportItem;
import org.flickit.assessment.core.application.domain.report.SubjectReportItem;
import org.flickit.assessment.core.application.port.in.subjectinsight.GetSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.core.test.fixture.application.SubjectInsightMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_SUBJECT_INSIGHT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSubjectInsightServiceTest {

    @InjectMocks
    private GetSubjectInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadSubjectInsightPort loadSubjectInsightPort;

    @Mock
    private LoadSubjectReportInfoPort loadSubjectReportInfoPort;

    @Test
    void testGetSubjectInsight_UserDoesNotHaveRequiredPermission_ThrowAccessDeniedException() {
        GetSubjectInsightUseCase.Param param = new GetSubjectInsightUseCase.Param(UUID.randomUUID(), 1L, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> service.getSubjectInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(loadSubjectReportInfoPort,
            validateAssessmentResultPort,
            loadAssessmentResultPort,
            loadSubjectInsightPort);
    }

    @Test
    void testGetSubjectInsight_SubjectInsightExistsAndIsValidAndEditable_ReturnAssessorInsight() {
        GetSubjectInsightUseCase.Param param = new GetSubjectInsightUseCase.Param(UUID.randomUUID(), 1L, UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        SubjectInsight subjectInsight = SubjectInsightMother.subjectInsight();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);

        GetSubjectInsightUseCase.Result result = service.getSubjectInsight(param);

        assertNull(result.defaultInsight());
        assertNotNull(result.assessorInsight());
        assertEquals(subjectInsight.getInsight(), result.assessorInsight().insight());
        assertEquals(subjectInsight.getInsightTime(), result.assessorInsight().creationTime());
        assertTrue(result.assessorInsight().isValid());
        assertTrue(result.editable());

        verifyNoInteractions(loadSubjectReportInfoPort);
    }

    @Test
    void testGetSubjectInsight_SubjectInsightExistsAndIsNotValidAndNotEditable_ReturnAssessorInsight() {
        GetSubjectInsightUseCase.Param param = new GetSubjectInsightUseCase.Param(UUID.randomUUID(), 1L, UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();
        SubjectInsight subjectInsight = new SubjectInsight(assessmentResult.getId(), param.getSubjectId(),
            "assessor insight",
            assessmentResult.getLastCalculationTime().minusDays(1),
            param.getCurrentUserId()
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.of(subjectInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(false);

        GetSubjectInsightUseCase.Result result = service.getSubjectInsight(param);

        assertNull(result.defaultInsight());
        assertNotNull(result.assessorInsight());
        assertEquals(subjectInsight.getInsight(), result.assessorInsight().insight());
        assertEquals(subjectInsight.getInsightTime(), result.assessorInsight().creationTime());
        assertFalse(result.assessorInsight().isValid());
        assertFalse(result.editable());

        verifyNoInteractions(loadSubjectReportInfoPort);
    }

    @Test
    void testGetSubjectInsight_SubjectInsightDoesNotExist_ReturnDefaultInsight() {
        GetSubjectInsightUseCase.Param param = new GetSubjectInsightUseCase.Param(UUID.randomUUID(), 1L, UUID.randomUUID());
        AssessmentResult assessmentResult = AssessmentResultMother.validResultWithJustAnId();

        var subjectReport = createSubjectReportInfo();

        var defaultInsight = MessageBundle.message(SUBJECT_DEFAULT_INSIGHT,
            subjectReport.subject().title(),
            subjectReport.subject().description(),
            subjectReport.subject().confidenceValue().intValue(),
            subjectReport.subject().title(),
            subjectReport.subject().maturityLevel().getIndex(),
            subjectReport.maturityLevels().size(),
            subjectReport.subject().maturityLevel().getTitle(),
            subjectReport.attributes().size(),
            subjectReport.subject().title());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId()))
            .thenReturn(Optional.empty());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(false);
        when(loadSubjectReportInfoPort.load(param.getAssessmentId(), param.getSubjectId())).thenReturn(subjectReport);

        GetSubjectInsightUseCase.Result result = service.getSubjectInsight(param);

        assertNull(result.assessorInsight());
        assertNotNull(result.defaultInsight());
        assertEquals(defaultInsight, result.defaultInsight().insight());
        assertFalse(result.editable());
    }

    private LoadSubjectReportInfoPort.Result createSubjectReportInfo() {
        MaturityLevel maturityLevel1 = MaturityLevelMother.levelTwo();
        MaturityLevel maturityLevel2 = MaturityLevelMother.levelFive();
        var subjectReportItem = new SubjectReportItem(2L, "software", "description", maturityLevel1,
            100.0, true, true);
        var maturityScore1 = new SubjectAttributeReportItem.MaturityScore(maturityLevel1, 23.1);
        var maturityScore2 = new SubjectAttributeReportItem.MaturityScore(maturityLevel2, 53.2);

        var subjectAttributeReportItem1 = new SubjectAttributeReportItem(3L,
            4,
            "scalability",
            "desc1",
            maturityLevel1,
            List.of(maturityScore1, maturityScore2),
            100.0);

        var subjectAttributeReportItem2 = new SubjectAttributeReportItem(4L,
            4,
            "flexibility",
            "desc2",
            maturityLevel2,
            List.of(maturityScore1, maturityScore2),
            90.0);

        return new LoadSubjectReportInfoPort.Result(subjectReportItem,
            List.of(maturityLevel1, maturityLevel2),
            List.of(subjectAttributeReportItem1, subjectAttributeReportItem2));
    }
}
