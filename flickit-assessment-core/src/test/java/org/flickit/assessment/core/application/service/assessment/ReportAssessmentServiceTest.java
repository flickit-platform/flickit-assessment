package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentSubjectReportItem;
import org.flickit.assessment.core.application.domain.report.AttributeReportItem;
import org.flickit.assessment.core.application.internal.ValidateAssessmentResult;
import org.flickit.assessment.core.application.port.in.assessment.ReportAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportAssessmentServiceTest {

    @InjectMocks
    private ReportAssessmentService service;

    @Mock
    private ValidateAssessmentResult validateAssessmentResult;

    @Mock
    private LoadAssessmentReportInfoPort loadReportInfoPort;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Test
    void testReportAssessment_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();

        ReportAssessmentUseCase.Param param = new ReportAssessmentUseCase.Param(assessmentId, currentUserId);

        var attributes = List.of(
            new AttributeReportItem(1L, "attrTitle1", 2, 1),
            new AttributeReportItem(2L, "attrTitle2", 1, 2),
            new AttributeReportItem(3L, "attrTitle3", 3, 3));
        var expertGroup = new AssessmentReportItem.AssessmentKitItem.ExpertGroup(1L, "expertGroupTitle1", "picture/link");
        var kit = new AssessmentReportItem.AssessmentKitItem(1L, "kitTitle", "kitSummary", 3, expertGroup);
        MaturityLevel assessmentMaturityLevel = MaturityLevelMother.levelThree();
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime lastModificationTime = LocalDateTime.now();
        AssessmentReportItem assessment = new AssessmentReportItem(assessmentId,
            "assessmentTitle",
            kit,
            assessmentMaturityLevel,
            1.5,
            true,
            true,
            AssessmentColor.BLUE,
            creationTime,
            lastModificationTime);

        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        MaturityLevel softwareLevel = MaturityLevelMother.levelFour();
        MaturityLevel teamLevel = MaturityLevelMother.levelTwo();
        var subjects = List.of(
            new AssessmentSubjectReportItem(1L, "software", 1, "subjectDesc1", 20.0, softwareLevel, List.of()),
            new AssessmentSubjectReportItem(2L, "team", 2, "subjectDesc2", 58.6, teamLevel, List.of()));
        var assessmentReport = new LoadAssessmentReportInfoPort.Result(assessment, attributes, maturityLevels, subjects);

        when(checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId)).thenReturn(true);
        doNothing().when(validateAssessmentResult).validate(param.getAssessmentId());
        when(loadReportInfoPort.load(assessmentId)).thenReturn(assessmentReport);

        ReportAssessmentUseCase.Result result = service.reportAssessment(param);

        assertNotNull(assessmentReport);
        assertNotNull(assessmentReport.assessment());
        assertEquals(assessmentReport.assessment().id(), result.assessment().id());
        assertEquals(assessmentReport.assessment().title(), result.assessment().title());
        assertEquals(assessmentReport.assessment().confidenceValue(), result.assessment().confidenceValue());
        assertEquals(assessmentReport.assessment().isCalculateValid(), result.assessment().isCalculateValid());
        assertEquals(assessmentReport.assessment().isConfidenceValid(), result.assessment().isConfidenceValid());
        assertEquals(assessmentReport.assessment().lastModificationTime(), result.assessment().lastModificationTime());
        assertEquals(assessmentReport.assessment().color(), result.assessment().color());
        assertEquals(assessmentReport.assessment().maturityLevel().getId(), result.assessment().maturityLevel().getId());
        assertEquals(assessmentReport.assessment().maturityLevel().getIndex(), result.assessment().maturityLevel().getIndex());
        assertEquals(assessmentReport.assessment().maturityLevel().getTitle(), result.assessment().maturityLevel().getTitle());
        assertEquals(assessmentReport.assessment().maturityLevel().getValue(), result.assessment().maturityLevel().getValue());
        assertEquals(assessmentReport.assessment().assessmentKit().id(), result.assessment().assessmentKit().id());
        assertEquals(assessmentReport.assessment().assessmentKit().title(), result.assessment().assessmentKit().title());
        assertEquals(assessmentReport.assessment().assessmentKit().summary(), result.assessment().assessmentKit().summary());
        assertEquals(assessmentReport.assessment().assessmentKit().maturityLevelCount(), result.assessment().assessmentKit().maturityLevelCount());
        assertEquals(assessmentReport.assessment().assessmentKit().expertGroup().id(), result.assessment().assessmentKit().expertGroup().id());
        assertEquals(assessmentReport.assessment().assessmentKit().expertGroup().title(), result.assessment().assessmentKit().expertGroup().title());

        assertNotNull(result.topStrengths());
        assertEquals(1, result.topStrengths().size());
        assertNotNull(result.topWeaknesses());
        assertEquals(2, result.topWeaknesses().size());

        assertEquals(assessmentReport.subjects().size(), result.subjects().size());
    }

    @Test
    void testReportAssessment_CurrentUserHasNotAccess_ThrowException() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        ReportAssessmentUseCase.Param param = new ReportAssessmentUseCase.Param(assessmentId, currentUserId);

        when(checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.reportAssessment(param));
    }
}
