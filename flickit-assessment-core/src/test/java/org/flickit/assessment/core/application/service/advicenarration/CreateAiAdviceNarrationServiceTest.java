package org.flickit.assessment.core.application.service.advicenarration;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.advice.AttributeLevelTarget;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.in.advicenarration.CreateAiAdviceNarrationUseCase;
import org.flickit.assessment.core.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.core.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.service.advicenarration.CreateAiAdviceNarrationService.AdviceDto;
import org.flickit.assessment.core.test.fixture.application.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_AI_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN;
import static org.flickit.assessment.core.common.MessageKey.ADVICE_NARRATION_AI_IS_DISABLED;
import static org.flickit.assessment.core.test.fixture.application.AdviceNarrationMother.aiNarration;
import static org.flickit.assessment.core.test.fixture.application.AttributeLevelTargetMother.createAttributeLevelTarget;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAiAdviceNarrationServiceTest {

    @InjectMocks
    private CreateAiAdviceNarrationService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAssessmentPort loadAssessmentPort;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private LoadAttributeValuePort loadAttributeValuePort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    @Mock
    private CallAiPromptPort callAiPromptPort;

    @Mock
    private LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Mock
    private CreateAdviceNarrationPort createAdviceNarrationPort;

    @Mock
    private UpdateAdviceNarrationPort updateAdviceNarrationPort;

    @Mock
    private CreateAdviceItemPort createAdviceItemPort;

    @Captor
    private ArgumentCaptor<CreateAdviceNarrationPort.Param> createAdviceNarrationParamCaptor;

    @Captor
    private ArgumentCaptor<UpdateAdviceNarrationPort.AiNarrationParam> updateNarrationCaptor;

    @Captor
    private ArgumentCaptor<List<CreateAdviceItemPort.Param>> adviceItemsCaptor;

    @Captor
    private ArgumentCaptor<Prompt> promptArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class<AdviceDto>> classCaptor;

    @Spy
    private AppAiProperties appAiProperties = appAiProperties();

    private final Assessment assessment = AssessmentMother.assessment();
    private final String aiNarration = "aiNarration";
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();
    private final List<CreateAiAdviceNarrationService.AdviceDto.AdviceItemDto> adviceItems = List.of(
        new CreateAiAdviceNarrationService.AdviceDto.AdviceItemDto("title1", "description1", 0, 1, 2),
        new CreateAiAdviceNarrationService.AdviceDto.AdviceItemDto("title1", "description1", 2, 0, 1)
    );
    private final AdviceDto aiAdvice = new AdviceDto(aiNarration, adviceItems);
    private final List<Attribute> attributes = List.of(AttributeMother.simpleAttribute());
    private final List<MaturityLevel> maturityLevels = List.of(MaturityLevelMother.levelThree());
    private final List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(attributes.getFirst().getId(), maturityLevels.getFirst().getId()));
    private final CreateAiAdviceNarrationUseCase.Param param = createParam(b -> b.assessmentId(assessment.getId()).attributeLevelTargets(attributeLevelTargets));

    @Test
    void testCreateAiAdviceNarration_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAiAdviceNarration(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(appAiProperties,
            loadAssessmentResultPort,
            validateAssessmentResultPort,
            callAiPromptPort,
            loadAdviceNarrationPort,
            createAdviceNarrationPort,
            loadAttributeValuePort,
            loadMaturityLevelsPort,
            loadAssessmentPort,
            loadAttributesPort,
            createAdviceItemPort);
    }

    @Test
    void testCreateAiAdviceNarration_whenAiIsDisabled_thenReturnAiIsDisabledMessage() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(appAiProperties.isEnabled()).thenReturn(false);

        var result = service.createAiAdviceNarration(param);
        assertEquals(MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED), result.content());

        verifyNoInteractions(loadAssessmentResultPort,
            validateAssessmentResultPort,
            callAiPromptPort,
            loadAdviceNarrationPort,
            createAdviceNarrationPort,
            loadAttributeValuePort,
            loadAttributesPort,
            loadAssessmentPort,
            loadMaturityLevelsPort,
            createAdviceItemPort);
    }

    @Test
    void testCreateAiAdviceNarration_whenAssessmentResultDoesNotExist_thenReturnResourceNoFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(appAiProperties.isEnabled()).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAiAdviceNarration(param));
        assertEquals(CREATE_AI_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            callAiPromptPort,
            loadAdviceNarrationPort,
            loadAssessmentPort,
            loadMaturityLevelsPort,
            createAdviceNarrationPort,
            createAdviceItemPort);
    }

    @Test
    void testCreateAiAdviceNarration_whenAdviceNarrationDoesNotExist_thenCreateAdviceNarration() {
        var expectedPrompt = new PromptTemplate(appAiProperties.getPrompt().getAdviceNarrationAndAdviceItems(),
            Map.of("attributeTargets", "TargetAttribute[attribute=" + attributes.getFirst().getTitle() + ", targetMaturityLevel=" + maturityLevels.getFirst().getTitle() + "]",
                "adviceRecommendations", "AdviceRecommendation[question=title, currentOption=answeredOption, recommendedOption=recommendedOption]",
                "language", assessmentResult.getLanguage().getTitle())).create();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(loadAttributeValuePort.loadCurrentAndTargetLevelIndices(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeValuePort.AttributeLevelIndex(param.getAttributeLevelTargets().getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAllByAssessment(param.getAssessmentId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndAssessmentId(List.of(param.getAttributeLevelTargets().getFirst().getAttributeId()), param.getAssessmentId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiAdvice);

        service.createAiAdviceNarration(param);

        verify(createAdviceNarrationPort).persist(createAdviceNarrationParamCaptor.capture(), eq(assessmentResult.getId()));
        verify(createAdviceItemPort).persistAll(adviceItemsCaptor.capture(), eq(assessmentResult.getId()));
        verify(validateAssessmentResultPort).validate(param.getAssessmentId());

        var capturedAdviceNarration = createAdviceNarrationParamCaptor.getValue();
        assertEquals(aiNarration, createAdviceNarrationParamCaptor.getValue().aiNarration());
        assertEquals(expectedPrompt.getContents(), promptArgumentCaptor.getValue().getContents());
        assertEquals(AdviceDto.class, classCaptor.getValue());
        assertNull(capturedAdviceNarration.createdBy());
        assertNull(capturedAdviceNarration.assessorNarration());
        assertNotNull(capturedAdviceNarration.aiNarrationTime());
        assertNull(capturedAdviceNarration.assessorNarrationTime());

        var capturedAdviceItems = adviceItemsCaptor.getValue();
        var expectedAdviceItems = aiAdvice.adviceItems();
        assertAdviceItems(expectedAdviceItems, capturedAdviceItems);

        verifyNoInteractions(updateAdviceNarrationPort);
    }

    @Test
    void testCreateAiAdviceNarration_whenAdviceNarrationExistsAndShortTitleNotExists_thenUpdateAdviceNarration() {
        var adviceNarration = aiNarration();
        var expectedPrompt = new PromptTemplate(appAiProperties.getPrompt().getAdviceNarrationAndAdviceItems(),
            Map.of("attributeTargets", "TargetAttribute[attribute=" + attributes.getFirst().getTitle() + ", targetMaturityLevel=" + maturityLevels.getFirst().getTitle() + "]",
                "adviceRecommendations", "AdviceRecommendation[question=title, currentOption=answeredOption, recommendedOption=recommendedOption]",
                "language", assessmentResult.getLanguage().getTitle())).create();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadAttributeValuePort.loadCurrentAndTargetLevelIndices(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeValuePort.AttributeLevelIndex(param.getAttributeLevelTargets().getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAllByAssessment(param.getAssessmentId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndAssessmentId(List.of(param.getAttributeLevelTargets().getFirst().getAttributeId()), param.getAssessmentId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiAdvice);

        service.createAiAdviceNarration(param);

        verify(updateAdviceNarrationPort).updateAiNarration(updateNarrationCaptor.capture());
        verify(createAdviceItemPort).persistAll(adviceItemsCaptor.capture(), eq(assessmentResult.getId()));
        verify(validateAssessmentResultPort).validate(param.getAssessmentId());

        var capturedAdviceNarration = updateNarrationCaptor.getValue();
        assertEquals(adviceNarration.getId(), capturedAdviceNarration.id());
        assertEquals(aiNarration, capturedAdviceNarration.narration());
        assertEquals(expectedPrompt.getContents(), promptArgumentCaptor.getValue().getContents());
        assertNotNull(capturedAdviceNarration.narrationTime());
        assertFalse(capturedAdviceNarration.approved());

        var capturedAdviceItems = adviceItemsCaptor.getValue();
        var expectedAdviceItems = aiAdvice.adviceItems();
        assertAdviceItems(expectedAdviceItems, capturedAdviceItems);

        verifyNoInteractions(createAdviceNarrationPort);
    }

    @Test
    void testCreateAiAdviceNarration_whenAdviceNarrationExistsAndShortTitleExists_thenUpdateAdviceNarration() {
        var adviceNarration = aiNarration();
        var expectedPrompt = new PromptTemplate(appAiProperties.getPrompt().getAdviceNarrationAndAdviceItems(),
            Map.of("attributeTargets", "TargetAttribute[attribute=" + attributes.getFirst().getTitle() + ", targetMaturityLevel=" + maturityLevels.getFirst().getTitle() + "]",
                "adviceRecommendations", "AdviceRecommendation[question=title, currentOption=answeredOption, recommendedOption=recommendedOption]",
                "language", assessmentResult.getLanguage().getTitle())).create();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadAttributeValuePort.loadCurrentAndTargetLevelIndices(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeValuePort.AttributeLevelIndex(param.getAttributeLevelTargets().getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAllByAssessment(param.getAssessmentId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndAssessmentId(List.of(attributes.getFirst().getId()), param.getAssessmentId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(param.getAssessmentId())).thenReturn(Optional.of(assessment));
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiAdvice);

        service.createAiAdviceNarration(param);

        verify(createAdviceItemPort).persistAll(adviceItemsCaptor.capture(), eq(assessmentResult.getId()));
        verify(updateAdviceNarrationPort).updateAiNarration(updateNarrationCaptor.capture());
        verify(validateAssessmentResultPort).validate(param.getAssessmentId());

        var capturedAdviceNarration = updateNarrationCaptor.getValue();
        assertEquals(adviceNarration.getId(), capturedAdviceNarration.id());
        assertEquals(aiNarration, capturedAdviceNarration.narration());
        assertEquals(expectedPrompt.getContents(), promptArgumentCaptor.getValue().getContents());
        assertNotNull(capturedAdviceNarration.narrationTime());
        assertFalse(capturedAdviceNarration.approved());

        var capturedAdviceItems = adviceItemsCaptor.getValue();
        var expectedAdviceItems = aiAdvice.adviceItems();
        assertAdviceItems(expectedAdviceItems, capturedAdviceItems);

        verifyNoInteractions(createAdviceNarrationPort);
    }

    @Test
    void testCreateAiAdviceNarration_whenNoValidTargetExists_thenThrowValidationException() {
        var attributeLevelTargets = List.of(createAttributeLevelTarget());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAttributeValuePort.loadCurrentAndTargetLevelIndices(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeValuePort.AttributeLevelIndex(attributeLevelTargets.getFirst().getAttributeId(), 1, 1)));

        var throwable = assertThrows(ValidationException.class, () -> service.createAiAdviceNarration(param));
        assertEquals(CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN, throwable.getMessageKey());

        verify(validateAssessmentResultPort).validate(param.getAssessmentId());
        verifyNoInteractions(loadAttributesPort,
            loadMaturityLevelsPort,
            callAiPromptPort,
            createAdviceNarrationPort);
    }

    private void assertAdviceItems(List<AdviceDto.AdviceItemDto> expectedAdviceItems, List<CreateAdviceItemPort.Param> capturedAdviceItems) {
        assertEquals(expectedAdviceItems.size(), capturedAdviceItems.size());
        assertEquals(AdviceDto.class, classCaptor.getValue());
        assertThat(capturedAdviceItems)
            .zipSatisfy(expectedAdviceItems, (actual, expected) -> {
                assertEquals(expected.title(), actual.title());
                assertEquals(expected.description(), actual.description());
                assertEquals(expected.cost(), actual.cost().getId());
                assertEquals(expected.impact(), actual.impact().getId());
                assertEquals(expected.priority(), actual.priority().getId());
                assertNotNull(actual.creationTime());
                assertNull(actual.createdBy());
            });
    }

    private AppAiProperties appAiProperties() {
        var properties = new AppAiProperties();
        properties.setEnabled(true);
        properties.setPrompt(new AppAiProperties.Prompt());
        properties.setSaveAiInputFileEnabled(true);
        properties.getPrompt().setAdviceNarrationAndAdviceItems("The assessment with attribute targets {attributeTargets} " +
            "and recommendations {adviceRecommendations} has been evaluated. Provide the result in the {language} language");
        return properties;
    }

    private CreateAiAdviceNarrationUseCase.Param createParam(Consumer<CreateAiAdviceNarrationUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAiAdviceNarrationUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAiAdviceNarrationUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .adviceListItems(List.of(AdvicePlanItemMother.createSimpleAdvicePlanItem()))
            .attributeLevelTargets(List.of(AttributeLevelTargetMother.createAttributeLevelTarget()))
            .currentUserId(UUID.randomUUID());
    }
}
