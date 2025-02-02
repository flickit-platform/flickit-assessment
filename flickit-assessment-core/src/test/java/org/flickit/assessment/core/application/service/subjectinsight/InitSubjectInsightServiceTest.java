package org.flickit.assessment.core.application.service.subjectinsight;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.subjectinsight.InitSubjectInsightUseCase;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.CreateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.LoadSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectinsight.UpdateSubjectInsightPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.LoadSubjectValuesPort;
import org.flickit.assessment.core.test.fixture.application.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_REPORT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.MessageKey.SUBJECT_DEFAULT_INSIGHT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private LoadSubjectValuesPort loadSubjectValuesPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadSubjectInsightPort loadSubjectInsightPort;

    @Captor
    private ArgumentCaptor<SubjectInsight> subjectInsightArgumentCaptor;

    private final MaturityLevel mLevel = MaturityLevelMother.levelOne();
    private final List<MaturityLevel> maturityLevels = List.of(MaturityLevelMother.levelOne(), MaturityLevelMother.levelTwo());
    private final List<AttributeValue> attributeValues = List.of(AttributeValueMother.hasFullScoreOnLevel23WithWeight(1, 123L),
        AttributeValueMother.hasFullScoreOnLevel23WithWeight(2, 124L));
    private final Subject subject = SubjectMother.subjectWithWeightAndAttributes(1, List.of(AttributeMother.simpleAttribute()));
    private final SubjectValue subjectValue = SubjectValueMother.withAttributeValues(attributeValues, mLevel, 3);
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();
    private final String insight = buildInsight();

    @Test
    void testInitSubjectInsight_whenCurrentUserDoesNotHaveRequiredPermission_throwAccessDeniedException() {
        var param = createParam(InitSubjectInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(false);

        var throwable = Assertions.assertThrows(AccessDeniedException.class, () -> service.initSubjectInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            createSubjectInsightPort,
            updateSubjectInsightPort,
            validateAssessmentResultPort,
            loadSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenAssessmentResultOfRequestedAssessmentNotExist_thenThrowResourceNotFoundException() {
        var param = createParam(InitSubjectInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.initSubjectInsight(param));
        assertEquals(INIT_SUBJECT_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadSubjectValuesPort,
            loadMaturityLevelsPort,
            createSubjectInsightPort,
            updateSubjectInsightPort,
            validateAssessmentResultPort,
            loadSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenSubjectInsightExists_thenUpdateSubjectInsight() {
        var param = createParam(InitSubjectInsightUseCase.Param.ParamBuilder::build);
        var subjectInsight = SubjectInsightMother.subjectInsight();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId()))
            .thenReturn(Optional.of(subjectInsight));
        when(loadSubjectValuesPort.load(param.getSubjectId(), assessmentResult.getId()))
            .thenReturn(subjectValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);
        doNothing().when(updateSubjectInsightPort).update(any(SubjectInsight.class));

        service.initSubjectInsight(param);

        verify(updateSubjectInsightPort).update(subjectInsightArgumentCaptor.capture());
        SubjectInsight capturedSubjectInsight = subjectInsightArgumentCaptor.getValue();
        assertNotNull(capturedSubjectInsight);
        assertEquals(assessmentResult.getId(), capturedSubjectInsight.getAssessmentResultId());
        assertEquals(param.getSubjectId(), capturedSubjectInsight.getSubjectId());
        assertEquals(insight, capturedSubjectInsight.getInsight());
        assertNull(capturedSubjectInsight.getInsightBy());
        assertNotNull(capturedSubjectInsight.getInsightTime());
        assertFalse(capturedSubjectInsight.isApproved());

        verifyNoInteractions(createSubjectInsightPort);
    }

    @Test
    void testInitSubjectInsight_whenRequestedToInitiateSubjectInsightOfExistingAssessmentResult_thenCreateDefaultSubjectInsight() {
        var param = createParam(InitSubjectInsightUseCase.Param.ParamBuilder::build);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), VIEW_ASSESSMENT_REPORT))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadSubjectInsightPort.load(assessmentResult.getId(), param.getSubjectId())).thenReturn(Optional.empty());
        when(loadSubjectValuesPort.load(param.getSubjectId(), assessmentResult.getId()))
            .thenReturn(subjectValue);
        when(loadMaturityLevelsPort.loadByKitVersionId(assessmentResult.getKitVersionId()))
            .thenReturn(maturityLevels);
        doNothing().when(createSubjectInsightPort).persist(any(SubjectInsight.class));

        service.initSubjectInsight(param);

        verify(createSubjectInsightPort).persist(subjectInsightArgumentCaptor.capture());
        SubjectInsight subjectInsight = subjectInsightArgumentCaptor.getValue();
        assertNotNull(subjectInsight);
        assertEquals(assessmentResult.getId(), subjectInsight.getAssessmentResultId());
        assertEquals(param.getSubjectId(), subjectInsight.getSubjectId());
        assertEquals(insight, subjectInsight.getInsight());
        assertNull(subjectInsight.getInsightBy());
        assertNotNull(subjectInsight.getInsightTime());
        assertFalse(subjectInsight.isApproved());

        verifyNoInteractions(updateSubjectInsightPort);
    }

    private @NotNull String buildInsight() {
        return MessageBundle.message(SUBJECT_DEFAULT_INSIGHT,
            subjectValue.getSubject().getTitle(),
            subjectValue.getSubject().getDescription(),
            subjectValue.getConfidenceValue() != null ? (int) Math.ceil(subjectValue.getConfidenceValue()) : 0,
            subject.getTitle(),
            subjectValue.getMaturityLevel().getIndex(),
            maturityLevels.size(),
            subjectValue.getMaturityLevel().getTitle(),
            subjectValue.getAttributeValues().size(),
            subject.getTitle());
    }

    private InitSubjectInsightUseCase.Param createParam(Consumer<InitSubjectInsightUseCase.Param.ParamBuilder> changer) {
        var param = paramBuilder();
        changer.accept(param);
        return param.build();
    }

    private InitSubjectInsightUseCase.Param.ParamBuilder paramBuilder() {
        return InitSubjectInsightUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .subjectId(1L)
            .currentUserId(UUID.randomUUID());
    }
}
