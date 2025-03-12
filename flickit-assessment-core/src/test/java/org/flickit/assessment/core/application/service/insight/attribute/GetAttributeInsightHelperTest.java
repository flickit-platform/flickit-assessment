package org.flickit.assessment.core.application.service.insight.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ATTRIBUTE_INSIGHT;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.aiInsightWithTime;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.attributeInsightWithTimes;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributeInsightHelperTest {

    @InjectMocks
    private GetAttributeInsightHelper helper;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAttributeInsightPort loadAttributeInsightPort;

    @Mock
    private LoadAttributeInsightsPort loadAttributeInsightsPort;

    private final AssessmentResult assessmentResult = validResult();
    private final Long attributeId = 123L;
    private final UUID currentUserId = UUID.randomUUID();

    @Test
    void testGetAttributeInsight_whenInsightDoesNotExistAndUserHasCreatePermission_thenReturnsDefaultResult() {
        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), attributeId)).thenReturn(Optional.empty());

        var result = helper.getAttributeInsight(assessmentResult, attributeId, currentUserId);

        assertNotNull(result);
        assertNull(result.getDefaultInsight());
        assertNull(result.getAssessorInsight());
        assertTrue(result.isEditable());
        assertNull(result.getApproved());
        verifyNoInteractions(loadAttributeInsightsPort);
    }

    @Test
    void testGetAttributeInsight_whenInsightDoesNotExistAndUserLacksCreatePermission_thenReturnsDefaultResult() {
        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(false);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), attributeId)).thenReturn(Optional.empty());

        var result = helper.getAttributeInsight(assessmentResult, attributeId, currentUserId);

        assertNotNull(result);
        assertNull(result.getDefaultInsight());
        assertNull(result.getAssessorInsight());
        assertFalse(result.isEditable());
        assertNull(result.getApproved());
        verifyNoInteractions(loadAttributeInsightsPort);
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsNewerThanAiInsightAndValid_thenReturnsAssessorInsight() {
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), attributeId)).thenReturn(Optional.of(attributeInsight));

        var result = helper.getAttributeInsight(assessmentResult, attributeId, currentUserId);

        assertNotNull(result);
        assertNull(result.getDefaultInsight());
        assertNotNull(result.getAssessorInsight());
        assertEquals(attributeInsight.getAssessorInsight(), result.getAssessorInsight().getInsight());
        assertEquals(attributeInsight.getAssessorInsightTime(), result.getAssessorInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getAssessorInsight().getLastModificationTime());
        assertTrue(result.getAssessorInsight().isValid());
        assertTrue(result.isEditable());
        assertTrue(result.getApproved());
        verifyNoInteractions(loadAttributeInsightsPort);
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsOlderThanAiInsightAndAiInsightIsValid_thenReturnsAiInsight() {
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), attributeId)).thenReturn(Optional.of(attributeInsight));

        var result = helper.getAttributeInsight(assessmentResult, attributeId, currentUserId);
        assertNotNull(result);
        assertNotNull(result.getDefaultInsight());
        assertNull(result.getAssessorInsight());
        assertEquals(attributeInsight.getAiInsight(), result.getDefaultInsight().getInsight());
        assertEquals(attributeInsight.getAiInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertTrue(result.getDefaultInsight().isValid());
        assertTrue(result.isEditable());
        assertTrue(result.getApproved());
        verifyNoInteractions(loadAttributeInsightsPort);
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsOlderThanAiInsightAndAiInsightIsNotValid_thenReturnsAiInsight() {
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), attributeId)).thenReturn(Optional.of(attributeInsight));

        var result = helper.getAttributeInsight(assessmentResult, attributeId, currentUserId);
        assertNotNull(result);
        assertNotNull(result.getDefaultInsight());
        assertNull(result.getAssessorInsight());
        assertEquals(attributeInsight.getAiInsight(), result.getDefaultInsight().getInsight());
        assertEquals(attributeInsight.getAiInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertFalse(result.getDefaultInsight().isValid());
        assertTrue(result.isEditable());
        assertTrue(result.getApproved());
        verifyNoInteractions(loadAttributeInsightsPort);
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsNewerThanAiInsightAndNotValid_thenReturnsAssessorInsight() {
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), attributeId)).thenReturn(Optional.of(attributeInsight));

        var result = helper.getAttributeInsight(assessmentResult, attributeId, currentUserId);
        assertNotNull(result);
        assertNull(result.getDefaultInsight());
        assertNotNull(result.getAssessorInsight());
        assertEquals(attributeInsight.getAssessorInsight(), result.getAssessorInsight().getInsight());
        assertEquals(attributeInsight.getAssessorInsightTime(), result.getAssessorInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getAssessorInsight().getLastModificationTime());
        assertFalse(result.getAssessorInsight().isValid());
        assertTrue(result.isEditable());
        assertTrue(result.getApproved());
        verifyNoInteractions(loadAttributeInsightsPort);
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsNullAndAiInsightIsNotValid_thenReturnsAiInsight() {
        var attributeInsight = aiInsightWithTime(LocalDateTime.now().minusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), attributeId)).thenReturn(Optional.of(attributeInsight));

        var result = helper.getAttributeInsight(assessmentResult, attributeId, currentUserId);
        assertNotNull(result);
        assertNotNull(result.getDefaultInsight());
        assertEquals(attributeInsight.getAiInsight(), result.getDefaultInsight().getInsight());
        assertEquals(attributeInsight.getAiInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertFalse(result.getDefaultInsight().isValid());
        assertNull(result.getAssessorInsight());
        assertTrue(result.isEditable());
        assertFalse(result.getApproved());
        verifyNoInteractions(loadAttributeInsightsPort);
    }

    @Test
    void testGetAttributeInsight_whenAssessorInsightIsNullAndAiInsightIsValid_thenReturnsAiInsight() {
        var attributeInsight = aiInsightWithTime(LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), attributeId)).thenReturn(Optional.of(attributeInsight));

        var result = helper.getAttributeInsight(assessmentResult, attributeId, currentUserId);
        assertNotNull(result);
        assertNotNull(result.getDefaultInsight());
        assertEquals(attributeInsight.getAiInsight(), result.getDefaultInsight().getInsight());
        assertEquals(attributeInsight.getAiInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertTrue(result.getDefaultInsight().isValid());
        assertNull(result.getAssessorInsight());
        assertTrue(result.isEditable());
        assertFalse(result.getApproved());
        verifyNoInteractions(loadAttributeInsightsPort);
    }

    @Test
    void testGetAttributeInsights_whenAssessorInsightIsNewerThanAiInsightAndValid_thenReturnsAssessorInsight() {
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight));

        var resultList = helper.getAttributeInsights(assessmentResult, currentUserId);
        assertNotNull(resultList);
        var result = resultList.get(attributeInsight.getAttributeId());

        assertNull(result.getDefaultInsight());
        assertNotNull(result.getAssessorInsight());
        assertEquals(attributeInsight.getAssessorInsight(), result.getAssessorInsight().getInsight());
        assertEquals(attributeInsight.getAssessorInsightTime(), result.getAssessorInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getAssessorInsight().getLastModificationTime());
        assertTrue(result.getAssessorInsight().isValid());
        assertTrue(result.isEditable());
        assertTrue(result.getApproved());
        verifyNoInteractions(loadAttributeInsightPort);
    }

    @Test
    void testGetAttributeInsights_whenAssessorInsightIsOlderThanAiInsightAndAiInsightIsValid_thenReturnsAiInsight() {
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight));

        var resultList = helper.getAttributeInsights(assessmentResult, currentUserId);
        assertNotNull(resultList);
        var result = resultList.get(attributeInsight.getAttributeId());

        assertNotNull(result.getDefaultInsight());
        assertNull(result.getAssessorInsight());
        assertEquals(attributeInsight.getAiInsight(), result.getDefaultInsight().getInsight());
        assertEquals(attributeInsight.getAiInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertTrue(result.getDefaultInsight().isValid());
        assertTrue(result.isEditable());
        assertTrue(result.getApproved());
        verifyNoInteractions(loadAttributeInsightPort);
    }

    @Test
    void testGetAttributeInsights_whenAssessorInsightIsOlderThanAiInsightAndAiInsightIsNotValid_thenReturnsAiInsight() {
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight));

        var resultList = helper.getAttributeInsights(assessmentResult, currentUserId);
        assertNotNull(resultList);
        var result = resultList.get(attributeInsight.getAttributeId());

        assertNotNull(result.getDefaultInsight());
        assertNull(result.getAssessorInsight());
        assertEquals(attributeInsight.getAiInsight(), result.getDefaultInsight().getInsight());
        assertEquals(attributeInsight.getAiInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertFalse(result.getDefaultInsight().isValid());
        assertTrue(result.isEditable());
        assertTrue(result.getApproved());
        verifyNoInteractions(loadAttributeInsightPort);
    }

    @Test
    void testGetAttributeInsights_whenAssessorInsightIsNewerThanAiInsightAndNotValid_thenReturnsAssessorInsight() {
        var attributeInsight = attributeInsightWithTimes(
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight));

        var resultList = helper.getAttributeInsights(assessmentResult, currentUserId);
        assertNotNull(resultList);
        var result = resultList.get(attributeInsight.getAttributeId());

        assertNull(result.getDefaultInsight());
        assertNotNull(result.getAssessorInsight());
        assertEquals(attributeInsight.getAssessorInsight(), result.getAssessorInsight().getInsight());
        assertEquals(attributeInsight.getAssessorInsightTime(), result.getAssessorInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getAssessorInsight().getLastModificationTime());
        assertFalse(result.getAssessorInsight().isValid());
        assertTrue(result.isEditable());
        assertTrue(result.getApproved());
        verifyNoInteractions(loadAttributeInsightPort);
    }

    @Test
    void testGetAttributeInsights_whenAssessorInsightIsNullAndAiInsightIsNotValid_thenReturnsAiInsight() {
        var attributeInsight = aiInsightWithTime(LocalDateTime.now().minusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight));

        var resultList = helper.getAttributeInsights(assessmentResult, currentUserId);
        assertNotNull(resultList);
        var result = resultList.get(attributeInsight.getAttributeId());

        assertNotNull(result.getDefaultInsight());
        assertEquals(attributeInsight.getAiInsight(), result.getDefaultInsight().getInsight());
        assertEquals(attributeInsight.getAiInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertFalse(result.getDefaultInsight().isValid());
        assertNull(result.getAssessorInsight());
        assertTrue(result.isEditable());
        assertFalse(result.getApproved());
        verifyNoInteractions(loadAttributeInsightPort);
    }

    @Test
    void testGetAttributeInsights_whenAssessorInsightIsNullAndAiInsightIsValid_thenReturnsAiInsight() {
        var attributeInsight = aiInsightWithTime(LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(assessmentResult.getAssessment().getId(), currentUserId, CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAttributeInsightsPort.loadInsights(assessmentResult.getId())).thenReturn(List.of(attributeInsight));

        var resultList = helper.getAttributeInsights(assessmentResult, currentUserId);
        assertNotNull(resultList);
        var result = resultList.get(attributeInsight.getAttributeId());

        assertNotNull(result.getDefaultInsight());
        assertEquals(attributeInsight.getAiInsight(), result.getDefaultInsight().getInsight());
        assertEquals(attributeInsight.getAiInsightTime(), result.getDefaultInsight().getCreationTime());
        assertEquals(attributeInsight.getLastModificationTime(), result.getDefaultInsight().getLastModificationTime());
        assertTrue(result.getDefaultInsight().isValid());
        assertNull(result.getAssessorInsight());
        assertTrue(result.isEditable());
        assertFalse(result.getApproved());
        verifyNoInteractions(loadAttributeInsightPort);
    }
}
