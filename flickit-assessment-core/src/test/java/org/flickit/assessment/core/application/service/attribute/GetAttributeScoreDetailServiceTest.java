package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoreDetailPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase.Param;
import static org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase.QuestionScore;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributeScoreDetailServiceTest {

    @InjectMocks
    private GetAttributeScoreDetailService service;

    @Mock
    private LoadAttributeScoreDetailPort loadAttributeScoreDetailPort;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Test
    void testGetAttributeScoreDetail_ValidParam() {
        UUID assessmentId = UUID.randomUUID();
        long attributeId = 123L;
        long maturityLevelId = 124L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(
            assessmentId,
            attributeId,
            maturityLevelId,
            currentUserId);

        QuestionScore questionWithScore = new QuestionScore(
            "DevOps",
            1,
            "Do you have CI/CD?",
            2,
            2,
            "Yes",
            0.5,
            1.0);
        QuestionScore questionWithoutScore = new QuestionScore(
            "DevOps",
            3,
            "Do you have Test?",
            1,
            1,
            "NO",
            0.0,
            0.0);
        QuestionScore questionWithoutAnswer = new QuestionScore(
            "Technology",
            10,
            "Do you have Technology?",
            4,
            null,
            null,
            null,
            0.0);

        List<QuestionScore> scores = List.of(
            questionWithScore,
            questionWithoutScore,
            questionWithoutAnswer
        );

        when(loadAttributeScoreDetailPort.loadScoreDetail(assessmentId, attributeId, maturityLevelId)).thenReturn(scores);
        when(checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId)).thenReturn(true);

        GetAttributeScoreDetailUseCase.Result result = service.getAttributeScoreDetail(param);

        assertNotNull(result);
        assertEquals(1, result.gainedScore());
        assertEquals(7, result.totalScore());
        assertEquals(3, result.questionScores().size());
    }

    @Test
    void testGetAttributeScoreDetail_ValidParam_NoQuestionScore() {
        UUID assessmentId = UUID.randomUUID();
        long attributeId = 123L;
        long maturityLevelId = 124L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(
            assessmentId,
            attributeId,
            maturityLevelId,
            currentUserId);

        List<QuestionScore> scores = List.of();
        when(loadAttributeScoreDetailPort.loadScoreDetail(assessmentId, attributeId, maturityLevelId)).thenReturn(scores);
        when(checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId)).thenReturn(true);

        GetAttributeScoreDetailUseCase.Result result = service.getAttributeScoreDetail(param);

        assertNotNull(result);
        assertEquals(0, result.gainedScore());
        assertEquals(0, result.totalScore());
        assertTrue(result.questionScores().isEmpty());
    }

    @Test
    void testGetAttributeScoreDetail_InvalidCurrentUser_ThrowsException() {
        UUID assessmentId = UUID.randomUUID();
        long attributeId = 123L;
        long maturityLevelId = 124L;
        UUID currentUserId = UUID.randomUUID();
        Param param = new Param(
            assessmentId,
            attributeId,
            maturityLevelId,
            currentUserId);

        when(checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class,
            () -> service.getAttributeScoreDetail(param));
        assertThat(throwable).hasMessage(COMMON_CURRENT_USER_NOT_ALLOWED);
    }
}
