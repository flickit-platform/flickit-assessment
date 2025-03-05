package org.flickit.assessment.core.application.service.insight.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;
import org.flickit.assessment.core.application.port.in.insight.assessment.InitAssessmentInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.CreateAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.LoadAssessmentInsightPort;
import org.flickit.assessment.core.application.port.out.insight.assessment.UpdateAssessmentInsightPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentInsightMother.createDefaultInsightWithAssessmentResultId;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResultWithKitLanguage;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevel;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelFive;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitAssessmentInsightServiceTest {

    @InjectMocks
    private InitAssessmentInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAssessmentInsightPort loadAssessmentInsightPort;

    @Mock
    private CreateAssessmentInsightHelper createAssessmentInsightHelper;

    @Mock
    private CreateAssessmentInsightPort createAssessmentInsightPort;

    @Mock
    private UpdateAssessmentInsightPort updateAssessmentInsightPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    private final InitAssessmentInsightUseCase.Param param = createParam(InitAssessmentInsightUseCase.Param.ParamBuilder::build);

    @Test
    void testInitAssessmentInsight_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(false);

        var throwable = Assertions.assertThrows(AccessDeniedException.class, () -> service.initAssessmentInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            createAssessmentInsightPort,
            updateAssessmentInsightPort,
            validateAssessmentResultPort,
            loadAssessmentInsightPort);
    }

    @Test
    void testInitAssessmentInsight_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.initAssessmentInsight(param));
        assertEquals(INIT_ASSESSMENT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAssessmentInsightPort,
            createAssessmentInsightHelper,
            createAssessmentInsightPort,
            updateAssessmentInsightPort,
            validateAssessmentResultPort);
    }

    @Test
    void testInitAssessmentInsight_whenInsightNotExists_thenCrateAssessmentInsightAndPersist() {
        var assessmentResult = validResultWithKitLanguage(KitLanguage.FA);
        var newAssessmentInsight = createDefaultInsightWithAssessmentResultId(assessmentResult.getId());
        var locale = Locale.of(KitLanguage.FA.getCode());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(createAssessmentInsightHelper.createAssessmentInsight(assessmentResult, locale)).thenReturn(newAssessmentInsight);

        service.initAssessmentInsight(param);

        ArgumentCaptor<AssessmentInsight> createCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(createAssessmentInsightPort).persist(createCaptor.capture());
        assertEquals(newAssessmentInsight, createCaptor.getValue());

        verifyNoInteractions(updateAssessmentInsightPort);
    }

    @Test
    void testInitAssessmentInsight_whenInsightExists_thenUpdateInsight() {
        var assessmentResult = validResultWithSubjectValuesAndMaturityLevel(null, levelFive());
        var loadedAssessmentInsight = createDefaultInsightWithAssessmentResultId(assessmentResult.getId());
        var newAssessmentInsight = createDefaultInsightWithAssessmentResultId(assessmentResult.getId());
        var locale = Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAssessmentInsightPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(loadedAssessmentInsight));
        when(createAssessmentInsightHelper.createAssessmentInsight(assessmentResult, locale))
            .thenReturn(newAssessmentInsight);

        service.initAssessmentInsight(param);

        ArgumentCaptor<AssessmentInsight> createCaptor = ArgumentCaptor.forClass(AssessmentInsight.class);
        verify(updateAssessmentInsightPort).updateInsight(createCaptor.capture());
        assertEquals(loadedAssessmentInsight.getId(), createCaptor.getValue().getId());
        assertEquals(assessmentResult.getId(), createCaptor.getValue().getAssessmentResultId());
        assertEquals(newAssessmentInsight.getInsight(), createCaptor.getValue().getInsight());
        assertEquals(newAssessmentInsight.getInsightTime(), createCaptor.getValue().getInsightTime());
        assertEquals(newAssessmentInsight.getLastModificationTime(), createCaptor.getValue().getLastModificationTime());
        assertEquals(newAssessmentInsight.getInsightBy(), createCaptor.getValue().getInsightBy());
        assertEquals(newAssessmentInsight.isApproved(), createCaptor.getValue().isApproved());

        verifyNoInteractions(createAssessmentInsightPort);
    }

    private InitAssessmentInsightUseCase.Param createParam(Consumer<InitAssessmentInsightUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private InitAssessmentInsightUseCase.Param.ParamBuilder paramBuilder() {
        return InitAssessmentInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .currentUserId(UUID.randomUUID());
    }
}
