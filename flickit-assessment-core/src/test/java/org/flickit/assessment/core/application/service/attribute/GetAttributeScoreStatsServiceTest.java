package org.flickit.assessment.core.application.service.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreStatsUseCase;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributeScoreStatsPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ATTRIBUTE_SCORE_DETAIL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAttributeScoreStatsServiceTest {

    @InjectMocks
    private GetAttributeScoreStatsService service;

    @Mock
    private LoadAttributeScoresPort loadAttributeScoresPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testGetAttributeScoreStats_CurrentUserDoesNotHaveRequiredPermission_ThrowsException() {
        var param = createParam(GetAttributeScoreStatsUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_SCORE_DETAIL))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAttributeScoreStats(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAttributeScoresPort);
    }

    @Test
    void testGetAttributeScoreStats_ValidParam() {
        var param = createParam(GetAttributeScoreStatsUseCase.Param.ParamBuilder::build);

        List<LoadAttributeScoresPort.Result> scores = List.of(
            new LoadAttributeScoresPort.Result(1L, 5, 1.0, false),
            new LoadAttributeScoresPort.Result(2L, 4, 0.5, false),
            new LoadAttributeScoresPort.Result(3L, 3, 0.0, false),
            new LoadAttributeScoresPort.Result(4L, 3, null, false),
            new LoadAttributeScoresPort.Result(5L, 1, null, true)
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_SCORE_DETAIL)).thenReturn(true);
        when(loadAttributeScoresPort.loadScores(param.getAssessmentId(), param.getAttributeId(), param.getMaturityLevelId())).thenReturn(scores);

        var result = service.getAttributeScoreStats(param);

        var expectedMaxPossibleScore = 5 + 4 + 3 + 3; // Last question is excluded because it's marked as notApplicable.
        var expectedGainedScore = (5 * 1.0) + (4 * 0.5); // 7.0
        var expectedGainedScorePercentage = (expectedGainedScore / expectedMaxPossibleScore) * 100;

        assertNotNull(result);
        assertEquals(expectedMaxPossibleScore, result.maxPossibleScore());
        assertEquals(expectedGainedScore, result.gainedScore());
        assertEquals(expectedGainedScorePercentage, result.gainedScorePercentage());
        assertEquals(scores.size(), result.questionsCount());
    }

    @Test
    void testGetAttributeScoreStats_ValidParam_NoQuestionScore() {
        var param = createParam(GetAttributeScoreStatsUseCase.Param.ParamBuilder::build);

        when(loadAttributeScoresPort.loadScores(param.getAssessmentId(), param.getAttributeId(), param.getMaturityLevelId())).thenReturn(List.of());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_SCORE_DETAIL)).thenReturn(true);

        var result = service.getAttributeScoreStats(param);

        assertNotNull(result);
        assertEquals(0, result.gainedScore());
        assertEquals(0, result.maxPossibleScore());
    }

    @Test
    void testGetAttributeScoreStats_InvalidCurrentUser_ThrowsException() {
        var param = createParam(GetAttributeScoreStatsUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_SCORE_DETAIL))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAttributeScoreStats(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    private GetAttributeScoreStatsUseCase.Param createParam(Consumer<GetAttributeScoreStatsUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetAttributeScoreStatsUseCase.Param.ParamBuilder paramBuilder() {
        return GetAttributeScoreStatsUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .attributeId(1L)
            .maturityLevelId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
