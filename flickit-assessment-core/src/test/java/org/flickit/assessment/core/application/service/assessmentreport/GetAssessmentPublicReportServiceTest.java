package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AdviceItem;
import org.flickit.assessment.core.application.domain.AssessmentReport;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentSubjectReportItem;
import org.flickit.assessment.core.application.domain.report.AttributeReportItem;
import org.flickit.assessment.core.application.domain.report.QuestionnaireReportItem;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentPublicReportUseCase;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentPublicReportUseCase.Attribute;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentPublicReportUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentPublicReportUseCase.Result;
import org.flickit.assessment.core.application.port.in.assessmentreport.GetAssessmentPublicReportUseCase.Subject;
import org.flickit.assessment.core.application.port.out.adviceitem.LoadAdviceItemsPort;
import org.flickit.assessment.core.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentQuestionsPort;
import org.flickit.assessment.core.application.port.out.assessmentreport.LoadAssessmentReportPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentReportInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentuserrole.LoadUserRoleForAssessmentPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentReportMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.core.test.fixture.application.QuestionMother;
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
import static org.flickit.assessment.core.application.domain.AssessmentUserRole.VIEWER;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_REPORT_LINK_HASH_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AdviceItemMother.adviceItem;
import static org.flickit.assessment.core.test.fixture.application.AnswerMother.answer;
import static org.flickit.assessment.core.test.fixture.application.AnswerOptionMother.optionFour;
import static org.flickit.assessment.core.test.fixture.application.AnswerOptionMother.optionOne;
import static org.flickit.assessment.core.test.fixture.application.AssessmentReportMetadataMother.fullMetadata;
import static org.flickit.assessment.core.test.fixture.application.AssessmentReportMother.publishedReportWithMetadata;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelThree;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.flickit.assessment.core.test.fixture.application.MeasureMother.createMeasure;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentPublicReportServiceTest {

    @InjectMocks
    private GetAssessmentPublicReportService service;

    @Mock
    private LoadAssessmentReportPort loadAssessmentReportPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentReportInfoPort loadAssessmentReportInfoPort;

    @Mock
    private LoadAssessmentQuestionsPort loadAssessmentQuestionsPort;

    @Mock
    private LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Mock
    private LoadAdviceItemsPort loadAdviceItemsPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadUserRoleForAssessmentPort loadUserRoleForAssessmentPort;

    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();
    private final AssessmentReport restrictedReport = AssessmentReportMother.restrictedAndPublishedReport();
    private final AssessmentReport notPublishedReport = AssessmentReportMother.publicAndNotPublishedReport();
    private final AssessmentReport publicReport = AssessmentReportMother.publicAndPublishedReport();
    private final Param paramWithUserId = createParam(Param.ParamBuilder::build);

    @Test
    void testGetAssessmentPublicReport_whenReportIsRestrictedAndUserIsNotLoggedIn_thenThrowResourceNotFoundException() {
        Param param = createParam(p -> p.currentUserId(null));
        when(loadAssessmentReportPort.loadByLinkHash(param.getLinkHash())).thenReturn(restrictedReport);
        when(loadAssessmentResultPort.load(restrictedReport.getAssessmentResultId())).thenReturn(Optional.of(assessmentResult));
        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentPublicReport(param));

        assertEquals(ASSESSMENT_REPORT_LINK_HASH_NOT_FOUND, exception.getMessage());
        verifyNoInteractions(assessmentAccessChecker,
            loadAssessmentReportInfoPort,
            loadAssessmentQuestionsPort,
            loadAdviceNarrationPort,
            loadAdviceItemsPort,
            loadUserRoleForAssessmentPort);
    }

    @Test
    void testGetAssessmentPublicReport_whenReportIsUnpublishedAndUserIsNotLoggedIn_thenThrowResourceNotFoundException() {
        Param param = createParam(p -> p.currentUserId(null));
        when(loadAssessmentReportPort.loadByLinkHash(param.getLinkHash())).thenReturn(notPublishedReport);
        when(loadAssessmentResultPort.load(notPublishedReport.getAssessmentResultId())).thenReturn(Optional.of(assessmentResult));
        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentPublicReport(param));

        assertEquals(ASSESSMENT_REPORT_LINK_HASH_NOT_FOUND, exception.getMessage());
        verifyNoInteractions(assessmentAccessChecker,
            loadAssessmentReportInfoPort,
            loadAssessmentQuestionsPort,
            loadAdviceNarrationPort,
            loadAdviceItemsPort,
            loadUserRoleForAssessmentPort);
    }

    @Test
    void testGetAssessmentPublicReport_whenReportIsRestrictedAndLoggedInUserHasNoRole_thenThrowResourceNotFoundException() {
        Param param = createParam(Param.ParamBuilder::build);
        when(loadAssessmentReportPort.loadByLinkHash(param.getLinkHash())).thenReturn(restrictedReport);
        when(loadAssessmentResultPort.load(restrictedReport.getAssessmentResultId())).thenReturn(Optional.of(assessmentResult));
        when(loadUserRoleForAssessmentPort.load(assessmentResult.getAssessment().getId(), param.getCurrentUserId()))
            .thenReturn(Optional.empty());
        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentPublicReport(param));

        assertEquals(ASSESSMENT_REPORT_LINK_HASH_NOT_FOUND, exception.getMessage());

        verifyNoInteractions(assessmentAccessChecker,
            loadAssessmentReportInfoPort,
            loadAssessmentQuestionsPort,
            loadAdviceNarrationPort,
            loadAdviceItemsPort);
    }

    @Test
    void testGetAssessmentPublicReport_whenReportIsUnpublishedAndLoggedInUserHasNoRole_thenThrowResourceNotFoundException() {
        Param param = createParam(Param.ParamBuilder::build);
        when(loadAssessmentReportPort.loadByLinkHash(param.getLinkHash())).thenReturn(notPublishedReport);
        when(loadAssessmentResultPort.load(notPublishedReport.getAssessmentResultId())).thenReturn(Optional.of(assessmentResult));
        when(loadUserRoleForAssessmentPort.load(assessmentResult.getAssessment().getId(), param.getCurrentUserId()))
            .thenReturn(Optional.empty());
        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentPublicReport(param));

        assertEquals(ASSESSMENT_REPORT_LINK_HASH_NOT_FOUND, exception.getMessage());

        verifyNoInteractions(assessmentAccessChecker,
            loadAssessmentReportInfoPort,
            loadAssessmentQuestionsPort,
            loadAdviceNarrationPort,
            loadAdviceItemsPort);
    }

    @Test
    void testGetAssessmentPublicReport_whenReportIsPublicAndUserIsNotLoggedIn_thenReturnReportWithoutPermissionsOrRoles() {
        var param = createParam(p -> p.currentUserId(null));
        var assessmentId = assessmentResult.getAssessment().getId();

        when(loadAssessmentReportPort.loadByLinkHash(param.getLinkHash())).thenReturn(publicReport);
        when(loadAssessmentResultPort.load(publicReport.getAssessmentResultId())).thenReturn(Optional.of(assessmentResult));

        var assessmentReport = createAssessmentReportItem(assessmentId);

        var teamLevel = levelTwo();
        var attributeReportItem = new AttributeReportItem(15L, "Agility", "agility of team",
            "in very good state", 1, 3, 63.0, levelThree());
        var subjects = List.of(new AssessmentSubjectReportItem(2L, "team", 2,
            "subject Insight", 58.6, teamLevel, List.of(attributeReportItem)));
        var assessmentReportInfo = new LoadAssessmentReportInfoPort.Result(assessmentReport, subjects);

        var report = publishedReportWithMetadata(fullMetadata());
        var adviceNarration = "assessor narration";
        var adviceItems = List.of(adviceItem(), adviceItem());

        var measure1 = assessmentReport.assessmentKit().measures().getFirst();
        var measure2 = assessmentReport.assessmentKit().measures().getLast();
        var questionAnswers = List.of(
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure1), answer(optionOne())),
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure2), answer(optionFour())));

        when(loadAssessmentReportInfoPort.load(assessmentId)).thenReturn(assessmentReportInfo);
        when(loadAdviceNarrationPort.load(assessmentReport.assessmentResultId())).thenReturn(adviceNarration);
        when(loadAdviceItemsPort.loadAll(assessmentReport.assessmentResultId())).thenReturn(adviceItems);
        when(loadAssessmentQuestionsPort.loadApplicableQuestions(assessmentId))
            .thenReturn(questionAnswers);

        var result = service.getAssessmentPublicReport(param);

        assertAssessmentReport(assessmentReport, result, report);
        assertEquals(subjects.size(), result.subjects().size());
        var expectedSubjectItem = subjects.getFirst();
        var actualSubjectItem = result.subjects().getFirst();
        assertSubjectItem(expectedSubjectItem, actualSubjectItem);
        var expectedAttributeItem = expectedSubjectItem.attributes().getFirst();
        var actualAttributeItem = actualSubjectItem.attributes().getFirst();
        assertAttributeItem(expectedAttributeItem, actualAttributeItem);
        assertEquals(adviceNarration, result.advice().narration());
        assertEquals(adviceItems.size(), result.advice().adviceItems().size());
        assertAdviceItem(adviceItems, result.advice().adviceItems(), assessmentReport.language());

        assertFalse(result.permissions().canViewDashboard());
        verifyNoInteractions(loadUserRoleForAssessmentPort, assessmentAccessChecker);
    }

    @Test
    void testGetAssessmentPublicReport_whenReportIsPublicAndLoggedInUserHasNoRole_thenReturnReportWithoutPermissionsOrRoles() {
        var assessmentId = assessmentResult.getAssessment().getId();

        when(loadAssessmentReportPort.loadByLinkHash(paramWithUserId.getLinkHash())).thenReturn(publicReport);
        when(loadAssessmentResultPort.load(publicReport.getAssessmentResultId())).thenReturn(Optional.of(assessmentResult));
        when(loadUserRoleForAssessmentPort.load(assessmentResult.getAssessment().getId(), paramWithUserId.getCurrentUserId()))
            .thenReturn(Optional.empty());

        var assessmentReport = createAssessmentReportItem(assessmentId);

        var teamLevel = levelTwo();
        var attributeReportItem = new AttributeReportItem(15L, "Agility", "agility of team",
            "in very good state", 1, 3, 63.0, levelThree());
        var subjects = List.of(new AssessmentSubjectReportItem(2L, "team", 2,
            "subject Insight", 58.6, teamLevel, List.of(attributeReportItem)));
        var assessmentReportInfo = new LoadAssessmentReportInfoPort.Result(assessmentReport, subjects);

        var report = publishedReportWithMetadata(fullMetadata());
        var adviceNarration = "assessor narration";
        var adviceItems = List.of(adviceItem(), adviceItem());

        var measure1 = assessmentReport.assessmentKit().measures().getFirst();
        var measure2 = assessmentReport.assessmentKit().measures().getLast();
        var questionAnswers = List.of(
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure1), answer(optionOne())),
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure2), answer(optionFour())));

        when(loadAssessmentReportInfoPort.load(assessmentId)).thenReturn(assessmentReportInfo);
        when(loadAdviceNarrationPort.load(assessmentReport.assessmentResultId())).thenReturn(adviceNarration);
        when(loadAdviceItemsPort.loadAll(assessmentReport.assessmentResultId())).thenReturn(adviceItems);
        when(loadAssessmentQuestionsPort.loadApplicableQuestions(assessmentId))
            .thenReturn(questionAnswers);

        var result = service.getAssessmentPublicReport(paramWithUserId);

        assertAssessmentReport(assessmentReport, result, report);
        assertEquals(subjects.size(), result.subjects().size());
        var expectedSubjectItem = subjects.getFirst();
        var actualSubjectItem = result.subjects().getFirst();
        assertSubjectItem(expectedSubjectItem, actualSubjectItem);
        var expectedAttributeItem = expectedSubjectItem.attributes().getFirst();
        var actualAttributeItem = actualSubjectItem.attributes().getFirst();
        assertAttributeItem(expectedAttributeItem, actualAttributeItem);
        assertEquals(adviceNarration, result.advice().narration());
        assertEquals(adviceItems.size(), result.advice().adviceItems().size());
        assertAdviceItem(adviceItems, result.advice().adviceItems(), assessmentReport.language());

        assertFalse(result.permissions().canViewDashboard());
        verifyNoInteractions(assessmentAccessChecker);
    }

    @Test
    void testGetAssessmentPublicReport_whenReportIsPublicAndLoggedInUserHasRole_thenReturnReportWithUserRole() {
        var assessmentId = assessmentResult.getAssessment().getId();
        var userRole = VIEWER;

        when(loadAssessmentReportPort.loadByLinkHash(paramWithUserId.getLinkHash())).thenReturn(publicReport);
        when(loadAssessmentResultPort.load(publicReport.getAssessmentResultId())).thenReturn(Optional.of(assessmentResult));
        when(loadUserRoleForAssessmentPort.load(assessmentId, paramWithUserId.getCurrentUserId()))
            .thenReturn(Optional.of(userRole));

        var assessmentReport = createAssessmentReportItem(assessmentId);

        var teamLevel = levelTwo();
        var attributeReportItem = new AttributeReportItem(15L, "Agility", "agility of team",
            "in very good state", 1, 3, 63.0, levelThree());
        var subjects = List.of(new AssessmentSubjectReportItem(2L, "team", 2,
            "subject Insight", 58.6, teamLevel, List.of(attributeReportItem)));
        var assessmentReportInfo = new LoadAssessmentReportInfoPort.Result(assessmentReport, subjects);

        var report = publishedReportWithMetadata(fullMetadata());
        var adviceNarration = "assessor narration";
        var adviceItems = List.of(adviceItem(), adviceItem());

        var measure1 = assessmentReport.assessmentKit().measures().getFirst();
        var measure2 = assessmentReport.assessmentKit().measures().getLast();
        var questionAnswers = List.of(
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure1), answer(optionOne())),
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure2), answer(optionFour())));

        when(loadAssessmentReportInfoPort.load(assessmentId)).thenReturn(assessmentReportInfo);
        when(loadAdviceNarrationPort.load(assessmentReport.assessmentResultId())).thenReturn(adviceNarration);
        when(loadAdviceItemsPort.loadAll(assessmentReport.assessmentResultId())).thenReturn(adviceItems);
        when(assessmentAccessChecker.isAuthorized(assessmentId, paramWithUserId.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(assessmentId, paramWithUserId.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(false);
        when(assessmentAccessChecker.isAuthorized(assessmentId, paramWithUserId.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            .thenReturn(true);

        when(loadAssessmentQuestionsPort.loadApplicableQuestions(assessmentId))
            .thenReturn(questionAnswers);

        var result = service.getAssessmentPublicReport(paramWithUserId);

        verify(assessmentAccessChecker, never()).isAuthorized(assessmentId, paramWithUserId.getCurrentUserId(), VIEW_GRAPHICAL_REPORT);

        assertAssessmentReport(assessmentReport, result, report);
        assertEquals(subjects.size(), result.subjects().size());
        var expectedSubjectItem = subjects.getFirst();
        var actualSubjectItem = result.subjects().getFirst();
        assertSubjectItem(expectedSubjectItem, actualSubjectItem);
        var expectedAttributeItem = expectedSubjectItem.attributes().getFirst();
        var actualAttributeItem = actualSubjectItem.attributes().getFirst();
        assertAttributeItem(expectedAttributeItem, actualAttributeItem);
        assertEquals(adviceNarration, result.advice().narration());
        assertEquals(adviceItems.size(), result.advice().adviceItems().size());
        assertAdviceItem(adviceItems, result.advice().adviceItems(), assessmentReport.language());
        assertTrue(result.permissions().canViewDashboard());
        assertFalse(result.permissions().canShareReport());
        assertTrue(result.permissions().canManageVisibility());
    }

    @Test
    void testGetAssessmentPublicReport_whenReportIsRestrictedAndLoggedInUserHasRoleAndHasViewGraphicalReportPermission_thenReturnReportWithPermissionsAndRole() {
        var assessmentId = assessmentResult.getAssessment().getId();
        var userRole = VIEWER;

        when(loadAssessmentReportPort.loadByLinkHash(paramWithUserId.getLinkHash())).thenReturn(restrictedReport);
        when(loadAssessmentResultPort.load(restrictedReport.getAssessmentResultId())).thenReturn(Optional.of(assessmentResult));
        when(loadUserRoleForAssessmentPort.load(assessmentId, paramWithUserId.getCurrentUserId()))
            .thenReturn(Optional.of(userRole));
        when(assessmentAccessChecker.isAuthorized(assessmentId, paramWithUserId.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);

        var assessmentReport = createAssessmentReportItem(assessmentId);

        var teamLevel = levelTwo();
        var attributeReportItem = new AttributeReportItem(15L, "Agility", "agility of team",
            "in very good state", 1, 3, 63.0, levelThree());
        var subjects = List.of(new AssessmentSubjectReportItem(2L, "team", 2,
            "subject Insight", 58.6, teamLevel, List.of(attributeReportItem)));
        var assessmentReportInfo = new LoadAssessmentReportInfoPort.Result(assessmentReport, subjects);

        var report = publishedReportWithMetadata(fullMetadata());
        var adviceNarration = "assessor narration";
        var adviceItems = List.of(adviceItem(), adviceItem());

        var measure1 = assessmentReport.assessmentKit().measures().getFirst();
        var measure2 = assessmentReport.assessmentKit().measures().getLast();
        var questionAnswers = List.of(
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure1), answer(optionOne())),
            new LoadAssessmentQuestionsPort.Result(QuestionMother.withMeasure(measure2), answer(optionFour())));

        when(loadAssessmentReportInfoPort.load(assessmentId)).thenReturn(assessmentReportInfo);
        when(loadAdviceNarrationPort.load(assessmentReport.assessmentResultId())).thenReturn(adviceNarration);
        when(loadAdviceItemsPort.loadAll(assessmentReport.assessmentResultId())).thenReturn(adviceItems);
        when(assessmentAccessChecker.isAuthorized(assessmentId, paramWithUserId.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(assessmentId, paramWithUserId.getCurrentUserId(), GRANT_ACCESS_TO_REPORT))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(assessmentId, paramWithUserId.getCurrentUserId(), MANAGE_ASSESSMENT_REPORT_VISIBILITY))
            .thenReturn(false);
        when(loadAssessmentQuestionsPort.loadApplicableQuestions(assessmentId))
            .thenReturn(questionAnswers);

        var result = service.getAssessmentPublicReport(paramWithUserId);

        assertAssessmentReport(assessmentReport, result, report);
        assertEquals(subjects.size(), result.subjects().size());
        var expectedSubjectItem = subjects.getFirst();
        var actualSubjectItem = result.subjects().getFirst();
        assertSubjectItem(expectedSubjectItem, actualSubjectItem);
        var expectedAttributeItem = expectedSubjectItem.attributes().getFirst();
        var actualAttributeItem = actualSubjectItem.attributes().getFirst();
        assertAttributeItem(expectedAttributeItem, actualAttributeItem);
        assertEquals(adviceNarration, result.advice().narration());
        assertEquals(adviceItems.size(), result.advice().adviceItems().size());
        assertAdviceItem(adviceItems, result.advice().adviceItems(), assessmentReport.language());
        assertTrue(result.permissions().canViewDashboard());
        assertTrue(result.permissions().canShareReport());
        assertFalse(result.permissions().canManageVisibility());
    }

    @Test
    void testGetAssessmentPublicReport_whenReportIsRestrictedAndLoggedInUserHasRoleAndLacksViewGraphicalReportPermission_thenThrowResourceNotFoundException() {
        when(loadAssessmentReportPort.loadByLinkHash(paramWithUserId.getLinkHash())).thenReturn(notPublishedReport);
        when(loadAssessmentResultPort.load(notPublishedReport.getAssessmentResultId())).thenReturn(Optional.of(assessmentResult));
        when(loadUserRoleForAssessmentPort.load(assessmentResult.getAssessment().getId(), paramWithUserId.getCurrentUserId()))
            .thenReturn(Optional.of(VIEWER));
        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), paramWithUserId.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(false);
        var exception = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentPublicReport(paramWithUserId));

        assertEquals(ASSESSMENT_REPORT_LINK_HASH_NOT_FOUND, exception.getMessage());

        verifyNoInteractions(loadAssessmentReportInfoPort,
            loadAssessmentQuestionsPort,
            loadAdviceNarrationPort,
            loadAdviceItemsPort);
    }

    private AssessmentReportItem createAssessmentReportItem(UUID assessmentId) {
        return new AssessmentReportItem(assessmentId,
            UUID.randomUUID(),
            "assessmentTitle",
            "assessment insight",
            createAssessmentKit(),
            levelTwo(),
            1.5,
            KitLanguage.FA,
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

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .linkHash(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }

    private void assertAssessmentReport(AssessmentReportItem assessmentReport, Result result, AssessmentReport report) {
        assertEquals(assessmentReport.title(), result.assessment().title());
        assertEquals(report.getMetadata().intro(), result.assessment().intro());
        assertEquals(assessmentReport.insight(), result.assessment().overallInsight());
        assertEquals(report.getMetadata().prosAndCons(), result.assessment().prosAndCons());
        assertEquals(assessmentReport.maturityLevel().getId(), result.assessment().maturityLevel().id());
        assertEquals(assessmentReport.maturityLevel().getTitle(), result.assessment().maturityLevel().title());
        assertEquals(assessmentReport.assessmentKit().id(), result.assessment().assessmentKit().id());
        assertEquals(assessmentReport.assessmentKit().title(), result.assessment().assessmentKit().title());
    }

    private void assertSubjectItem(AssessmentSubjectReportItem expectedSubjectItem, Subject actualSubjectItem) {
        assertEquals(expectedSubjectItem.id(), actualSubjectItem.id());
        assertEquals(expectedSubjectItem.title(), actualSubjectItem.title());
        assertEquals(expectedSubjectItem.index(), actualSubjectItem.index());
        assertEquals(expectedSubjectItem.confidenceValue(), actualSubjectItem.confidenceValue());
        assertEquals(expectedSubjectItem.insight(), actualSubjectItem.insight());
        assertEquals(expectedSubjectItem.id(), actualSubjectItem.id());
        assertEquals(expectedSubjectItem.maturityLevel().getId(), actualSubjectItem.maturityLevel().id());
        assertEquals(expectedSubjectItem.attributes().size(), actualSubjectItem.attributes().size());
    }

    private void assertAttributeItem(AttributeReportItem expectedAttributeItem, Attribute actualAttributeItem) {
        assertEquals(expectedAttributeItem.id(), actualAttributeItem.id());
        assertEquals(expectedAttributeItem.index(), actualAttributeItem.index());
        assertEquals(expectedAttributeItem.title(), actualAttributeItem.title());
        assertEquals(expectedAttributeItem.insight(), actualAttributeItem.insight());
        assertEquals(expectedAttributeItem.description(), actualAttributeItem.description());
        assertEquals(expectedAttributeItem.confidenceValue(), actualAttributeItem.confidenceValue());
        assertEquals(expectedAttributeItem.maturityLevel().getId(), actualAttributeItem.maturityLevel().id());
        assertEquals(expectedAttributeItem.weight(), actualAttributeItem.weight());
    }

    private void assertAdviceItem(List<AdviceItem> expectedAdviceItems, List<GetAssessmentPublicReportUseCase.AdviceItem> actualAdviceItems, KitLanguage kitLanguage) {
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
}
