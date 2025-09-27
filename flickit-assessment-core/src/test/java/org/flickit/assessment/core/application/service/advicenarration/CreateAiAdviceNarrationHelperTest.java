package org.flickit.assessment.core.application.service.advicenarration;

import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.advice.AdvicePlanItem;
import org.flickit.assessment.common.application.domain.advice.AttributeLevelTarget;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.core.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;
import org.flickit.assessment.core.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.core.application.service.advicenarration.CreateAiAdviceNarrationHelper.AdviceDto;
import org.flickit.assessment.core.test.fixture.application.AdvicePlanItemMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.AttributeMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.core.common.MessageKey.ADVICE_NARRATION_AI_IS_DISABLED;
import static org.flickit.assessment.core.test.fixture.application.AdviceNarrationMother.aiNarration;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAiAdviceNarrationHelperTest {

    @InjectMocks
    private CreateAiAdviceNarrationHelper helper;

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
    private ArgumentCaptor<CreateAdviceNarrationPort.Param> createAdviceNarrationCaptor;

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

    private final List<AdvicePlanItem> advicePlanItems = List.of(AdvicePlanItemMother.createSimpleAdvicePlanItem());

    private final String aiNarration = "aiNarration";
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();
    private final UUID assessmentId = assessmentResult.getAssessment().getId();
    private final List<AdviceDto.AdviceItemDto> adviceItems = List.of(
        new AdviceDto.AdviceItemDto("title1", "description1", 0, 1, 2),
        new AdviceDto.AdviceItemDto("title1", "description1", 2, 0, 1)
    );
    private final AdviceDto aiAdvice = new AdviceDto(aiNarration, adviceItems);
    private final List<Attribute> attributes = List.of(AttributeMother.simpleAttribute());
    private final List<MaturityLevel> maturityLevels = List.of(MaturityLevelMother.levelThree());
    private final List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(attributes.getFirst().getId(), maturityLevels.getFirst().getId()));

    @Test
    void testCreateAiAdviceNarration_whenAiIsDisabled_thenReturnAiIsDisabledMessage() {
        when(appAiProperties.isEnabled()).thenReturn(false);

        var result = helper.createAiAdviceNarration(assessmentResult, advicePlanItems, attributeLevelTargets);
        assertEquals(MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED), result);

        verifyNoInteractions(
            callAiPromptPort,
            loadAdviceNarrationPort,
            createAdviceNarrationPort,
            loadAttributesPort,
            loadMaturityLevelsPort,
            createAdviceItemPort);
    }

    @Test
    void testCreateAiAdviceNarration_whenAdviceNarrationDoesNotExist_thenCreateAdviceNarration() {
        var expectedPrompt = new PromptTemplate(appAiProperties.getPrompt().getAdviceNarrationAndAdviceItems(),
            Map.of("attributeTargets", "TargetAttribute[attribute=" + attributes.getFirst().getTitle() + ", targetMaturityLevel=" + maturityLevels.getFirst().getTitle() + "]",
                "adviceRecommendations", "AdviceRecommendation[question=title, currentOption=answeredOption, recommendedOption=recommendedOption]",
                "language", assessmentResult.getLanguage().getTitle())).create();

        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(loadMaturityLevelsPort.loadAllByAssessment(assessmentId)).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndAssessmentId(List.of(attributeLevelTargets.getFirst().getAttributeId()), assessmentId)).thenReturn(attributes);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiAdvice);

        helper.createAiAdviceNarration(assessmentResult, advicePlanItems, attributeLevelTargets);

        verify(createAdviceNarrationPort).persist(createAdviceNarrationCaptor.capture(), eq(assessmentResult.getId()));
        verify(createAdviceItemPort).persistAll(adviceItemsCaptor.capture(), eq(assessmentResult.getId()));

        var capturedAdviceNarration = createAdviceNarrationCaptor.getValue();
        assertEquals(aiNarration, createAdviceNarrationCaptor.getValue().aiNarration());
        assertEquals(expectedPrompt.getContents(), promptArgumentCaptor.getValue().getContents());
        assertEquals(AdviceDto.class, classCaptor.getValue());
        assertNull(capturedAdviceNarration.createdBy());
        assertNull(capturedAdviceNarration.assessorNarration());
        assertNotNull(capturedAdviceNarration.aiNarrationTime());
        assertNull(capturedAdviceNarration.assessorNarrationTime());
        assertFalse(capturedAdviceNarration.approved());

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

        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadMaturityLevelsPort.loadAllByAssessment(assessmentId)).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndAssessmentId(List.of(attributes.getFirst().getId()), assessmentId)).thenReturn(attributes);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiAdvice);

        helper.createAiAdviceNarration(assessmentResult, advicePlanItems, attributeLevelTargets);

        verify(updateAdviceNarrationPort).updateAiNarration(updateNarrationCaptor.capture());
        verify(createAdviceItemPort).persistAll(adviceItemsCaptor.capture(), eq(assessmentResult.getId()));

        var capturedAdviceNarration = updateNarrationCaptor.getValue();
        assertEquals(adviceNarration.getId(), capturedAdviceNarration.id());
        assertEquals(aiNarration, capturedAdviceNarration.narration());
        assertEquals(expectedPrompt.getContents(), promptArgumentCaptor.getValue().getContents());
        assertNotNull(capturedAdviceNarration.narrationTime());

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

        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadMaturityLevelsPort.loadAllByAssessment(assessmentId)).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndAssessmentId(List.of(attributeLevelTargets.getFirst().getAttributeId()), assessmentId)).thenReturn(attributes);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiAdvice);

        helper.createAiAdviceNarration(assessmentResult, advicePlanItems, attributeLevelTargets);

        verify(createAdviceItemPort).persistAll(adviceItemsCaptor.capture(), eq(assessmentResult.getId()));
        verify(updateAdviceNarrationPort).updateAiNarration(updateNarrationCaptor.capture());

        var capturedAdviceNarration = updateNarrationCaptor.getValue();
        assertEquals(adviceNarration.getId(), capturedAdviceNarration.id());
        assertEquals(aiNarration, capturedAdviceNarration.narration());
        assertEquals(expectedPrompt.getContents(), promptArgumentCaptor.getValue().getContents());
        assertNotNull(capturedAdviceNarration.narrationTime());

        var capturedAdviceItems = adviceItemsCaptor.getValue();
        var expectedAdviceItems = aiAdvice.adviceItems();
        assertAdviceItems(expectedAdviceItems, capturedAdviceItems);

        verifyNoInteractions(createAdviceNarrationPort);
    }

    private void assertAdviceItems(List<AdviceDto.AdviceItemDto> expectedAdviceItems, List<CreateAdviceItemPort.Param> capturedAdviceItems) {
        assertEquals(expectedAdviceItems.size(), capturedAdviceItems.size());
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
        properties.getPrompt().setAdviceNarrationAndAdviceItems("The assessment with attribute targets {attributeTargets}" +
            "and recommendations {adviceRecommendations} has been evaluated. Provide the result in the {language} language");
        return properties;
    }
}
