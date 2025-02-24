package org.flickit.assessment.core.application.service.insight.subject;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.SubjectInsight;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.in.insight.subject.InitSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.insight.subject.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.insight.subject.UpdateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.test.fixture.application.SubjectInsightMother;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_SUBJECT_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResultWithKitLanguage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitSubjectInsightServiceTest {

    @InjectMocks
    private InitSubjectInsightService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private CreateSubjectInsightPort createSubjectInsightPort;

    @Mock
    private UpdateSubjectInsightPort updateSubjectInsightPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadSubjectPort loadSubjectPort;

    @Mock
    private CreateSubjectInsightsHelper createSubjectInsightsHelper;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadSubjectInsightPort loadSubjectInsightPort;

    @Captor
    private ArgumentCaptor<CreateSubjectInsightsHelper.Param> initInsightParamArgumentCaptor;

    @Captor
    private ArgumentCaptor<SubjectInsight> subjectInsightArgumentCaptor;

    private final AssessmentResult assessmentResult = validResult();
    private final SubjectValue subjectValue = SubjectValueMother.createSubjectValue();
    private final InitSubjectInsightUseCase.Param param = createParam(InitSubjectInsightUseCase.Param.ParamBuilder::build);

    @Test
    void testInitSubjectInsight_whenCurrentUserDoesNotHaveRequiredPermission_throwAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(false);

        var throwable = Assertions.assertThrows(AccessDeniedException.class, () -> service.initSubjectInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            createSubjectInsightPort,
            updateSubjectInsightPort,
            validateAssessmentResultPort,
            loadSubjectInsightPort,
            loadSubjectPort,
            createSubjectInsightsHelper);
    }

    @Test
    void testInitSubjectInsight_whenAssessmentResultOfRequestedAssessmentNotExist_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.initSubjectInsight(param));
        assertEquals(INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadSubjectPort,
            createSubjectInsightsHelper,
            createSubjectInsightPort,
            updateSubjectInsightPort,
            validateAssessmentResultPort,
            loadSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenSubjectInsightExists_thenUpdateSubjectInsight() {
        var subjectInsight = SubjectInsightMother.subjectInsight();
        var newSubjectInsight = SubjectInsightMother.defaultSubjectInsight();
        var locale = Locale.of(assessmentResult.getAssessment().getAssessmentKit().getLanguage().getCode());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId()))
            .thenReturn(Optional.of(subjectInsight));
        when(loadSubjectPort.loadByIdAndKitVersionId(param.getSubjectId(), assessmentResult.getKitVersionId()))
            .thenReturn(Optional.of(subjectValue.getSubject()));
        when(createSubjectInsightsHelper.initSubjectInsights(initInsightParamArgumentCaptor.capture()))
            .thenReturn(List.of(newSubjectInsight));

        service.initSubjectInsight(param);

        assertEquals(assessmentResult, initInsightParamArgumentCaptor.getValue().assessmentResult());
        assertEquals(List.of(param.getSubjectId()), initInsightParamArgumentCaptor.getValue().subjectIds());
        assertEquals(locale, initInsightParamArgumentCaptor.getValue().locale());

        verify(updateSubjectInsightPort).update(subjectInsightArgumentCaptor.capture());
        assertEquals(newSubjectInsight, subjectInsightArgumentCaptor.getValue());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(createSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenSubjectInsightDoesNotExist_thenCreateDefaultSubjectInsight() {
        var assessmentResultWithPersianKit = validResultWithKitLanguage(KitLanguage.FA);
        var paramForPersianKit = createParam(b -> b.assessmentId(assessmentResultWithPersianKit.getAssessment().getId()));
        var newSubjectInsight = SubjectInsightMother.defaultSubjectInsight();
        var locale = Locale.of(assessmentResultWithPersianKit.getAssessment().getAssessmentKit().getLanguage().getCode());
        when(assessmentAccessChecker.isAuthorized(paramForPersianKit.getAssessmentId(), paramForPersianKit.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(paramForPersianKit.getAssessmentId())).thenReturn(Optional.of(assessmentResultWithPersianKit));
        when(loadSubjectInsightPort.load(assessmentResultWithPersianKit.getId(), paramForPersianKit.getSubjectId()))
            .thenReturn(Optional.empty());
        when(loadSubjectPort.loadByIdAndKitVersionId(paramForPersianKit.getSubjectId(), assessmentResultWithPersianKit.getKitVersionId()))
            .thenReturn(Optional.of(subjectValue.getSubject()));
        when(createSubjectInsightsHelper.initSubjectInsights(initInsightParamArgumentCaptor.capture()))
            .thenReturn(List.of(newSubjectInsight));

        service.initSubjectInsight(paramForPersianKit);

        assertEquals(assessmentResultWithPersianKit, initInsightParamArgumentCaptor.getValue().assessmentResult());
        assertEquals(List.of(paramForPersianKit.getSubjectId()), initInsightParamArgumentCaptor.getValue().subjectIds());
        assertEquals(locale, initInsightParamArgumentCaptor.getValue().locale());

        verify(createSubjectInsightPort).persist(subjectInsightArgumentCaptor.capture());
        assertEquals(newSubjectInsight, subjectInsightArgumentCaptor.getValue());

        verify(validateAssessmentResultPort).validate(paramForPersianKit.getAssessmentId());
        verifyNoInteractions(updateSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenSubjectIdIsNotExist_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadSubjectPort.loadByIdAndKitVersionId(param.getSubjectId(), assessmentResult.getKitVersionId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.initSubjectInsight(param));
        assertEquals(INIT_SUBJECT_INSIGHT_SUBJECT_NOT_FOUND, throwable.getMessage());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(updateSubjectInsightPort,
            createSubjectInsightPort,
            createSubjectInsightsHelper,
            loadSubjectInsightPort);
    }

    private InitSubjectInsightUseCase.Param createParam(Consumer<InitSubjectInsightUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private InitSubjectInsightUseCase.Param.ParamBuilder paramBuilder() {
        return InitSubjectInsightUseCase.Param.builder()
            .assessmentId(assessmentResult.getAssessment().getId())
            .subjectId(subjectValue.getSubject().getId())
            .currentUserId(UUID.randomUUID());
    }
}
