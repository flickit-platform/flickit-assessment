package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentReportMetadata;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentSubjectReportItem;
import org.flickit.assessment.core.application.domain.report.AttributeReportItem;
import org.flickit.assessment.core.application.domain.report.QuestionnaireReportItem;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportUseCase;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentReportMother;
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
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_GRAPHICAL_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelThree;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentReportServiceTest {

    @InjectMocks
    private GetAssessmentReportService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentReportInfoPort loadAssessmentReportInfoPort;

    @Mock
    private LoadAssessmentReportPort loadAssessmentReportPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Test
    void testGetAssessmentReport_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(GetAssessmentReportUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentReport(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentReportInfoPort, loadAssessmentReportPort, validateAssessmentResultPort);
    }

    @Test
    void testGetAssessmentReport_whenAssessmentCalculateIsNotValid_thenThrowException() {
        var param = createParam(GetAssessmentReportUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);
        doThrow(new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID))
            .when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var throwable = assertThrows(CalculateNotValidException.class, () -> service.getAssessmentReport(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        verifyNoInteractions(loadAssessmentReportInfoPort, loadAssessmentReportPort);
    }

    @Test
    void testGetAssessmentReport_whenAssessmentCalculateIsValid_thenReturnReport() {
        var param = createParam(GetAssessmentReportUseCase.Param.ParamBuilder::build);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        AssessmentReportItem assessmentReport = createAssessmentReportItem(param);

        MaturityLevel teamLevel = MaturityLevelMother.levelTwo();
        var attributeReportItem = new AttributeReportItem(3L, "Agility", "agility of team",
            "in very good state", 1, 63.0, levelThree());
        var subjects = List.of(new AssessmentSubjectReportItem(2L, "team", 2, "subjectDesc2",
            "subject Insight", 58.6, teamLevel, List.of(attributeReportItem)));
        var assessmentReportInfo = new LoadAssessmentReportInfoPort.Result(assessmentReport, subjects);
        var reportMetadata = new AssessmentReportMetadata("intro", "pros", "steps", "participants");
        AssessmentReport report = AssessmentReportMother.reportWithMetadata(reportMetadata);

        when(loadAssessmentReportInfoPort.load(param.getAssessmentId())).thenReturn(assessmentReportInfo);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(report));

        var result = service.getAssessmentReport(param);

        assertEquals(assessmentReport.title(), result.assessment().title());
        assertEquals(report.getMetadata().intro(), result.assessment().intro());
        assertEquals(assessmentReport.insight(), result.assessment().overallInsight());
        assertEquals(report.getMetadata().prosAndCons(), result.assessment().prosAndCons());
        assertEquals(assessmentReport.maturityLevel().getId(), result.assessment().maturityLevel().id());
        assertEquals(assessmentReport.maturityLevel().getTitle(), result.assessment().maturityLevel().title());
        assertEquals(assessmentReport.assessmentKit().id(), result.assessment().assessmentKit().id());
        assertEquals(assessmentReport.assessmentKit().title(), result.assessment().assessmentKit().title());
        assertEquals(subjects.size(), result.subjects().size());
        var expectedSubjectItem = subjects.getFirst();
        var actualSubjectItem = result.subjects().getFirst();
        assertEquals(expectedSubjectItem.id(), actualSubjectItem.id());
        assertEquals(expectedSubjectItem.title(), actualSubjectItem.title());
        assertEquals(expectedSubjectItem.index(), actualSubjectItem.index());
        assertEquals(expectedSubjectItem.confidenceValue(), actualSubjectItem.confidenceValue());
        assertEquals(expectedSubjectItem.insight(), actualSubjectItem.insight());
        assertEquals(expectedSubjectItem.id(), actualSubjectItem.id());
        assertEquals(expectedSubjectItem.maturityLevel().getId(), actualSubjectItem.maturityLevel().id());
        assertEquals(expectedSubjectItem.attributes().size(), actualSubjectItem.attributes().size());
        assertEquals(expectedSubjectItem.attributes().getFirst().id(), actualSubjectItem.attributes().getFirst().id());
        assertEquals(expectedSubjectItem.attributes().getFirst().index(), actualSubjectItem.attributes().getFirst().index());
        assertEquals(expectedSubjectItem.attributes().getFirst().title(), actualSubjectItem.attributes().getFirst().title());
        assertEquals(expectedSubjectItem.attributes().getFirst().insight(), actualSubjectItem.attributes().getFirst().insight());
        assertEquals(expectedSubjectItem.attributes().getFirst().description(), actualSubjectItem.attributes().getFirst().description());
        assertEquals(expectedSubjectItem.attributes().getFirst().confidenceValue(), actualSubjectItem.attributes().getFirst().confidenceValue());
        assertEquals(expectedSubjectItem.attributes().getFirst().maturityLevel().getId(), actualSubjectItem.attributes().getFirst().maturityLevel().id());
    }

    private AssessmentReportItem createAssessmentReportItem(GetAssessmentReportUseCase.Param param) {
        AssessmentReportItem.Space space = new AssessmentReportItem.Space(1563L, "Space");
        return new AssessmentReportItem(param.getAssessmentId(),
            "assessmentTitle",
            "shortAssessmentTitle",
            "assessment insight",
            createAssessmentKit(),
            MaturityLevelMother.levelTwo(),
            1.5,
            true,
            true,
            LocalDateTime.now(),
            LocalDateTime.now(),
            space);
    }

    private AssessmentReportItem.AssessmentKitItem createAssessmentKit() {
        return new AssessmentReportItem.AssessmentKitItem(15L,
            "kit title",
            "kit summary",
            "about",
            5,
            150,
            MaturityLevelMother.allLevels(),
            List.of(new QuestionnaireReportItem(14L, "questionnaire title", "questionnaire description", 1, 15)),
            new AssessmentReportItem.AssessmentKitItem.ExpertGroup(569L, "expert group", null));
    }

    private GetAssessmentReportUseCase.Param createParam(Consumer<GetAssessmentReportUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAssessmentReportUseCase.Param.ParamBuilder paramBuilder() {
        return GetAssessmentReportUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }

    private AssessmentReport createReport() {
        return new AssessmentReport(null,
            null,
            new AssessmentReportMetadata("intro", "pros", "steps", "participants"),
            false,
            LocalDateTime.now(),
            LocalDateTime.now(),
            UUID.randomUUID(),
            UUID.randomUUID());
    }
}