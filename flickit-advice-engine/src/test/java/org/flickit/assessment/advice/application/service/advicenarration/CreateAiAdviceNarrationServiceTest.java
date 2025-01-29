package org.flickit.assessment.advice.application.service.advicenarration;

import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.Attribute;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.advice.application.domain.adviceitem.AdviceItem;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAiAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.CreateAiAdviceItemsPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.test.fixture.application.AdviceListItemMother;
import org.flickit.assessment.advice.test.fixture.application.AssessmentMother;
import org.flickit.assessment.advice.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.advice.test.fixture.application.AttributeLevelTargetMother;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_AI_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN;
import static org.flickit.assessment.advice.common.MessageKey.ADVICE_NARRATION_AI_IS_DISABLED;
import static org.flickit.assessment.advice.test.fixture.application.AttributeLevelTargetMother.createAttributeLevelTarget;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private LoadAttributeCurrentAndTargetLevelIndexPort loadAttributeCurrentAndTargetLevelIndexPort;

    @Mock
    private LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    private LoadAttributesPort loadAttributesPort;

    @Mock
    private OpenAiProperties openAiProperties;

    @Mock
    private CreateAiAdviceItemsPort createAiAdviceItemsPort;

    @Mock
    private LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Mock
    private CreateAdviceNarrationPort createAdviceNarrationPort;

    @Mock
    private AppAiProperties appAiProperties;

    @Captor
    private ArgumentCaptor<AdviceNarration> adviceNarrationCaptor;

    @Captor
    private ArgumentCaptor<List<AdviceItem>> adviceItemsCaptor;

    @Mock
    private CreateAdviceItemPort createAdviceItemPort;

    private final String aiNarration = "aiNarration";
    private final String prompt = "AI prompt";
    private final AssessmentResult assessmentResult = AssessmentResultMother.createAssessmentResult();
    private final CreateAiAdviceItemsPort.Result portResult = new CreateAiAdviceItemsPort.Result(aiNarration, List.of(
        new CreateAiAdviceItemsPort.Result.AdviceItem("title", "description", 0, 1, 2),
        new CreateAiAdviceItemsPort.Result.AdviceItem("title", "description", 2, 0, 1)
    ));
    private final CreateAiAdviceNarrationUseCase.Param param = createParam(CreateAiAdviceNarrationUseCase.Param.ParamBuilder::build);
    private final List<Attribute> attributes = List.of(new Attribute(param.getAttributeLevelTargets().getFirst().getAttributeId(), "Reliability"));
    private final List<MaturityLevel> maturityLevels = List.of(new MaturityLevel(param.getAttributeLevelTargets().getFirst().getMaturityLevelId(), "Great"));
    private final List<CreateAiAdviceNarrationService.AdviceRecommendation> adviceRecommendations =
        List.of(new CreateAiAdviceNarrationService.AdviceRecommendation(param.getAdviceListItems().getFirst().question().title(),
            param.getAdviceListItems().getFirst().answeredOption().title(),
            param.getAdviceListItems().getFirst().recommendedOption().title()));
    private final List<CreateAiAdviceNarrationService.TargetAttribute> targetAttributes = List.of(new CreateAiAdviceNarrationService.TargetAttribute(
        attributes.getFirst().getTitle(), maturityLevels.getFirst().getTitle()));

    @Test
    void testCreateAiAdviceNarration_WhenUserDoesNotHaveRequiredPermission_ThenShouldThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAiAdviceNarration(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(appAiProperties,
            loadAssessmentResultPort,
            validateAssessmentResultPort,
            openAiProperties,
            createAiAdviceItemsPort,
            loadAdviceNarrationPort,
            createAdviceNarrationPort,
            loadAttributeCurrentAndTargetLevelIndexPort,
            loadMaturityLevelsPort,
            loadAssessmentPort,
            loadAttributesPort,
            createAdviceItemPort);
    }

    @Test
    void testCreateAiAdviceNarration_WhenAiIsDisabled_ThenShouldReturnAiIsDisabledMessage() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(appAiProperties.isEnabled()).thenReturn(false);

        var result = service.createAiAdviceNarration(param);
        assertEquals(MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED), result.content());

        verifyNoInteractions(loadAssessmentResultPort,
            validateAssessmentResultPort,
            openAiProperties,
            createAiAdviceItemsPort,
            loadAdviceNarrationPort,
            createAdviceNarrationPort,
            loadAttributeCurrentAndTargetLevelIndexPort,
            loadAttributesPort,
            loadAssessmentPort,
            loadMaturityLevelsPort,
            createAdviceItemPort);
    }

    @Test
    void testCreateAiAdviceNarration_WhenAssessmentResultDoesNotExist_ThenShouldReturnResourceNoFound() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(appAiProperties.isEnabled()).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAiAdviceNarration(param));
        assertEquals(CREATE_AI_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            openAiProperties,
            createAiAdviceItemsPort,
            loadAdviceNarrationPort,
            loadAssessmentPort,
            createAdviceNarrationPort,
            createAdviceItemPort);
    }

    @Test
    void testCreateAiAdviceNarration_AdviceNarrationDoesNotExist_ShouldCreateAdviceNarration() {
        var assessment = AssessmentMother.assessmentWithShortTitle("ShortTitle");

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(appAiProperties.isEnabled()).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(loadAttributeCurrentAndTargetLevelIndexPort.loadAttributeCurrentAndTargetLevelIndex(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(param.getAttributeLevelTargets().getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAll(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndKitVersionId(List.of(param.getAttributeLevelTargets().getFirst().getAttributeId()), assessmentResult.getKitVersionId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(param.getAssessmentId())).thenReturn(assessment);
        when(openAiProperties.createAiAdviceNarrationAndItemsPrompt(assessment.getShortTitle(), targetAttributes.toString(), adviceRecommendations.toString())).thenReturn(prompt);
        when(createAiAdviceItemsPort.generateAiAdviceItems(prompt)).thenReturn(portResult);

        service.createAiAdviceNarration(param);

        verify(createAdviceNarrationPort).persist(adviceNarrationCaptor.capture());
        assertEquals(aiNarration, adviceNarrationCaptor.getValue().getAiNarration());
        assertEquals(param.getCurrentUserId(), adviceNarrationCaptor.getValue().getCreatedBy());
        assertNull(adviceNarrationCaptor.getValue().getAssessorNarration());
        assertNotNull(adviceNarrationCaptor.getValue().getAiNarrationTime());
        assertNull(adviceNarrationCaptor.getValue().getAssessorNarrationTime());
        assertEquals(assessmentResult.getId(), adviceNarrationCaptor.getValue().getAssessmentResultId());

        verify(createAiAdviceItemsPort).generateAiAdviceItems(prompt);

        verify(createAdviceNarrationPort).persist(adviceNarrationCaptor.capture());
        AdviceNarration capturedAdviceNarration = adviceNarrationCaptor.getValue();
        assertAll("adviceNarration",
            () -> assertEquals(aiNarration, capturedAdviceNarration.getAiNarration()),
            () -> assertEquals(param.getCurrentUserId(), capturedAdviceNarration.getCreatedBy()),
            () -> assertNull(capturedAdviceNarration.getAssessorNarration()),
            () -> assertNotNull(capturedAdviceNarration.getAiNarrationTime()),
            () -> assertNull(capturedAdviceNarration.getAssessorNarrationTime()),
            () -> assertEquals(assessmentResult.getId(), capturedAdviceNarration.getAssessmentResultId())
        );

        verify(createAdviceItemPort).persist(adviceItemsCaptor.capture());
        List<AdviceItem> capturedAdviceItems = adviceItemsCaptor.getValue();
        List<CreateAiAdviceItemsPort.Result.AdviceItem> expectedAdviceItems = portResult.adviceItems();
        assertEquals(portResult.adviceItems().size(), capturedAdviceItems.size());

        IntStream.range(0, capturedAdviceItems.size())
            .forEach(i -> assertAdviceItem(expectedAdviceItems.get(i), capturedAdviceItems.get(i)));
    }

    @Test
    void testCreateAiAdviceNarration_AdviceNarrationExistsAndShortTitleNotExists_ShouldUpdateAdviceNarration() {
        var adviceNarration = new AdviceNarration(UUID.randomUUID(), assessmentResult.getId(), aiNarration, null, LocalDateTime.now(), null, UUID.randomUUID());
        var assessment = AssessmentMother.assessmentWithShortTitle(null);

        when(appAiProperties.isEnabled()).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadAttributeCurrentAndTargetLevelIndexPort.loadAttributeCurrentAndTargetLevelIndex(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(param.getAttributeLevelTargets().getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAll(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndKitVersionId(List.of(param.getAttributeLevelTargets().getFirst().getAttributeId()), assessmentResult.getKitVersionId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(param.getAssessmentId())).thenReturn(assessment);
        when(openAiProperties.createAiAdviceNarrationAndItemsPrompt(assessment.getTitle(), targetAttributes.toString(), adviceRecommendations.toString())).thenReturn(prompt);
        when(createAiAdviceItemsPort.generateAiAdviceItems(prompt)).thenReturn(portResult);
        doNothing().when(createAdviceNarrationPort).persist(any(AdviceNarration.class));

        service.createAiAdviceNarration(param);

        verify(createAdviceNarrationPort).persist(adviceNarrationCaptor.capture());
        AdviceNarration capturedAdviceNarration = adviceNarrationCaptor.getValue();
        assertAll("adviceNarration",
            () -> assertEquals(aiNarration, capturedAdviceNarration.getAiNarration()),
            () -> assertEquals(param.getCurrentUserId(), capturedAdviceNarration.getCreatedBy()),
            () -> assertNull(capturedAdviceNarration.getAssessorNarration()),
            () -> assertNotNull(capturedAdviceNarration.getAiNarrationTime()),
            () -> assertNull(capturedAdviceNarration.getAssessorNarrationTime()),
            () -> assertEquals(assessmentResult.getId(), capturedAdviceNarration.getAssessmentResultId())
        );

        verify(createAdviceItemPort).persist(adviceItemsCaptor.capture());
        List<AdviceItem> capturedAdviceItems = adviceItemsCaptor.getValue();
        List<CreateAiAdviceItemsPort.Result.AdviceItem> expectedAdviceItems = portResult.adviceItems();
        assertEquals(portResult.adviceItems().size(), capturedAdviceItems.size());

        IntStream.range(0, capturedAdviceItems.size())
            .forEach(i -> assertAdviceItem(expectedAdviceItems.get(i), capturedAdviceItems.get(i)));
    }

    @Test
    void testCreateAiAdviceNarration_AdviceNarrationExistsAndShortTitleExists_ShouldUpdateAdviceNarration() {
        var adviceNarration = new AdviceNarration(UUID.randomUUID(), assessmentResult.getId(), aiNarration, null, LocalDateTime.now(), null, UUID.randomUUID());
        var assessment = AssessmentMother.assessmentWithShortTitle("shortTitle");

        when(appAiProperties.isEnabled()).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadAttributeCurrentAndTargetLevelIndexPort.loadAttributeCurrentAndTargetLevelIndex(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(param.getAttributeLevelTargets().getFirst().getAttributeId(), 1, 2)));
        when(loadMaturityLevelsPort.loadAll(assessmentResult.getKitVersionId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadByIdsAndKitVersionId(List.of(param.getAttributeLevelTargets().getFirst().getAttributeId()), assessmentResult.getKitVersionId())).thenReturn(attributes);
        when(loadAssessmentPort.loadById(param.getAssessmentId())).thenReturn(assessment);
        when(openAiProperties.createAiAdviceNarrationAndItemsPrompt(assessment.getShortTitle(), targetAttributes.toString(), adviceRecommendations.toString())).thenReturn(prompt);
        when(createAiAdviceItemsPort.generateAiAdviceItems(prompt)).thenReturn(portResult);
        doNothing().when(createAdviceNarrationPort).persist(any(AdviceNarration.class));

        service.createAiAdviceNarration(param);

        verify(createAdviceNarrationPort).persist(adviceNarrationCaptor.capture());
        AdviceNarration capturedAdviceNarration = adviceNarrationCaptor.getValue();
        assertAll("adviceNarration",
            () -> assertEquals(aiNarration, capturedAdviceNarration.getAiNarration()),
            () -> assertEquals(param.getCurrentUserId(), capturedAdviceNarration.getCreatedBy()),
            () -> assertNull(capturedAdviceNarration.getAssessorNarration()),
            () -> assertNotNull(capturedAdviceNarration.getAiNarrationTime()),
            () -> assertNull(capturedAdviceNarration.getAssessorNarrationTime()),
            () -> assertEquals(assessmentResult.getId(), capturedAdviceNarration.getAssessmentResultId())
        );

        verify(createAdviceItemPort).persist(adviceItemsCaptor.capture());
        List<AdviceItem> capturedAdviceItems = adviceItemsCaptor.getValue();
        List<CreateAiAdviceItemsPort.Result.AdviceItem> expectedAdviceItems = portResult.adviceItems();
        assertEquals(portResult.adviceItems().size(), capturedAdviceItems.size());

        IntStream.range(0, capturedAdviceItems.size())
            .forEach(i -> assertAdviceItem(expectedAdviceItems.get(i), capturedAdviceItems.get(i)));
    }
    @Test
    void testCreateAiAdviceNarration_NoValidTargetExists_ThrowValidationException() {
        var attributeLevelTargets = List.of(createAttributeLevelTarget());
        var adviceNarration = new AdviceNarration(UUID.randomUUID(), assessmentResult.getId(), aiNarration, null, LocalDateTime.now(), null, UUID.randomUUID());


        when(appAiProperties.isEnabled()).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(loadAttributeCurrentAndTargetLevelIndexPort.loadAttributeCurrentAndTargetLevelIndex(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(attributeLevelTargets.getFirst().getAttributeId(), 1, 1)));

        var throwable = assertThrows(ValidationException.class, () -> service.createAiAdviceNarration(param));
        assertEquals(CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN, throwable.getMessageKey());

        verifyNoInteractions(loadAttributesPort,
            loadMaturityLevelsPort,
            createAiAdviceItemsPort,
            openAiProperties,
            createAdviceNarrationPort);
    }

    private void assertAdviceItem(CreateAiAdviceItemsPort.Result.AdviceItem portItem, AdviceItem capturedItem) {
        assertAll("adviceItem",
            () -> assertEquals(portItem.title(), capturedItem.getTitle()),
            () -> assertEquals(assessmentResult.getId(), capturedItem.getAssessmentResultId()),
            () -> assertEquals(portItem.description(), capturedItem.getDescription()),
            () -> assertEquals(portItem.cost(), capturedItem.getCost().getId()),
            () -> assertEquals(portItem.impact(), capturedItem.getImpact().getId()),
            () -> assertEquals(portItem.priority(), capturedItem.getPriority().getId()),
            () -> assertNotNull(capturedItem.getCreationTime()),
            () -> assertNotNull(capturedItem.getLastModificationTime()),
            () -> assertNull(capturedItem.getCreatedBy()),
            () -> assertNull(capturedItem.getLastModifiedBy())
        );
    }

    private CreateAiAdviceNarrationUseCase.Param createParam(Consumer<CreateAiAdviceNarrationUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAiAdviceNarrationUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAiAdviceNarrationUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .adviceListItems(List.of(AdviceListItemMother.createSimpleAdviceListItem()))
            .attributeLevelTargets(List.of(AttributeLevelTargetMother.createAttributeLevelTarget()))
            .currentUserId(UUID.randomUUID());
    }
}
