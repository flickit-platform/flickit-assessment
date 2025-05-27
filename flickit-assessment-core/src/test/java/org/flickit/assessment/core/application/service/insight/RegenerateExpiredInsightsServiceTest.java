package org.flickit.assessment.core.application.service.insight;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.insight.RegenerateExpiredInsightsUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.GENERATE_ALL_ASSESSMENT_INSIGHTS;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegenerateExpiredInsightsServiceTest {

    @InjectMocks
    private RegenerateExpiredInsightsService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private RegenerateExpiredInsightsHelper regenerateExpiredInsightsHelper;

    private final Param param = createParam(Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResultWithLanguage(KitLanguage.FA, KitLanguage.EN);

    @Test
    void testRegenerateExpiredInsights_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.regenerateExpiredInsights(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            loadAssessmentResultPort,
            regenerateExpiredInsightsHelper);
    }

    @Test
    void testRegenerateExpiredInsights_whenAssessmentResultIsNotFound_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.regenerateExpiredInsights(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            regenerateExpiredInsightsHelper);
    }

    @Test
    void testRegenerateExpiredInsights_whenCalculatedResultIsNotValid_thenThrowCalculateNotValidException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doThrow(new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID))
            .when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var throwable = assertThrows(CalculateNotValidException.class, () -> service.regenerateExpiredInsights(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        verifyNoInteractions(regenerateExpiredInsightsHelper);
    }

    @Test
    void testRegenerateExpiredInsights_whenParametersAreValid_thenRegenerateExpiredInsights() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), GENERATE_ALL_ASSESSMENT_INSIGHTS))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        doNothing().when(regenerateExpiredInsightsHelper).regenerateExpiredInsights(assessmentResult, Locale.of(assessmentResult.getLanguage().getCode()));

        service.regenerateExpiredInsights(param);
    }

    private Param createParam(Consumer<Param.ParamBuilder> changer) {
        var newParam = paramBuilder();
        changer.accept(newParam);
        return newParam.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
