package org.flickit.assessment.core.application.service.subject;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.report.SubjectReport;
import org.flickit.assessment.core.application.internal.ValidateAssessmentResult;
import org.flickit.assessment.core.application.port.in.subject.ReportSubjectUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectReportInfoPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

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
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Test
    void testReportSubject_WhenCurrentUserDoesntHaveAssessmentAccess_ThenThrowsAccessDeniedException() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        long subjectId = 1;
        ReportSubjectUseCase.Param param = new ReportSubjectUseCase.Param(assessmentId, currentUserId, subjectId);

        when(checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId)).thenReturn(false);

        AccessDeniedException throwable = assertThrows(AccessDeniedException.class, () -> service.reportSubject(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testReportSubject_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        long subjectId = 1;
        ReportSubjectUseCase.Param param = new ReportSubjectUseCase.Param(assessmentId, currentUserId, subjectId);

        SubjectReport.MaturityLevel maturityLevel1 = new SubjectReport.MaturityLevel(1, "great", 1, 1);
        SubjectReport.MaturityLevel maturityLevel2 = new SubjectReport.MaturityLevel(2, "perfect", 2, 1);
        SubjectReport.SubjectReportItem subjectReportItem = new SubjectReport.SubjectReportItem(2L,
            "software",
            maturityLevel1,
            100.0,
            true,
            true);
        SubjectReport.AttributeReportItem.MaturityScore maturityScore1 =
            new SubjectReport.AttributeReportItem.MaturityScore(maturityLevel1, 23.1);
        SubjectReport.AttributeReportItem.MaturityScore maturityScore2 =
            new SubjectReport.AttributeReportItem.MaturityScore(maturityLevel2, 53.2);

        SubjectReport.AttributeReportItem attributeReportItem1 = new SubjectReport.AttributeReportItem(3L,
            4,
            "scalability",
            "desc1",
            maturityLevel1,
            List.of(maturityScore1, maturityScore2),
            100.0);

        SubjectReport.AttributeReportItem attributeReportItem2 = new SubjectReport.AttributeReportItem(4L,
            4,
            "flexibility",
            "desc2",
            maturityLevel2,
            List.of(maturityScore1, maturityScore2),
            90.0);

        SubjectReport subjectReport = new SubjectReport(subjectReportItem,
            List.of(maturityLevel1, maturityLevel2),
            List.of(attributeReportItem1, attributeReportItem2));


        when(checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId)).thenReturn(true);
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
        assertEquals(2, result.topWeaknesses().size());
    }
}
