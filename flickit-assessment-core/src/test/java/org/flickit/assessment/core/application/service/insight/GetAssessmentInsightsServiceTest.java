package org.flickit.assessment.core.application.service.insight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.domain.insight.Insight;
import org.flickit.assessment.core.application.port.in.insight.GetAssessmentInsightsUseCase.*;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.CountMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuePort;
import org.flickit.assessment.core.application.service.insight.assessment.GetAssessmentInsightHelper;
import org.flickit.assessment.core.application.service.insight.attribute.GetAttributeInsightHelper;
import org.flickit.assessment.core.application.service.insight.subject.GetSubjectInsightHelper;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.InsightMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentInsightsServiceTest {

    @InjectMocks
    private GetAssessmentInsightsService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private GetAssessmentInsightHelper getAssessmentInsightHelper;

    @Mock
    private LoadSubjectValuePort loadSubjectValuePort;

    @Mock
    private GetSubjectInsightHelper getSubjectInsightHelper;

    @Mock
    private LoadAttributeValuePort loadAttributeValuePort;

    @Mock
    private GetAttributeInsightHelper getAttributeInsightHelper;

    @Mock
    private CountMaturityLevelsPort countMaturityLevelsPort;

    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();

    @Test
    void testGetAssessmentInsightsService_whenCurrentUserDoesNotHaveRequiredPermissions_thenThrowAccessDeniedException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentInsights(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            validateAssessmentResultPort,
            getAssessmentInsightHelper,
            loadSubjectValuePort,
            getSubjectInsightHelper,
            loadAttributeValuePort,
            getAttributeInsightHelper,
            countMaturityLevelsPort);
    }

    @Test
    void testGetAssessmentInsightsService_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        var param = createParam(Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentInsights(param));
        assertEquals(GET_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            getAssessmentInsightHelper,
            loadSubjectValuePort,
            getSubjectInsightHelper,
            loadAttributeValuePort,
            getAttributeInsightHelper,
            countMaturityLevelsPort);
    }

    @Test
    void testGetAssessmentInsightsService_whenNoInsightExists_thenReturnsEmptyInsights() {
        var param = createParam(Param.ParamBuilder::build);
        var emptyInsight = InsightMother.emptyInsight();
        var subjectValue1 = SubjectValueMother.createSubjectValue();
        var attributeValues1 = subjectValue1.getAttributeValues();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(countMaturityLevelsPort.count(assessmentResult.getKitVersionId()))
            .thenReturn(MaturityLevelMother.allLevels().size());
        when(getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId()))
            .thenReturn(emptyInsight);
        when(loadSubjectValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(List.of(subjectValue1));
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(loadAttributeValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(attributeValues1);
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT))
            .thenReturn(true);

        var result = service.getAssessmentInsights(param);

        assertAssessment(assessmentResult, result.assessment());
        assertEmptyEditableInsight(result.assessment().insight());

        assertSubject(subjectValue1, result.subjects().getFirst());
        assertEmptyEditableInsight(result.subjects().getFirst().insight());

        assertEquals(attributeValues1.size(), result.subjects().getFirst().attributes().size());

        for (int attributeIndex = 0; attributeIndex < result.subjects().getFirst().attributes().size(); attributeIndex++) {
            var expectedAttributeValue = attributeValues1.get(attributeIndex);
            var actualAttribute = result.subjects().getFirst().attributes().get(attributeIndex);
            assertAttribute(expectedAttributeValue, actualAttribute);
            assertEmptyEditableInsight(actualAttribute.insight());
        }

        assertEquals(4, result.issues().notGenerated());
        assertEquals(0, result.issues().unapproved());
        assertEquals(0, result.issues().expired());
    }

    @Test
    void testGetAssessmentInsightsService_whenAssessmentInsightIsExpired_thenReturnsExpiredInsight() {
        var param = createParam(Param.ParamBuilder::build);
        var expiredInsight = InsightMother.defaultInsightWithMinLastModificationTime();
        var subjectValue1 = SubjectValueMother.createSubjectValue();
        var attributeValues1 = subjectValue1.getAttributeValues();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId()))
            .thenReturn(expiredInsight);
        when(countMaturityLevelsPort.count(assessmentResult.getKitVersionId()))
            .thenReturn(MaturityLevelMother.allLevels().size());
        when(loadSubjectValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(List.of(subjectValue1));
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(loadAttributeValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(attributeValues1);
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT))
            .thenReturn(true);

        var result = service.getAssessmentInsights(param);

        assertAssessment(assessmentResult, result.assessment());
        assertInsight(expiredInsight, result.assessment().insight());

        assertSubject(subjectValue1, result.subjects().getFirst());
        assertEmptyEditableInsight(result.subjects().getFirst().insight());

        assertEquals(attributeValues1.size(), result.subjects().getFirst().attributes().size());
        for (int attributeIndex = 0; attributeIndex < result.subjects().getFirst().attributes().size(); attributeIndex++) {
            var expectedAttributeValue = attributeValues1.get(attributeIndex);
            var actualAttribute = result.subjects().getFirst().attributes().get(attributeIndex);
            assertAttribute(expectedAttributeValue, actualAttribute);
            assertEmptyEditableInsight(actualAttribute.insight());
        }

        assertEquals(3, result.issues().notGenerated());
        assertEquals(0, result.issues().unapproved());
        assertEquals(1, result.issues().expired());
    }

    @Test
    void testGetAssessmentInsightsService_whenAssessmentInsightIsExpiredAndSubjectInsightIsExpiredAndUnapproved_thenReturnsResult() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentInsight = InsightMother.defaultInsightWithMinLastModificationTime();
        var subjectInsight = InsightMother.unapprovedAssessorInsightWithMinLastModificationTime();
        var subjectValue1 = SubjectValueMother.createSubjectValue();
        var attributeValues1 = subjectValue1.getAttributeValues();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId()))
            .thenReturn(assessmentInsight);
        when(loadSubjectValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(List.of(subjectValue1));
        when(countMaturityLevelsPort.count(assessmentResult.getKitVersionId()))
            .thenReturn(MaturityLevelMother.allLevels().size());
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of(subjectValue1.getSubject().getId(), subjectInsight));
        when(loadAttributeValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(attributeValues1);
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT))
            .thenReturn(true);

        var result = service.getAssessmentInsights(param);

        assertAssessment(assessmentResult, result.assessment());
        assertInsight(assessmentInsight, result.assessment().insight());

        assertSubject(subjectValue1, result.subjects().getFirst());
        assertInsight(subjectInsight, result.subjects().getFirst().insight());

        assertEquals(attributeValues1.size(), result.subjects().getFirst().attributes().size());
        for (int attributeIndex = 0; attributeIndex < result.subjects().getFirst().attributes().size(); attributeIndex++) {
            var expectedAttributeValue = attributeValues1.get(attributeIndex);
            var actualAttribute = result.subjects().getFirst().attributes().get(attributeIndex);
            assertAttribute(expectedAttributeValue, actualAttribute);
            assertEmptyEditableInsight(actualAttribute.insight());
        }

        assertEquals(2, result.issues().notGenerated());
        assertEquals(1, result.issues().unapproved());
        assertEquals(2, result.issues().expired());
    }

    @Test
    void testGetAssessmentInsightsService_whenAllInsightsExistsAndAssessmentInsightIsExpiredAndUnapproved_thenReturnsAllInsights() {
        var param = createParam(Param.ParamBuilder::build);
        var assessmentInsight = InsightMother.unapprovedAssessorInsightWithMinLastModificationTime();
        var defaultInsight = InsightMother.defaultInsight();
        var subjectValue1 = SubjectValueMother.createSubjectValue();
        var attributeValues1 = subjectValue1.getAttributeValues();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(getAssessmentInsightHelper.getAssessmentInsight(assessmentResult, param.getCurrentUserId()))
            .thenReturn(assessmentInsight);
        when(loadSubjectValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(List.of(subjectValue1));
        when(countMaturityLevelsPort.count(assessmentResult.getKitVersionId()))
            .thenReturn(MaturityLevelMother.allLevels().size());
        when(getSubjectInsightHelper.getSubjectInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of(subjectValue1.getSubject().getId(), defaultInsight));
        when(loadAttributeValuePort.loadAll(assessmentResult.getId()))
            .thenReturn(attributeValues1);
        when(getAttributeInsightHelper.getAttributeInsights(assessmentResult, param.getCurrentUserId()))
            .thenReturn(Map.of(
                attributeValues1.getFirst().getAttribute().getId(), defaultInsight,
                attributeValues1.get(1).getAttribute().getId(), defaultInsight));
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_SUBJECT_INSIGHT))
            .thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT))
            .thenReturn(true);

        var result = service.getAssessmentInsights(param);

        assertAssessment(assessmentResult, result.assessment());
        assertInsight(assessmentInsight, result.assessment().insight());

        assertSubject(subjectValue1, result.subjects().getFirst());
        assertInsight(defaultInsight, result.subjects().getFirst().insight());

        assertEquals(attributeValues1.size(), result.subjects().getFirst().attributes().size());

        for (int attributeIndex = 0; attributeIndex < result.subjects().getFirst().attributes().size(); attributeIndex++) {
            var expectedAttributeValue = attributeValues1.get(attributeIndex);
            var actualAttribute = result.subjects().getFirst().attributes().get(attributeIndex);
            assertAttribute(expectedAttributeValue, actualAttribute);
            assertInsight(defaultInsight, actualAttribute.insight());
        }

        assertEquals(0, result.issues().notGenerated());
        assertEquals(1, result.issues().unapproved());
        assertEquals(1, result.issues().expired());
    }

    private void assertAssessment(AssessmentResult assessmentResult, AssessmentModel assessment) {
        assertEquals(assessmentResult.getAssessment().getId(), assessment.id());
        assertEquals(assessmentResult.getAssessment().getTitle(), assessment.title());
        assertEquals(assessmentResult.getConfidenceValue(), assessment.confidenceValue());
        assertEquals(assessmentResult.getIsCalculateValid(), assessment.isCalculateValid());
        assertEquals(assessmentResult.getIsConfidenceValid(), assessment.isConfidenceValid());
        assertMaturityLevel(assessmentResult.getMaturityLevel(), assessment.maturityLevel());
    }

    private void assertMaturityLevel(MaturityLevel expected, MaturityLevelModel actual) {
        assertEquals(expected.getId(), actual.id());
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getValue(), actual.value());
        assertEquals(expected.getIndex(), actual.index());
    }

    private void assertEmptyEditableInsight(InsightModel insight) {
        assertNull(insight.defaultInsight());
        assertNull(insight.assessorInsight());
        assertTrue(insight.editable());
        assertNull(insight.approved());
    }

    private void assertSubject(SubjectValue expected, SubjectModel actual) {
        assertEquals(expected.getSubject().getId(), actual.id());
        assertEquals(expected.getSubject().getTitle(), actual.title());
        assertEquals(expected.getSubject().getDescription(), actual.description());
        assertEquals(expected.getSubject().getIndex(), actual.index());
        assertEquals(expected.getSubject().getWeight(), actual.weight());
        assertMaturityLevel(expected.getMaturityLevel(), actual.maturityLevel());
        assertEquals(expected.getConfidenceValue(), actual.confidenceValue());
    }

    private void assertAttribute(AttributeValue expectedAttributeValue, AttributeModel actualAttribute) {
        assertEquals(expectedAttributeValue.getAttribute().getId(), actualAttribute.id());
        assertEquals(expectedAttributeValue.getAttribute().getTitle(), actualAttribute.title());
        assertEquals(expectedAttributeValue.getAttribute().getDescription(), actualAttribute.description());
        assertEquals(expectedAttributeValue.getAttribute().getIndex(), actualAttribute.index());
        assertEquals(expectedAttributeValue.getAttribute().getWeight(), actualAttribute.weight());
        assertMaturityLevel(expectedAttributeValue.getMaturityLevel(), actualAttribute.maturityLevel());
    }

    private void assertInsight(Insight actual, InsightModel expected) {
        assertInsightDetail(actual.getDefaultInsight(), expected.defaultInsight());
        assertInsightDetail(actual.getAssessorInsight(), expected.assessorInsight());
        assertEquals(actual.isEditable(), expected.editable());
        assertEquals(actual.getApproved(), expected.approved());
    }

    private void assertInsightDetail(Insight.InsightDetail expected, InsightModel.InsightDetail actual) {
        if (expected == null)
            assertNull(actual);
        else {
            assertEquals(expected.getInsight(), actual.insight());
            assertEquals(expected.getCreationTime(), actual.creationTime());
            assertEquals(expected.isValid(), actual.isValid());
        }
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
