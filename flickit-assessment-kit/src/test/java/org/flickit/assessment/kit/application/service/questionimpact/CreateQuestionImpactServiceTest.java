package org.flickit.assessment.kit.application.service.questionimpact;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.port.in.questionimpact.CreateQuestionImpactUseCase;
import org.flickit.assessment.kit.application.port.out.answeroption.LoadAnswerOptionsByQuestionPort;
import org.flickit.assessment.kit.application.port.out.answeroptionimpact.CreateAnswerOptionImpactPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateQuestionImpactServiceTest {

    @InjectMocks
    private CreateQuestionImpactService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private CreateQuestionImpactPort createQuestionImpactPort;

    @Mock
    private LoadAnswerOptionsByQuestionPort loadAnswerOptionsByQuestionPort;

    @Mock
    private CreateAnswerOptionImpactPort createAnswerOptionImpactPort;

    @Captor
    private ArgumentCaptor<List<CreateAnswerOptionImpactPort.Param>> createAnswerOptionImpactPortCaptor;

    private final UUID ownerId = UUID.randomUUID();
    private final KitVersion kitVersion = createKitVersion(simpleKit());

    @Test
    void testCreateQuestionImpact_currentUserIsNotExpertGroupOwner_throwsResourceNotFoundException() {
        var param = createParam(CreateQuestionImpactUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createQuestionImpact(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAnswerOptionsByQuestionPort, createAnswerOptionImpactPort, createQuestionImpactPort);
    }

    @Test
    void testCreateQuestionImpact_validParams_successfulCreateQuestionImpact() {
        var param = createParam(b -> b.currentUserId(ownerId));
        AnswerOption option1 = AnswerOptionMother.createAnswerOption(1, "op1", 1);
        AnswerOption option2 = AnswerOptionMother.createAnswerOption(1, "op2", 2);
        var options = List.of(option1, option2);
        long questionImpactId = 1L;

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(createQuestionImpactPort.persist(any(QuestionImpact.class))).thenReturn(questionImpactId);
        when(loadAnswerOptionsByQuestionPort.loadByQuestionId(param.getQuestionId(), param.getKitVersionId())).thenReturn(options);
        doNothing().when(createAnswerOptionImpactPort).persistAll(anyList());

        long result = service.createQuestionImpact(param);
        assertEquals(questionImpactId, result);

        ArgumentCaptor<QuestionImpact> questionImpactCaptor = ArgumentCaptor.forClass(QuestionImpact.class);
        verify(createQuestionImpactPort).persist(questionImpactCaptor.capture());
        assertEquals(param.getKitVersionId(), questionImpactCaptor.getValue().getKitVersionId());
        assertEquals(param.getAttributeId(), questionImpactCaptor.getValue().getAttributeId());
        assertEquals(param.getMaturityLevelId(), questionImpactCaptor.getValue().getMaturityLevelId());
        assertEquals(param.getQuestionId(), questionImpactCaptor.getValue().getQuestionId());
        assertEquals(param.getWeight(), questionImpactCaptor.getValue().getWeight());
        assertEquals(param.getCurrentUserId(), questionImpactCaptor.getValue().getCreatedBy());
        assertEquals(param.getCurrentUserId(), questionImpactCaptor.getValue().getLastModifiedBy());
        assertNotNull(questionImpactCaptor.getValue().getCreationTime());
        assertNotNull(questionImpactCaptor.getValue().getLastModificationTime());

        verify(createAnswerOptionImpactPort).persistAll(createAnswerOptionImpactPortCaptor.capture());
        List<CreateAnswerOptionImpactPort.Param> portParams = createAnswerOptionImpactPortCaptor.getValue();
        for (int i = 0; i < portParams.size(); i++) {
            assertEquals(questionImpactId, portParams.get(i).questionImpactId());
            assertEquals(options.get(i).getId(), portParams.get(i).optionId());
            assertNull(portParams.get(i).value());
            assertEquals(param.getKitVersionId(), portParams.get(i).kitVersionId());
            assertEquals(param.getCurrentUserId(), portParams.get(i).createdBy());
        }
    }

    private CreateQuestionImpactUseCase.Param createParam(Consumer<CreateQuestionImpactUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateQuestionImpactUseCase.Param.ParamBuilder paramBuilder() {
        return CreateQuestionImpactUseCase.Param.builder()
            .kitVersionId(kitVersion.getId())
            .attributeId(2L)
            .maturityLevelId(3L)
            .questionId(4L)
            .weight(1)
            .currentUserId(UUID.randomUUID());
    }
}
