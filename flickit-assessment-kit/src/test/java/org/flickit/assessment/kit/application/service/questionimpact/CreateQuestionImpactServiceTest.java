package org.flickit.assessment.kit.application.service.questionimpact;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.port.in.questionimpact.CreateQuestionImpactUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.questionimpact.CreateQuestionImpactPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_VERSION_ID_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void testCreateQuestionImpactServiceTest_kitVersionIdDoesNotExist_throwsResourceNotFoundException() {
        var param = createParam(CreateQuestionImpactUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenThrow(new ResourceNotFoundException(KIT_VERSION_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createQuestionImpact(param));

        assertEquals(KIT_VERSION_ID_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testCreateQuestionImpactServiceTest_currentUserIsNotExpertGroupOwner_throwsResourceNotFoundException() {
        var param = createParam(CreateQuestionImpactUseCase.Param.ParamBuilder::build);
        var assessmentKt = AssessmentKitMother.simpleKit();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(KitVersionMother.createKitVersion(assessmentKt));
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKt.getExpertGroupId())).thenReturn(UUID.randomUUID());

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createQuestionImpact(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateQuestionImpactServiceTest_validParams_successfulCreateQuestionImpact() {
        var param = createParam(CreateQuestionImpactUseCase.Param.ParamBuilder::build);
        var assessmentKt = AssessmentKitMother.simpleKit();

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(KitVersionMother.createKitVersion(assessmentKt));
        when(loadExpertGroupOwnerPort.loadOwnerId(assessmentKt.getExpertGroupId())).thenReturn(param.getCurrentUserId());

        ArgumentCaptor<QuestionImpact> captor = ArgumentCaptor.forClass(QuestionImpact.class);

        assertDoesNotThrow(() -> service.createQuestionImpact(param));

        verify(loadKitVersionPort).load(param.getKitVersionId());
        verify(loadExpertGroupOwnerPort).loadOwnerId(assessmentKt.getExpertGroupId());
        verify(createQuestionImpactPort).persist(captor.capture());

        assertEquals(param.getQuestionId(), captor.getValue().getQuestionId());
        assertEquals(param.getKitVersionId(), captor.getValue().getKitVersionId());
        assertEquals(param.getQuestionId(), captor.getValue().getQuestionId());
        assertEquals(param.getMaturityLevelId(), captor.getValue().getMaturityLevelId());
        assertEquals(param.getWeight(), captor.getValue().getWeight());
        assertEquals(param.getCurrentUserId(), captor.getValue().getCreatedBy());
        assertEquals(param.getCurrentUserId(), captor.getValue().getLastModifiedBy());
    }

    private CreateQuestionImpactUseCase.Param createParam(Consumer<CreateQuestionImpactUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateQuestionImpactUseCase.Param.ParamBuilder paramBuilder() {
        return CreateQuestionImpactUseCase.Param.builder()
            .kitVersionId(1L)
            .attributeId(2L)
            .maturityLevelId(3L)
            .weight(1)
            .questionId(4L)
            .currentUserId(UUID.randomUUID());
    }
}
