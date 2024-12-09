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
    private LoadAttributeScoreStatsPort loadAttributeScoreStatsPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testGetAttributeScoreStats_ValidParam() {
        var param = createParam(GetAttributeScoreStatsUseCase.Param.ParamBuilder::build);

        List<LoadAttributeScoreStatsPort.Result> mockStats = List.of(
            new LoadAttributeScoreStatsPort.Result(1L, 5.0, 1.0, false),
            new LoadAttributeScoreStatsPort.Result(2L, 4.0, 0.5, false),
            new LoadAttributeScoreStatsPort.Result(3L, 3.0, 0.0, false),
            new LoadAttributeScoreStatsPort.Result(4L, 3.0, null, false),
            new LoadAttributeScoreStatsPort.Result(5L, 0.0, null, true)
        );

        when(loadAttributeScoreStatsPort.loadScoreStats(param.getAssessmentId(), param.getAttributeId(), param.getMaturityLevelId())).thenReturn(mockStats);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ATTRIBUTE_SCORE_DETAIL)).thenReturn(true);

        GetAttributeScoreStatsUseCase.Result result = service.getAttributeScoreStats(param);

        assertNotNull(result);
        assertEquals(15.0, result.maxPossibleScore());
        assertEquals(7.0, result.gainedScore());
        assertEquals(7.0 / 15.0, result.gainedScorePercentage());
        assertEquals(5, result.questionsCount());
    }

    @Test
    void testGetAttributeScoreStats_ValidParam_NoQuestionScore() {
        var param = createParam(GetAttributeScoreStatsUseCase.Param.ParamBuilder::build);

        when(loadAttributeScoreStatsPort.loadScoreStats(param.getAssessmentId(), param.getAttributeId(), param.getMaturityLevelId())).thenReturn(List.of());
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
