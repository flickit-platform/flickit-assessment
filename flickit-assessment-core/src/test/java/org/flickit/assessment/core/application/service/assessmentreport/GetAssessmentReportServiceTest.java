package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.InvalidStateException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentSubjectReportItem;
import org.flickit.assessment.core.application.domain.report.AttributeReportItem;
import org.flickit.assessment.core.application.domain.report.QuestionnaireReportItem;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentReportUseCase;
import org.flickit.assessment.core.application.port.out.adviceitem.LoadAdviceItemsPort;
import org.flickit.assessment.core.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentQuestionsPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentReportMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
import org.flickit.assessment.core.test.fixture.application.SpaceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.common.exception.api.ErrorCodes.REPORT_UNPUBLISHED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_REPORT_REPORT_NOT_PUBLISHED;
import static org.flickit.assessment.core.test.fixture.application.AdviceItemMother.adviceItem;
import static org.flickit.assessment.core.test.fixture.application.AnswerMother.answer;
import static org.flickit.assessment.core.test.fixture.application.AnswerOptionMother.optionFour;
import static org.flickit.assessment.core.test.fixture.application.AnswerOptionMother.optionOne;
import static org.flickit.assessment.core.test.fixture.application.AssessmentReportMetadataMother.fullMetadata;
import static org.flickit.assessment.core.test.fixture.application.AssessmentReportMother.publishedReportWithMetadata;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.*;
import static org.flickit.assessment.core.test.fixture.application.MeasureMother.createMeasure;
import static org.junit.jupiter.api.Assertions.*;
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
    private LoadAssessmentQuestionsPort loadAssessmentQuestionsPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Mock
    private LoadAdviceItemsPort loadAdviceItemsPort;

    @Mock
    private LoadSpacePort loadSpacePort;

    @Test
    void testGetAssessmentReport_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        var param = createParam(GetAssessmentReportUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentReport(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentReportInfoPort,
            loadAssessmentReportPort,
            loadAssessmentQuestionsPort,
            validateAssessmentResultPort,
            loadAdviceItemsPort,
            loadAdviceNarrationPort,
            loadSpacePort);
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

        verifyNoInteractions(loadAssessmentReportInfoPort,
            loadAssessmentReportPort,
            loadAssessmentQuestionsPort,
            loadAdviceItemsPort,
            loadAdviceNarrationPort,
            loadSpacePort);
    }

    @Test
    void testGetAssessmentReport_whenReportNotPublishedAndUserDoesNotHavePreviewPermission_thenThrowException() {
        var param = createParam(GetAssessmentReportUseCase.Param.ParamBuilder::build);

        AssessmentReport report = AssessmentReportMother.reportWithMetadata(fullMetadata());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(report));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_REPORT_PREVIEW))
            .thenReturn(false);

        var throwable = assertThrows(InvalidStateException.class, () -> service.getAssessmentReport(param));
        assertEquals(REPORT_UNPUBLISHED, throwable.getCode());
        assertEquals(GET_ASSESSMENT_REPORT_REPORT_NOT_PUBLISHED, throwable.getMessage());

        verify(assessmentAccessChecker, times(2))
            .isAuthorized(eq(param.getAssessmentId()), eq(param.getCurrentUserId()), any(AssessmentPermission.class));
        verifyNoInteractions(loadAdviceItemsPort,
            loadAssessmentQuestionsPort,
            loadAdviceNarrationPort);
    }

    @Test
    void testGetAssessmentReport_whenReportEntityNotExistsAndUserHasPreviewPermission_thenReturnEmptyReport() {
        var param = createParam(GetAssessmentReportUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createBasicSpace();

        var assessmentReport = createAssessmentReportItem(param);
        var attributeReportItem1 = new AttributeReportItem(15L, "Agility", "agility of team",
            "in very good state", 1, 3, 63.0, levelThree());
        var attributeReportItem2 = new AttributeReportItem(15L, "Agility", "agility of team",
            "in very good state", 1, 3, 63.0, levelFive());
        var subjects = List.of(new AssessmentSubjectReportItem(2L, "team", 2,
            "subject Insight", 58.6, levelTwo(), List.of(attributeReportItem1)),
            new AssessmentSubjectReportItem(3L, "team", 3,
                "subject Insight", 58.6, levelFive(), List.of(attributeReportItem2)));
        var assessmentReportInfo = new LoadAssessmentReportInfoPort.Result(assessmentReport, subjects);
        var adviceNarration = "assessor narration";
        var adviceItems = List.of(adviceItem(), adviceItem());

        var measure = assessmentReport.assessmentKit().measures().getFirst();
        var questionAnswers = List.of(new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure),
            answer(optionOne())));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentReportInfoPort.load(param.getAssessmentId())).thenReturn(assessmentReportInfo);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.empty());
        when(loadAdviceNarrationPort.load(assessmentReport.assessmentResultId())).thenReturn(adviceNarration);
        when(loadAdviceItemsPort.loadAll(assessmentReport.assessmentResultId())).thenReturn(adviceItems);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_REPORT_PREVIEW))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(true);
        when(loadAssessmentQuestionsPort.loadApplicableQuestions(param.getAssessmentId()))
            .thenReturn(questionAnswers);
        when(loadSpacePort.loadSpace(param.getAssessmentId())).thenReturn(Optional.of(space));

        var result = service.getAssessmentReport(param);

        assertAssessmentReport(assessmentReport, result, AssessmentReportMother.empty());
        assertEquals(subjects.size(), result.subjects().size());
        var expectedSubjectItem1 = subjects.getFirst();
        var expectedSubjectItem2 = subjects.getLast();
        var actualSubjectItem1 = result.subjects().getFirst();
        var actualSubjectItem2 = result.subjects().getLast();
        assertSubjectItem(expectedSubjectItem1, actualSubjectItem1);
        assertSubjectItem(expectedSubjectItem2, actualSubjectItem2);
        var expectedAttributeItem1 = expectedSubjectItem1.attributes().getFirst();
        var expectedAttributeItem2 = expectedSubjectItem2.attributes().getFirst();
        var actualAttributeItem1 = actualSubjectItem1.attributes().getFirst();
        var actualAttributeItem2 = actualSubjectItem2.attributes().getFirst();
        assertAttributeItem(expectedAttributeItem1, actualAttributeItem1);
        assertAttributeItem(expectedAttributeItem2, actualAttributeItem2);
        assertEquals(adviceNarration, result.advice().narration());
        assertEquals(adviceItems.size(), result.advice().adviceItems().size());
        assertAdviceItem(adviceItems, result.advice().adviceItems(), assessmentReport.language());
        assertSpace(space, result.assessment().space());
        assertTrue(result.permissions().canViewDashboard());
        assertFalse(result.permissions().canShareReport());
        assertFalse(result.permissions().canManageVisibility());
        assertEquals(VisibilityType.RESTRICTED.name(), result.visibility());
        assertNull(result.linkHash());
        assertTrue(result.isAdvisable());

        verify(assessmentAccessChecker, times(3))
            .isAuthorized(eq(param.getAssessmentId()), eq(param.getCurrentUserId()), any(AssessmentPermission.class));
    }

    @Test
    void testGetAssessmentReport_whenAssessmentCalculateIsValidAndUserHasNotViewDashboardPermission_thenReturnReport() {
        var param = createParam(GetAssessmentReportUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createPremiumSpace();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        AssessmentReportItem assessmentReport = createAssessmentReportItem(param);

        var teamLevel = levelTwo();
        var attributeReportItem = new AttributeReportItem(15L, "Agility", "agility of team",
            "in very good state", 1, 3, 63.0, levelFive());
        var subjects = List.of(new AssessmentSubjectReportItem(2L, "team", 2,
            "subject Insight", 58.6, teamLevel, List.of(attributeReportItem)));
        var assessmentReportInfo = new LoadAssessmentReportInfoPort.Result(assessmentReport, subjects);
        AssessmentReport report = publishedReportWithMetadata(fullMetadata());
        var adviceNarration = "assessor narration";
        var adviceItems = List.of(adviceItem(), adviceItem());

        var measure1 = assessmentReport.assessmentKit().measures().getFirst();
        var measure2 = assessmentReport.assessmentKit().measures().getLast();
        var questionAnswers = List.of(
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure1), answer(optionOne())),
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure2), answer(optionFour())));

        when(loadAssessmentReportInfoPort.load(param.getAssessmentId())).thenReturn(assessmentReportInfo);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(report));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            .thenReturn(true);
        when(loadAssessmentQuestionsPort.loadApplicableQuestions(param.getAssessmentId()))
            .thenReturn(questionAnswers);
        when(loadAdviceItemsPort.loadAll(assessmentReport.assessmentResultId())).thenReturn(adviceItems);
        when(loadAdviceNarrationPort.load(assessmentReport.assessmentResultId())).thenReturn(adviceNarration);
        when(loadSpacePort.loadSpace(param.getAssessmentId())).thenReturn(Optional.of(space));

        var result = service.getAssessmentReport(param);

        assertAssessmentReport(assessmentReport, result, report);
        assertEquals(subjects.size(), result.subjects().size());
        var expectedSubjectItem = subjects.getFirst();
        var actualSubjectItem = result.subjects().getFirst();
        assertSubjectItem(expectedSubjectItem, actualSubjectItem);
        var expectedAttributeItem = expectedSubjectItem.attributes().getFirst();
        var actualAttributeItem = actualSubjectItem.attributes().getFirst();
        assertAttributeItem(expectedAttributeItem, actualAttributeItem);
        assertEquals(adviceNarration, result.advice().narration());
        assertAdviceItem(adviceItems, result.advice().adviceItems(), assessmentReport.language());
        assertSpace(space, result.assessment().space());
        assertFalse(result.permissions().canViewDashboard());
        assertFalse(result.permissions().canShareReport());
        assertTrue(result.permissions().canManageVisibility());
        assertEquals(report.getVisibility().name(), result.visibility());
        assertEquals(report.getLinkHash(), result.linkHash());
        assertFalse(result.isAdvisable());
    }

    @Test
    void testGetAssessmentReport_whenIsNotAdvisableAndThereIsNotAdvice_thenReturnReport() {
        var param = createParam(GetAssessmentReportUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createDefaultSpace();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        AssessmentReportItem assessmentReport = createAssessmentReportItem(param);

        var teamLevel = levelTwo();
        var attributeReportItem = new AttributeReportItem(15L, "Agility", "agility of team",
            "in very good state", 1, 3, 63.0, levelFive());
        var subjects = List.of(new AssessmentSubjectReportItem(2L, "team", 2,
            "subject Insight", 58.6, teamLevel, List.of(attributeReportItem)));
        var assessmentReportInfo = new LoadAssessmentReportInfoPort.Result(assessmentReport, subjects);
        AssessmentReport report = publishedReportWithMetadata(fullMetadata());

        var measure1 = assessmentReport.assessmentKit().measures().getFirst();
        var measure2 = assessmentReport.assessmentKit().measures().getLast();
        var questionAnswers = List.of(
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure1), answer(optionOne())),
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure2), answer(optionFour())));

        when(loadAssessmentReportInfoPort.load(param.getAssessmentId())).thenReturn(assessmentReportInfo);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(report));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            .thenReturn(true);
        when(loadAssessmentQuestionsPort.loadApplicableQuestions(param.getAssessmentId()))
            .thenReturn(questionAnswers);
        when(loadAdviceItemsPort.loadAll(assessmentReport.assessmentResultId())).thenReturn(List.of());
        when(loadAdviceNarrationPort.load(assessmentReport.assessmentResultId())).thenReturn(null);
        when(loadSpacePort.loadSpace(param.getAssessmentId())).thenReturn(Optional.of(space));

        var result = service.getAssessmentReport(param);

        assertAssessmentReport(assessmentReport, result, report);
        assertEquals(subjects.size(), result.subjects().size());
        var expectedSubjectItem = subjects.getFirst();
        var actualSubjectItem = result.subjects().getFirst();
        assertSubjectItem(expectedSubjectItem, actualSubjectItem);
        var expectedAttributeItem = expectedSubjectItem.attributes().getFirst();
        var actualAttributeItem = actualSubjectItem.attributes().getFirst();
        assertAttributeItem(expectedAttributeItem, actualAttributeItem);
        assertSpace(space, result.assessment().space());
        assertNull(result.advice().narration());
        assertEquals(0, result.advice().adviceItems().size());
        assertFalse(result.permissions().canViewDashboard());
        assertFalse(result.permissions().canShareReport());
        assertTrue(result.permissions().canManageVisibility());
        assertEquals(report.getVisibility().name(), result.visibility());
        assertEquals(report.getLinkHash(), result.linkHash());
        assertFalse(result.isAdvisable());
    }

    @Test
    void testGetAssessmentReport_whenIsAdvisableAndThereIsNotAdvice_thenReturnReport() {
        var param = createParam(GetAssessmentReportUseCase.Param.ParamBuilder::build);
        var space = SpaceMother.createDefaultSpace();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        AssessmentReportItem assessmentReport = createAssessmentReportItem(param);

        var teamLevel = levelTwo();
        var attributeReportItem = new AttributeReportItem(15L, "Agility", "agility of team",
            "in very good state", 1, 3, 63.0, levelThree());
        var subjects = List.of(new AssessmentSubjectReportItem(2L, "team", 2,
            "subject Insight", 58.6, teamLevel, List.of(attributeReportItem)));
        var assessmentReportInfo = new LoadAssessmentReportInfoPort.Result(assessmentReport, subjects);
        AssessmentReport report = publishedReportWithMetadata(fullMetadata());

        var measure1 = assessmentReport.assessmentKit().measures().getFirst();
        var measure2 = assessmentReport.assessmentKit().measures().getLast();
        var questionAnswers = List.of(
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure1), answer(optionOne())),
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure2), answer(optionFour())));

        when(loadAssessmentReportInfoPort.load(param.getAssessmentId())).thenReturn(assessmentReportInfo);
        when(loadAssessmentReportPort.load(param.getAssessmentId())).thenReturn(Optional.of(report));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            .thenReturn(true);
        when(loadAssessmentQuestionsPort.loadApplicableQuestions(param.getAssessmentId()))
            .thenReturn(questionAnswers);
        when(loadAdviceItemsPort.loadAll(assessmentReport.assessmentResultId())).thenReturn(List.of());
        when(loadAdviceNarrationPort.load(assessmentReport.assessmentResultId())).thenReturn(null);
        when(loadSpacePort.loadSpace(param.getAssessmentId())).thenReturn(Optional.of(space));

        var result = service.getAssessmentReport(param);

        assertAssessmentReport(assessmentReport, result, report);
        assertEquals(subjects.size(), result.subjects().size());
        var expectedSubjectItem = subjects.getFirst();
        var actualSubjectItem = result.subjects().getFirst();
        assertSubjectItem(expectedSubjectItem, actualSubjectItem);
        assertNull(result.advice().narration());
        assertTrue(result.advice().adviceItems().isEmpty());
        var expectedAttributeItem = expectedSubjectItem.attributes().getFirst();
        var actualAttributeItem = actualSubjectItem.attributes().getFirst();
        assertAttributeItem(expectedAttributeItem, actualAttributeItem);
        assertSpace(space, result.assessment().space());
        assertFalse(result.permissions().canViewDashboard());
        assertFalse(result.permissions().canShareReport());
        assertTrue(result.permissions().canManageVisibility());
        assertEquals(report.getVisibility().name(), result.visibility());
        assertEquals(report.getLinkHash(), result.linkHash());
        assertTrue(result.isAdvisable());
    }

    private AssessmentReportItem createAssessmentReportItem(GetAssessmentReportUseCase.Param param) {
        return new AssessmentReportItem(param.getAssessmentId(),
            UUID.randomUUID(),
            "assessmentTitle",
            "assessment insight",
            createAssessmentKit(),
            levelTwo(),
            1.5,
            KitLanguage.FA,
            AssessmentMode.QUICK,
            LocalDateTime.now()
        );
    }

    private AssessmentReportItem.AssessmentKitItem createAssessmentKit() {
        return new AssessmentReportItem.AssessmentKitItem(15L,
            "kit title",
            5,
            150,
            MaturityLevelMother.allLevels(),
            List.of(new QuestionnaireReportItem(14L, "questionnaire title", "questionnaire description", 1, 15)),
            List.of(createMeasure(), createMeasure()));
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

    private void assertAssessmentReport(AssessmentReportItem assessmentReport, GetAssessmentReportUseCase.Result result, AssessmentReport report) {
        assertEquals(assessmentReport.title(), result.assessment().title());
        assertEquals(report.getMetadata().intro(), result.assessment().intro());
        assertEquals(assessmentReport.insight(), result.assessment().overallInsight());
        assertEquals(report.getMetadata().prosAndCons(), result.assessment().prosAndCons());
        assertEquals(assessmentReport.maturityLevel().getId(), result.assessment().maturityLevel().id());
        assertEquals(assessmentReport.maturityLevel().getTitle(), result.assessment().maturityLevel().title());
        assertEquals(assessmentReport.assessmentKit().id(), result.assessment().assessmentKit().id());
        assertEquals(assessmentReport.assessmentKit().title(), result.assessment().assessmentKit().title());
        assertEquals(assessmentReport.mode().getCode(), result.assessment().mode().code());
    }

    private void assertSubjectItem(AssessmentSubjectReportItem expectedSubjectItem, GetAssessmentReportUseCase.Subject actualSubjectItem) {
        assertEquals(expectedSubjectItem.id(), actualSubjectItem.id());
        assertEquals(expectedSubjectItem.title(), actualSubjectItem.title());
        assertEquals(expectedSubjectItem.index(), actualSubjectItem.index());
        assertEquals(expectedSubjectItem.confidenceValue(), actualSubjectItem.confidenceValue());
        assertEquals(expectedSubjectItem.insight(), actualSubjectItem.insight());
        assertEquals(expectedSubjectItem.id(), actualSubjectItem.id());
        assertEquals(expectedSubjectItem.maturityLevel().getId(), actualSubjectItem.maturityLevel().id());
        assertEquals(expectedSubjectItem.attributes().size(), actualSubjectItem.attributes().size());
    }

    private void assertAttributeItem(AttributeReportItem expectedAttributeItem, GetAssessmentReportUseCase.Attribute actualAttributeItem) {
        assertEquals(expectedAttributeItem.id(), actualAttributeItem.id());
        assertEquals(expectedAttributeItem.index(), actualAttributeItem.index());
        assertEquals(expectedAttributeItem.title(), actualAttributeItem.title());
        assertEquals(expectedAttributeItem.insight(), actualAttributeItem.insight());
        assertEquals(expectedAttributeItem.description(), actualAttributeItem.description());
        assertEquals(expectedAttributeItem.confidenceValue(), actualAttributeItem.confidenceValue());
        assertEquals(expectedAttributeItem.maturityLevel().getId(), actualAttributeItem.maturityLevel().id());
        assertEquals(expectedAttributeItem.weight(), actualAttributeItem.weight());
    }

    private void assertAdviceItem(List<AdviceItem> expectedAdviceItems, List<GetAssessmentReportUseCase.AdviceItem> actualAdviceItems, KitLanguage kitLanguage) {
        Locale locale = Locale.of(kitLanguage.name());
        assertThat(actualAdviceItems)
            .zipSatisfy(expectedAdviceItems, (actual, expected) -> {
                assertEquals(expected.getId(), actual.id());
                assertEquals(expected.getTitle(), actual.title());
                assertEquals(expected.getDescription(), actual.description());
                assertEquals(expected.getCost().getTitle(locale), actual.cost().title());
                assertEquals(expected.getCost().getCode(), actual.cost().code());
                assertEquals(expected.getPriority().getTitle(locale), actual.priority().title());
                assertEquals(expected.getPriority().getCode(), actual.priority().code());
                assertEquals(expected.getImpact().getTitle(locale), actual.impact().title());
                assertEquals(expected.getImpact().getCode(), actual.impact().code());
            });
    }

    private void assertSpace(Space space, GetAssessmentReportUseCase.SpaceDto spaceDto) {
        assertEquals(space.getId(), spaceDto.id());
        assertEquals(space.getTitle(), spaceDto.title());
        assertEquals(space.isDefault(), spaceDto.isDefault());
    }
}
