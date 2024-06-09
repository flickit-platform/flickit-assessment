package org.flickit.assessment.core.application.service.subject;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.SubjectAttributeReportItem;
import org.flickit.assessment.core.application.domain.report.SubjectReportItem;
import org.flickit.assessment.core.application.internal.ValidateAssessmentResult;
import org.flickit.assessment.core.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_SUBJECT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportSubjectServiceTest {

    @InjectMocks
    private ReportSubjectService service;

    @Mock
    private ValidateAssessmentResult validateAssessmentResult;

    @Mock
    private LoadSubjectReportInfoPort loadSubjectReportInfoPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testReportSubject_WhenCurrentUserDoesntHaveAssessmentAccess_ThenThrowsAccessDeniedException() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        long subjectId = 1;
        ReportSubjectUseCase.Param param = new ReportSubjectUseCase.Param(assessmentId, subjectId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_SUBJECT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.reportSubject(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testReportSubject_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        long subjectId = 1;
        ReportSubjectUseCase.Param param = new ReportSubjectUseCase.Param(assessmentId, subjectId, currentUserId);

        MaturityLevel maturityLevel1 = MaturityLevelMother.levelTwo();
        MaturityLevel maturityLevel2 = MaturityLevelMother.levelFive();
        var subjectReportItem = new SubjectReportItem(2L, "software", maturityLevel1,
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

        LoadSubjectReportInfoPort.Result subjectReport = new LoadSubjectReportInfoPort.Result(subjectReportItem,
            List.of(maturityLevel1, maturityLevel2),
            List.of(subjectAttributeReportItem1, subjectAttributeReportItem2));

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_SUBJECT_REPORT)).thenReturn(true);
        doNothing().when(validateAssessmentResult).validate(assessmentId);
        when(loadSubjectReportInfoPort.load(assessmentId, subjectId)).thenReturn(subjectReport);

        ReportSubjectUseCase.Result result = service.reportSubject(param);

        assertNotNull(result);
        assertEquals(subjectReport.subject(), result.subject());
        assertEquals(subjectReport.attributes(), result.attributes());
        assertEquals(subjectReport.maturityLevels().size(), result.maturityLevelsCount());
        assertNotNull(result.topStrengths());
        assertEquals(1, result.topStrengths().size());
        assertNotNull(result.topWeaknesses());
        assertEquals(1, result.topWeaknesses().size());
    }
}
