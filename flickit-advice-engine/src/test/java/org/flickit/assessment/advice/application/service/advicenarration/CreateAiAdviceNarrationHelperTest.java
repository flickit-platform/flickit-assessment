package org.flickit.assessment.advice.application.service.advicenarration;

import org.flickit.assessment.advice.application.domain.*;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.application.service.advicenarration.CreateAiAdviceNarrationHelper.AdviceDto;
import org.flickit.assessment.advice.test.fixture.application.AdviceListItemMother;
import org.flickit.assessment.advice.test.fixture.application.AttributeLevelTargetMother;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN;
import static org.flickit.assessment.advice.common.MessageKey.ADVICE_NARRATION_AI_IS_DISABLED;
import static org.flickit.assessment.advice.test.fixture.application.AssessmentMother.assessmentWithShortTitle;
import static org.flickit.assessment.advice.test.fixture.application.AssessmentResultMother.createAssessmentResult;
import static org.flickit.assessment.advice.test.fixture.application.AttributeLevelTargetMother.createAttributeLevelTarget;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAiAdviceNarrationHelperTest {

    @InjectMocks
    private CreateAiAdviceNarrationHelper helper;

    @Mock
    private LoadAssessmentPort loadAssessmentPort;

    @Mock
    private LoadAttributeCurrentAndTargetLevelIndexPort loadAttributeCurrentAndTargetLevelIndexPort;

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
    private ArgumentCaptor<AdviceNarration> adviceNarrationCaptor;

    @Captor
    private ArgumentCaptor<UpdateAdviceNarrationPort.AiNarrationParam> updateNarrationCaptor;

    @Captor
    private ArgumentCaptor<List<AdviceItem>> adviceItemsCaptor;

    @Captor
    private ArgumentCaptor<Prompt> promptArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class<AdviceDto>> classCaptor;

    @Spy
    private AppAiProperties appAiProperties = appAiProperties();

    private final List<AdviceListItem> adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
    private final List<AttributeLevelTarget> attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());

    private final String aiNarration = "aiNarration";
    private final AssessmentResult assessmentResult = createAssessmentResult();
    private final List<AdviceDto.AdviceItemDto> adviceItems = List.of(
        new AdviceDto.AdviceItemDto("title1", "description1", 0, 1, 2),
        new AdviceDto.AdviceItemDto("title1", "description1", 2, 0, 1)
    );
    private final AdviceDto aiAdvice = new AdviceDto(aiNarration, adviceItems);
    private final List<Attribute> attributes = List.of(new Attribute(attributeLevelTargets.getFirst().getAttributeId(), "Reliability"));
    private final List<MaturityLevel> maturityLevels = List.of(new MaturityLevel(attributeLevelTargets.getFirst().getMaturityLevelId(), "Great", 0));

    @Test
    void testCreateAiAdviceNarration_whenAiIsDisabled_thenReturnAiIsDisabledMessage() {
        when(appAiProperties.isEnabled()).thenReturn(false);

        var result = helper.createAiAdviceNarration(assessmentResult, adviceListItems, attributeLevelTargets);
        assertEquals(MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED), result);

        verifyNoInteractions(
            callAiPromptPort,
            loadAdviceNarrationPort,
            createAdviceNarrationPort,
            loadAttributeCurrentAndTargetLevelIndexPort,
            loadAttributesPort,
            loadAssessmentPort,
            loadMaturityLevelsPort,
            createAdviceItemPort);
    }

    @Test
    void testCreateAiAdviceNarration_whenNoValidTargetExists_thenThrowValidationException() {
        var attributeLevelTargets = List.of(createAttributeLevelTarget());
        var adviceNarration = new AdviceNarration(UUID.randomUUID(), assessmentResult.getId(), aiNarration, null, LocalDateTime.now(), null, UUID.randomUUID());

        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadAttributeCurrentAndTargetLevelIndexPort.load(assessmentResult.getAssessmentId(), attributeLevelTargets))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(attributeLevelTargets.getFirst().getAttributeId(), 1, 1)));

        var throwable = assertThrows(ValidationException.class,
            () -> helper.createAiAdviceNarration(assessmentResult, adviceListItems, attributeLevelTargets));
        assertEquals(CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN, throwable.getMessageKey());

        verifyNoInteractions(loadAttributesPort,
            loadMaturityLevelsPort,
            callAiPromptPort,
            createAdviceNarrationPort);
    }

    @Test
    void testCreateAiAdviceNarration_whenAdviceNarrationDoesNotExist_thenCreateAdviceNarration() {
        var assessment = assessmentWithShortTitle("ShortTitle");

        var expectedPrompt = new PromptTemplate(appAiProperties.getPrompt().getAdviceNarrationAndAdviceItems(),
            Map.of("assessmentTitle", assessment.getShortTitle(),
                "attributeTargets", "TargetAttribute[attribute=Reliability, targetMaturityLevel=Great]",
                "adviceRecommendations", "AdviceRecommendation[question=title, currentOption=answeredOption, recommendedOption=recommendedOption]",
                "language", assessmentResult.getLanguage().getTitle())).create();

        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(loadAttributeCurrentAndTargetLevelIndexPort.load(assessmentResult.getAssessmentId(), attributeLevelTargets))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(attributeLevelTargets.getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAll(assessmentResult.getAssessmentId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndAssessmentId(List.of(attributeLevelTargets.getFirst().getAttributeId()), assessmentResult.getAssessmentId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(assessmentResult.getAssessmentId())).thenReturn(assessment);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiAdvice);

        helper.createAiAdviceNarration(assessmentResult, adviceListItems, attributeLevelTargets);

        verify(createAdviceNarrationPort).persist(adviceNarrationCaptor.capture());
        verify(createAdviceItemPort).persistAll(adviceItemsCaptor.capture());

        var capturedAdviceNarration = adviceNarrationCaptor.getValue();
        assertEquals(aiNarration, adviceNarrationCaptor.getValue().getAiNarration());
        assertEquals(expectedPrompt.getContents(), promptArgumentCaptor.getValue().getContents());
        assertEquals(AdviceDto.class, classCaptor.getValue());
        assertNull(capturedAdviceNarration.getCreatedBy());
        assertNull(capturedAdviceNarration.getAssessorNarration());
        assertNotNull(capturedAdviceNarration.getAiNarrationTime());
        assertNull(capturedAdviceNarration.getAssessorNarrationTime());
        assertEquals(assessmentResult.getId(), capturedAdviceNarration.getAssessmentResultId());

        var capturedAdviceItems = adviceItemsCaptor.getValue();
        var expectedAdviceItems = aiAdvice.adviceItems();
        assertAdviceItems(expectedAdviceItems, capturedAdviceItems);

        verifyNoInteractions(updateAdviceNarrationPort);
    }

    @Test
    void testCreateAiAdviceNarration_whenAdviceNarrationExistsAndShortTitleNotExists_thenUpdateAdviceNarration() {
        var assessment = assessmentWithShortTitle(null);

        var adviceNarration = new AdviceNarration(UUID.randomUUID(), assessmentResult.getId(), aiNarration, null, LocalDateTime.now(), null, UUID.randomUUID());
        var expectedPrompt = new PromptTemplate(appAiProperties.getPrompt().getAdviceNarrationAndAdviceItems(),
            Map.of("assessmentTitle", assessment.getTitle(),
                "attributeTargets", "TargetAttribute[attribute=Reliability, targetMaturityLevel=Great]",
                "adviceRecommendations", "AdviceRecommendation[question=title, currentOption=answeredOption, recommendedOption=recommendedOption]",
                "language", assessmentResult.getLanguage().getTitle())).create();

        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadAttributeCurrentAndTargetLevelIndexPort.load(assessmentResult.getAssessmentId(), attributeLevelTargets))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(attributeLevelTargets.getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAll(assessmentResult.getAssessmentId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndAssessmentId(List.of(attributeLevelTargets.getFirst().getAttributeId()), assessmentResult.getAssessmentId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(assessmentResult.getAssessmentId())).thenReturn(assessment);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiAdvice);

        helper.createAiAdviceNarration(assessmentResult, adviceListItems, attributeLevelTargets);

        verify(updateAdviceNarrationPort).updateAiNarration(updateNarrationCaptor.capture());
        verify(createAdviceItemPort).persistAll(adviceItemsCaptor.capture());

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
        var assessment = assessmentWithShortTitle("ShortTitle");

        var adviceNarration = new AdviceNarration(UUID.randomUUID(), assessmentResult.getId(), aiNarration, null, LocalDateTime.now(), null, UUID.randomUUID());
        var expectedPrompt = new PromptTemplate(appAiProperties.getPrompt().getAdviceNarrationAndAdviceItems(),
            Map.of("assessmentTitle", assessment.getShortTitle(),
                "attributeTargets", "TargetAttribute[attribute=Reliability, targetMaturityLevel=Great]",
                "adviceRecommendations", "AdviceRecommendation[question=title, currentOption=answeredOption, recommendedOption=recommendedOption]",
                "language", assessmentResult.getLanguage().getTitle())).create();

        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadAttributeCurrentAndTargetLevelIndexPort.load(assessmentResult.getAssessmentId(), attributeLevelTargets))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(attributeLevelTargets.getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAll(assessmentResult.getAssessmentId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndAssessmentId(List.of(attributeLevelTargets.getFirst().getAttributeId()), assessmentResult.getAssessmentId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(assessmentResult.getAssessmentId())).thenReturn(assessment);
        when(callAiPromptPort.call(promptArgumentCaptor.capture(), classCaptor.capture())).thenReturn(aiAdvice);

        helper.createAiAdviceNarration(assessmentResult, adviceListItems, attributeLevelTargets);

        verify(createAdviceItemPort).persistAll(adviceItemsCaptor.capture());
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


    private void assertAdviceItems(List<AdviceDto.AdviceItemDto> expectedAdviceItems, List<AdviceItem> capturedAdviceItems) {
        assertEquals(expectedAdviceItems.size(), capturedAdviceItems.size());
        assertEquals(AdviceDto.class, classCaptor.getValue());
        assertThat(capturedAdviceItems)
            .zipSatisfy(expectedAdviceItems, (actual, expected) -> {
                assertEquals(expected.title(), actual.getTitle());
                assertEquals(expected.description(), actual.getDescription());
                assertEquals(assessmentResult.getId(), actual.getAssessmentResultId());
                assertEquals(expected.cost(), actual.getCost().getId());
                assertEquals(expected.impact(), actual.getImpact().getId());
                assertEquals(expected.priority(), actual.getPriority().getId());
                assertNotNull(actual.getCreationTime());
                assertNotNull(actual.getLastModificationTime());
                assertNull(actual.getCreatedBy());
                assertNull(actual.getLastModifiedBy());
            });
    }

    private AppAiProperties appAiProperties() {
        var properties = new AppAiProperties();
        properties.setEnabled(true);
        properties.setPrompt(new AppAiProperties.Prompt());
        properties.setSaveAiInputFileEnabled(true);
        properties.getPrompt().setAdviceNarrationAndAdviceItems("The assessment \"{assessmentTitle}\" " +
            "with attribute targets {attributeTargets} and recommendations {adviceRecommendations} has been evaluated. " +
            "Provide the result in the {language} language");
        return properties;
    }
}
