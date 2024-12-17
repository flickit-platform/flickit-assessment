package org.flickit.assessment.kit.application.service.kitversion;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;
import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase;
import org.flickit.assessment.kit.application.port.in.kitversion.ActivateKitVersionUseCase.Param;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitActiveVersionPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.UpdateKitLastMajorModificationTimePort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.flickit.assessment.kit.application.port.out.kitversion.UpdateKitVersionStatusPort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.LoadSubjectQuestionnairePort;
import org.flickit.assessment.kit.test.fixture.application.AnswerOptionMother;
import org.flickit.assessment.kit.test.fixture.application.KitVersionMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionImpactMother;
import org.flickit.assessment.kit.test.fixture.application.QuestionMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.ACTIVATE_KIT_VERSION_INVALID;
import static org.flickit.assessment.kit.common.ErrorMessageKey.ACTIVATE_KIT_VERSION_STATUS_INVALID;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.kitWithKitVersionId;
import static org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother.simpleKit;
import static org.flickit.assessment.kit.test.fixture.application.KitVersionMother.createKitVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivateKitVersionServiceTest {

    @InjectMocks
    private ActivateKitVersionService service;

    @Mock
    private LoadKitVersionPort loadKitVersionPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Mock
    private UpdateKitVersionStatusPort updateKitVersionStatusPort;

    @Mock
    private LoadSubjectQuestionnairePort loadSubjectQuestionnairePort;

    @Mock
    private UpdateKitActiveVersionPort updateKitActiveVersionPort;

    @Mock
    private CreateSubjectQuestionnairePort createSubjectQuestionnairePort;

    @Mock
    private UpdateKitLastMajorModificationTimePort updateKitLastMajorModificationTimePort;

    @Mock
    private KitVersionValidator kitVersionValidator;

    @Captor
    private ArgumentCaptor<Map<Long, Set<Long>>> qnnIdToSubjIdsCaptor;

    private final UUID ownerId = UUID.randomUUID();
    private KitVersion kitVersion = createKitVersion(simpleKit());
    List<SubjectQuestionnaire> subjectQuestionnaireList = List.of(
        new SubjectQuestionnaire(null, 11L, 123L),
        new SubjectQuestionnaire(null, 21L, 123L),
        new SubjectQuestionnaire(null, 31L, 456L)
    );

    @Test
    void testActivateKitVersion_kitVersionIsNotInUpdatingStatus_ThrowsValidationException() {
        kitVersion = KitVersionMother.createActiveKitVersion(simpleKit());
        var param = createParam(ActivateKitVersionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);

        var exception = assertThrows(ValidationException.class, () -> service.activateKitVersion(param));
        assertEquals(ACTIVATE_KIT_VERSION_STATUS_INVALID, exception.getMessageKey());

        verifyNoInteractions(loadExpertGroupOwnerPort,
            updateKitVersionStatusPort,
            updateKitActiveVersionPort,
            updateKitLastMajorModificationTimePort,
            createSubjectQuestionnairePort,
            kitVersionValidator);
    }

    @Test
    void testActivateKitVersion_userHasNotAccess_ThrowsAccessDeniedException() {
        var param = createParam(ActivateKitVersionUseCase.Param.ParamBuilder::build);

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);

        var exception = assertThrows(AccessDeniedException.class, () -> service.activateKitVersion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, exception.getMessage());

        verifyNoInteractions(updateKitVersionStatusPort,
            updateKitActiveVersionPort,
            updateKitLastMajorModificationTimePort,
            createSubjectQuestionnairePort,
            kitVersionValidator);
    }

    @Test
    void testActivateKitVersion_kitVersionIsNotValid_ThrowsValidationException() {
        var param = createParam(b -> b.currentUserId(ownerId));

        when(loadKitVersionPort.load(param.getKitVersionId())).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        when(kitVersionValidator.validate(param.getKitVersionId())).thenReturn(List.of("Invalid question"));

        var exception = assertThrows(ValidationException.class, () -> service.activateKitVersion(param));
        assertEquals(ACTIVATE_KIT_VERSION_INVALID, exception.getMessageKey());

        verifyNoInteractions(updateKitVersionStatusPort,
            updateKitActiveVersionPort,
            updateKitLastMajorModificationTimePort,
            createSubjectQuestionnairePort);
    }

    @Test
    void testActivateKitVersion_ActiveVersionExists_ArchiveOldVersion() {
        Param param = createParam(b -> b.currentUserId(ownerId));

        var option1 = AnswerOptionMother.createAnswerOption(1L, "op1", 1);
        var option2 = AnswerOptionMother.createAnswerOption(2L, "op2", 2);

        var question1 = QuestionMother.createQuestion(option1.getAnswerRangeId());
        var question2 = QuestionMother.createQuestion(option2.getAnswerRangeId());

        var qImpact1 = QuestionImpactMother.createQuestionImpact(1L, 1L, 1, question1.getId());
        var qImpact2 = QuestionImpactMother.createQuestionImpact(1L, 2L, 1, question2.getId());

        question1.setImpacts(List.of(qImpact1));
        question2.setImpacts(List.of(qImpact2));

        Long kitVersionId = param.getKitVersionId();
        when(loadKitVersionPort.load(kitVersionId)).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kitVersion.getKit().getExpertGroupId())).thenReturn(ownerId);
        doNothing().when(updateKitVersionStatusPort).updateStatus(kitVersion.getKit().getActiveVersionId(), KitVersionStatus.ARCHIVE);
        when(kitVersionValidator.validate(param.getKitVersionId())).thenReturn(List.of());
        doNothing().when(updateKitVersionStatusPort).updateStatus(kitVersionId, KitVersionStatus.ACTIVE);
        doNothing().when(updateKitActiveVersionPort).updateActiveVersion(kitVersion.getKit().getId(), kitVersionId);
        doNothing().when(updateKitLastMajorModificationTimePort).updateLastMajorModificationTime(eq(kitVersion.getKit().getId()), notNull(LocalDateTime.class));
        when(loadSubjectQuestionnairePort.extractPairs(kitVersionId)).thenReturn(subjectQuestionnaireList);

        service.activateKitVersion(param);

        verify(createSubjectQuestionnairePort).persistAll(qnnIdToSubjIdsCaptor.capture(), eq(kitVersionId));
        assertEquals(2, qnnIdToSubjIdsCaptor.getValue().size());
        assertNotNull(qnnIdToSubjIdsCaptor.getValue().get(123L));
        assertEquals(2, qnnIdToSubjIdsCaptor.getValue().get(123L).size());
        assertEquals(Set.of(11L, 21L), qnnIdToSubjIdsCaptor.getValue().get(123L));
        assertNotNull(qnnIdToSubjIdsCaptor.getValue().get(456L));
        assertEquals(1, qnnIdToSubjIdsCaptor.getValue().get(456L).size());
        assertEquals(Set.of(31L), qnnIdToSubjIdsCaptor.getValue().get(456L));
    }

    @Test
    void testActivateKitVersion_ThereIsNoActiveVersion_ActivateNewKitVersion() {
        var kit = kitWithKitVersionId(null);
        kitVersion = createKitVersion(kit);
        var param = createParam(b -> b.currentUserId(ownerId));

        var option1 = AnswerOptionMother.createAnswerOption(1L, "op1", 1);
        var option2 = AnswerOptionMother.createAnswerOption(2L, "op2", 2);

        var question1 = QuestionMother.createQuestion(option1.getAnswerRangeId());
        var question2 = QuestionMother.createQuestion(option2.getAnswerRangeId());

        var qImpact1 = QuestionImpactMother.createQuestionImpact(1L, 1L, 1, question1.getId());
        var qImpact2 = QuestionImpactMother.createQuestionImpact(1L, 2L, 1, question2.getId());

        question1.setImpacts(List.of(qImpact1));
        question2.setImpacts(List.of(qImpact2));


        Long kitVersionId = param.getKitVersionId();
        when(loadKitVersionPort.load(kitVersionId)).thenReturn(kitVersion);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);
        when(kitVersionValidator.validate(param.getKitVersionId())).thenReturn(List.of());
        doNothing().when(updateKitVersionStatusPort).updateStatus(kitVersionId, KitVersionStatus.ACTIVE);
        doNothing().when(updateKitActiveVersionPort).updateActiveVersion(kit.getId(), kitVersionId);
        doNothing().when(updateKitLastMajorModificationTimePort).updateLastMajorModificationTime(eq(kitVersion.getKit().getId()), notNull(LocalDateTime.class));
        when(loadSubjectQuestionnairePort.extractPairs(kitVersionId)).thenReturn(subjectQuestionnaireList);

        service.activateKitVersion(param);

        verify(createSubjectQuestionnairePort).persistAll(qnnIdToSubjIdsCaptor.capture(), eq(kitVersionId));
        assertEquals(2, qnnIdToSubjIdsCaptor.getValue().size());
        assertNotNull(qnnIdToSubjIdsCaptor.getValue().get(123L));
        assertEquals(2, qnnIdToSubjIdsCaptor.getValue().get(123L).size());
        assertEquals(Set.of(11L, 21L), qnnIdToSubjIdsCaptor.getValue().get(123L));
        assertNotNull(qnnIdToSubjIdsCaptor.getValue().get(456L));
        assertEquals(1, qnnIdToSubjIdsCaptor.getValue().get(456L).size());
        assertEquals(Set.of(31L), qnnIdToSubjIdsCaptor.getValue().get(456L));
    }

    private ActivateKitVersionUseCase.Param createParam(Consumer<Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private Param.ParamBuilder paramBuilder() {
        return Param.builder()
            .kitVersionId(kitVersion.getId())
            .currentUserId(UUID.randomUUID());
    }
}
