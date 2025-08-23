package org.flickit.assessment.core.application.service.insight.attribute;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.insight.AttributeInsight;
import org.flickit.assessment.core.application.port.in.insight.attribute.CreateAttributeAiInsightUseCase;
import org.flickit.assessment.core.application.port.in.insight.attribute.CreateAttributeAiInsightUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributePort;
import org.flickit.assessment.core.application.port.out.insight.attribute.CreateAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.LoadAttributeInsightPort;
import org.flickit.assessment.core.application.port.out.insight.attribute.UpdateAttributeInsightPort;
import org.flickit.assessment.core.application.service.insight.attribute.CreateAttributeAiInsightHelper.AttributeInsightParam;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ATTRIBUTE_INSIGHT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.invalidResultWithSubjectValues;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResult;
import static org.flickit.assessment.core.test.fixture.application.AttributeInsightMother.aiInsightWithTime;
import static org.flickit.assessment.core.test.fixture.application.AttributeMother.simpleAttribute;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAttributeAiInsightServiceTest {

    @InjectMocks
    private CreateAttributeAiInsightService service;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private UpdateAttributeInsightPort updateAttributeInsightPort;

    @Mock
    private LoadAttributePort loadAttributePort;

    @Mock
    private LoadAttributeInsightPort loadAttributeInsightPort;

    @Mock
    private CreateAttributeInsightPort createAttributeInsightPort;

    @Mock
    private CreateAttributeAiInsightHelper createAttributeAiInsightHelper;

    @Captor
    private ArgumentCaptor<AttributeInsight> attributeInsightArgumentCaptor;

    @Captor
    private ArgumentCaptor<AttributeInsightParam> helperParamArgumentCaptor;

    private final Attribute attribute = simpleAttribute();
    private final AssessmentResult assessmentResult = validResult();
    private final CreateAttributeAiInsightUseCase.Param param = createParam(CreateAttributeAiInsightUseCase.Param.ParamBuilder::build);

    @Test
    void testCreateAttributeAiInsight_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAiInsight(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAttributePort,
            loadAssessmentResultPort,
            loadAttributeInsightPort,
            updateAttributeInsightPort,
            createAttributeAiInsightHelper);
    }

    @Test
    void testCreateAttributeAiInsight_whenAssessmentResultIsNotFound_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenThrow(new ResourceNotFoundException(CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAiInsight(param));
        assertEquals(CREATE_ATTRIBUTE_AI_INSIGHT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort,
            updateAttributeInsightPort,
            createAttributeAiInsightHelper);
    }

    @Test
    void testCreateAttributeAiInsight_whenCalculatedResultIsNotValid_thenThrowCalculateNotValidException() {
        var invalidResult = invalidResultWithSubjectValues(null);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(invalidResult));
        doThrow(new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID)).when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var throwable = assertThrows(CalculateNotValidException.class, () -> service.createAiInsight(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        verifyNoInteractions(loadAttributeInsightPort,
            updateAttributeInsightPort,
            createAttributeAiInsightHelper);
    }

    @Test
    void testCreateAttributeAiInsight_whenInsightDoesNotExist_thenCreateAiInsightAndPersist() {
        var aiInsight = aiInsightWithTime(LocalDateTime.now());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());
        when(createAttributeAiInsightHelper.createAttributeAiInsight(helperParamArgumentCaptor.capture()))
            .thenReturn(aiInsight);

        var result = service.createAiInsight(param);

        assertEquals(assessmentResult, helperParamArgumentCaptor.getValue().assessmentResult());
        assertEquals(param.getAttributeId(), helperParamArgumentCaptor.getValue().attributeId());
        assertEquals(Locale.of(assessmentResult.getLanguage().getCode()),
            helperParamArgumentCaptor.getValue().locale());

        assertEquals(aiInsight.getAiInsight(), result.content());
        verify(createAttributeInsightPort, times(1)).persist(attributeInsightArgumentCaptor.capture());
        assertEquals(aiInsight, attributeInsightArgumentCaptor.getValue());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(updateAttributeInsightPort);
    }

    @Test
    void testCreateAttributeAiInsight_whenAiInsightExistsAndInsightTimeIsAfterCalculationTime_thenReturnExistingInsight() {
        var attributeInsight = aiInsightWithTime(LocalDateTime.now().plusDays(1));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));

        var result = service.createAiInsight(param);
        assertEquals(result.content(), attributeInsight.getAiInsight());

        ArgumentCaptor<UpdateAttributeInsightPort.AiTimeParam> attributeInsightParam = ArgumentCaptor.forClass(UpdateAttributeInsightPort.AiTimeParam.class);
        verify(updateAttributeInsightPort, times(1)).updateAiInsightTime(attributeInsightParam.capture());
        assertEquals(assessmentResult.getId(), attributeInsightParam.getValue().assessmentResultId());
        assertEquals(param.getAttributeId(), attributeInsightParam.getValue().attributeId());
        assertNotNull(attributeInsightParam.getValue().aiInsightTime());
        assertNotNull(attributeInsightParam.getValue().lastModificationTime());

        verifyNoInteractions(createAttributeAiInsightHelper);
    }

    @Test
    void testCreateAttributeAiInsight_whenAiInsightExistsAndInsightTimeIsBeforeCalculationTime_thenRecreateAiInsightAndUpdateInsight() {
        var attributeInsight = aiInsightWithTime(LocalDateTime.now().minusDays(1));
        var newAttributeAiInsight = aiInsightWithTime(LocalDateTime.now());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributePort.load(attribute.getId(), assessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.load(assessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.of(attributeInsight));
        when(createAttributeAiInsightHelper.createAttributeAiInsight(helperParamArgumentCaptor.capture()))
            .thenReturn(newAttributeAiInsight);

        var result = service.createAiInsight(param);

        assertEquals(assessmentResult, helperParamArgumentCaptor.getValue().assessmentResult());
        assertEquals(param.getAttributeId(), helperParamArgumentCaptor.getValue().attributeId());
        assertEquals(Locale.of(assessmentResult.getLanguage().getCode()),
            helperParamArgumentCaptor.getValue().locale());

        assertEquals(newAttributeAiInsight.getAiInsight(), result.content());

        ArgumentCaptor<UpdateAttributeInsightPort.AiParam> captor = ArgumentCaptor.forClass(UpdateAttributeInsightPort.AiParam.class);
        verify(updateAttributeInsightPort).updateAiInsight(captor.capture());
        assertEquals(newAttributeAiInsight.getAssessmentResultId(), captor.getValue().assessmentResultId());
        assertEquals(newAttributeAiInsight.getAttributeId(), captor.getValue().attributeId());
        assertNotNull(captor.getValue().aiInsightTime());
        assertFalse(captor.getValue().isApproved());
        assertNotNull(captor.getValue().lastModificationTime());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(createAttributeInsightPort);
    }

    @Test
    void testCreateAttributeAiInsight_WhenInsightDoesNotExistAndAssessmentIsInSecondLanguageOfKit_ThenCreateAiInsightAndPersist() {
        var faAssessmentResult = AssessmentResultMother.validResultWithLanguage(KitLanguage.EN, KitLanguage.FA);
        var aiInsight = aiInsightWithTime(LocalDateTime.now());
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ATTRIBUTE_INSIGHT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(faAssessmentResult));
        when(loadAttributePort.load(attribute.getId(), faAssessmentResult.getKitVersionId())).thenReturn(attribute);
        when(loadAttributeInsightPort.load(faAssessmentResult.getId(), param.getAttributeId())).thenReturn(Optional.empty());
        when(createAttributeAiInsightHelper.createAttributeAiInsight(helperParamArgumentCaptor.capture()))
            .thenReturn(aiInsight);

        var result = service.createAiInsight(param);

        assertEquals(faAssessmentResult, helperParamArgumentCaptor.getValue().assessmentResult());
        assertEquals(param.getAttributeId(), helperParamArgumentCaptor.getValue().attributeId());
        assertEquals(Locale.of(faAssessmentResult.getLanguage().getCode()),
            helperParamArgumentCaptor.getValue().locale());

        assertEquals(aiInsight.getAiInsight(), result.content());
        verify(createAttributeInsightPort, times(1)).persist(attributeInsightArgumentCaptor.capture());
        assertEquals(aiInsight, attributeInsightArgumentCaptor.getValue());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(updateAttributeInsightPort);
    }

    private CreateAttributeAiInsightUseCase.Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAttributeAiInsightUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAttributeAiInsightUseCase.Param.builder()
            .assessmentId(assessmentResult.getAssessment().getId())
            .attributeId(attribute.getId())
            .currentUserId(UUID.randomUUID());
    }
}
