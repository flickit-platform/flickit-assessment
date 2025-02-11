package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem.Space;
import org.flickit.assessment.core.application.domain.report.AssessmentSubjectReportItem;
import org.flickit.assessment.core.application.internal.ValidateAssessmentResult;
import org.flickit.assessment.core.application.port.in.assessment.ReportAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.EXPORT_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
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
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadSpaceOwnerPort loadSpaceOwnerPort;

    @Mock
    private LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    @Test
    void testReportAssessment_ValidResult() {
        UUID currentUserId = UUID.randomUUID();
        var assessmentResult = validResult();
        UUID assessmentId = assessmentResult.getAssessment().getId();

        ReportAssessmentUseCase.Param param = new ReportAssessmentUseCase.Param(assessmentId, currentUserId);

        MaturityLevel softwareLevel = MaturityLevelMother.levelFour();
        MaturityLevel teamLevel = MaturityLevelMother.levelTwo();
        List<MaturityLevel> maturityLevels = List.of(softwareLevel, teamLevel);

        var expertGroup = new AssessmentReportItem.AssessmentKitItem.ExpertGroup(1L, "expertGroupTitle1", "picture/link");
        var kit = new AssessmentReportItem.AssessmentKitItem(1L, "kitTitle", "kitSummary", "about kit", KitLanguage.FA, 3, 156, maturityLevels, List.of(), expertGroup);
        MaturityLevel assessmentMaturityLevel = MaturityLevelMother.levelThree();
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime lastModificationTime = LocalDateTime.now();
        Space space = new Space(1563L, "Space");
        AssessmentReportItem assessment = new AssessmentReportItem(assessmentId,
            assessmentResult.getId(),
            "assessmentTitle",
            "shortAssessmentTitle",
            "assessment insight",
            kit,
            assessmentMaturityLevel,
            1.5,
            true,
            true,
            creationTime,
            lastModificationTime,
            space);

        var subjects = List.of(
            new AssessmentSubjectReportItem(1L, "software", 1, "subjectDesc1", "subject insight 1", 20.0, softwareLevel, List.of()),
            new AssessmentSubjectReportItem(2L, "team", 2, "subjectDesc2", "subject insight 2", 58.6, teamLevel, List.of()));
        var assessmentReport = new LoadAssessmentReportInfoPort.Result(assessment, subjects);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);
        doNothing().when(validateAssessmentResult).validate(param.getAssessmentId());
        when(loadReportInfoPort.load(assessmentId)).thenReturn(assessmentReport);
        when(loadSpaceOwnerPort.loadOwnerId(space.id())).thenReturn(currentUserId);

        ReportAssessmentUseCase.Result result = service.reportAssessment(param);

        assertNotNull(assessmentReport);
        assertNotNull(assessmentReport.assessment());
        assertEquals(assessmentReport.assessment().id(), result.assessment().id());
        assertEquals(assessmentReport.assessment().title(), result.assessment().title());
        assertEquals(assessmentReport.assessment().confidenceValue(), result.assessment().confidenceValue());
        assertEquals(assessmentReport.assessment().isCalculateValid(), result.assessment().isCalculateValid());
        assertEquals(assessmentReport.assessment().isConfidenceValid(), result.assessment().isConfidenceValid());
        assertEquals(assessmentReport.assessment().lastModificationTime(), result.assessment().lastModificationTime());
        assertEquals(assessmentReport.assessment().maturityLevel().getId(), result.assessment().maturityLevel().getId());
        assertEquals(assessmentReport.assessment().maturityLevel().getIndex(), result.assessment().maturityLevel().getIndex());
        assertEquals(assessmentReport.assessment().maturityLevel().getTitle(), result.assessment().maturityLevel().getTitle());
        assertEquals(assessmentReport.assessment().maturityLevel().getValue(), result.assessment().maturityLevel().getValue());
        assertEquals(assessmentReport.assessment().assessmentKit().id(), result.assessment().assessmentKit().id());
        assertEquals(assessmentReport.assessment().assessmentKit().title(), result.assessment().assessmentKit().title());
        assertEquals(assessmentReport.assessment().assessmentKit().summary(), result.assessment().assessmentKit().summary());
        assertEquals(assessmentReport.assessment().assessmentKit().about(), result.assessment().assessmentKit().about());
        assertEquals(assessmentReport.assessment().assessmentKit().maturityLevelCount(), result.assessment().assessmentKit().maturityLevelCount());
        assertEquals(assessmentReport.assessment().assessmentKit().maturityLevels(), result.assessment().assessmentKit().maturityLevels());
        assertEquals(assessmentReport.assessment().assessmentKit().expertGroup().id(), result.assessment().assessmentKit().expertGroup().id());
        assertEquals(assessmentReport.assessment().assessmentKit().expertGroup().title(), result.assessment().assessmentKit().expertGroup().title());
        assertEquals(space.id(), result.assessment().space().id());
        assertEquals(space.title(), result.assessment().space().title());
        assertTrue(result.assessmentPermissions().manageable());
        assertTrue(result.assessmentPermissions().exportable());

        assertEquals(assessmentReport.subjects().size(), result.subjects().size());
    }

    @Test
    void testReportAssessment_CurrentUserHasNotAccess_ThrowException() {
        UUID currentUserId = UUID.randomUUID();
        UUID assessmentId = UUID.randomUUID();
        ReportAssessmentUseCase.Param param = new ReportAssessmentUseCase.Param(assessmentId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.reportAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testReportAssessment_assessmentIsNotManageableByCurrentUser() {
        UUID currentUserId = UUID.randomUUID();
        var assessmentResult = validResult();
        UUID assessmentId = assessmentResult.getAssessment().getId();

        ReportAssessmentUseCase.Param param = new ReportAssessmentUseCase.Param(assessmentId, currentUserId);

        MaturityLevel teamLevel = MaturityLevelMother.levelTwo();
        Space space = new Space(1563L, "Space");
        AssessmentReportItem assessment = new AssessmentReportItem(assessmentId,
            assessmentResult.getId(),
            "assessmentTitle",
            "shortAssessmentTitle",
            null,
            null,
            MaturityLevelMother.levelTwo(),
            1.5,
            true,
            true,
            LocalDateTime.now(),
            LocalDateTime.now(),
            space);

        var subjects = List.of(
            new AssessmentSubjectReportItem(2L, "team", 2, "subjectDesc2", "subject insight 2", 58.6, teamLevel, List.of()));
        var assessmentReport = new LoadAssessmentReportInfoPort.Result(assessment, subjects);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        doNothing().when(validateAssessmentResult).validate(param.getAssessmentId());
        when(loadReportInfoPort.load(assessmentId)).thenReturn(assessmentReport);
        when(loadSpaceOwnerPort.loadOwnerId(space.id())).thenReturn(UUID.randomUUID());
        when(loadUserRoleForAssessmentPort.load(assessmentId, currentUserId)).thenReturn(Optional.of(AssessmentUserRole.VIEWER));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(false);

        ReportAssessmentUseCase.Result result = service.reportAssessment(param);

        assertNotNull(assessmentReport);
        assertNotNull(assessmentReport.assessment());
        assertFalse(result.assessmentPermissions().manageable());
        assertFalse(result.assessmentPermissions().exportable());

        assertEquals(assessmentReport.subjects().size(), result.subjects().size());
    }

    @Test
    void testReportAssessment_currentUserHasManagerRole() {
        UUID currentUserId = UUID.randomUUID();
        var assessmentResult = validResult();
        UUID assessmentId = assessmentResult.getAssessment().getId();

        ReportAssessmentUseCase.Param param = new ReportAssessmentUseCase.Param(assessmentId, currentUserId);

        MaturityLevel teamLevel = MaturityLevelMother.levelTwo();
        Space space = new Space(1563L, "Space");
        AssessmentReportItem assessment = new AssessmentReportItem(assessmentId,
            assessmentResult.getId(),
            "assessmentTitle",
            "shortAssessmentTitle",
            null,
            null,
            MaturityLevelMother.levelTwo(),
            1.5,
            true,
            true,
            LocalDateTime.now(),
            LocalDateTime.now(),
            space);

        var subjects = List.of(
            new AssessmentSubjectReportItem(2L, "team", 2, "subjectDesc2", "subject insight 2", 58.6, teamLevel, List.of()));
        var assessmentReport = new LoadAssessmentReportInfoPort.Result(assessment, subjects);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT)).thenReturn(true);
        doNothing().when(validateAssessmentResult).validate(param.getAssessmentId());
        when(loadReportInfoPort.load(assessmentId)).thenReturn(assessmentReport);
        when(loadSpaceOwnerPort.loadOwnerId(space.id())).thenReturn(UUID.randomUUID());
        when(loadUserRoleForAssessmentPort.load(assessmentId, currentUserId)).thenReturn(Optional.of(AssessmentUserRole.MANAGER));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), EXPORT_ASSESSMENT_REPORT)).thenReturn(true);

        ReportAssessmentUseCase.Result result = service.reportAssessment(param);

        assertTrue(result.assessmentPermissions().manageable());
        assertTrue(result.assessmentPermissions().exportable());
    }
}
