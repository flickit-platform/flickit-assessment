package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.in.assessment.UpdateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.UPDATE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_ASSESSMENT_KIT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAssessmentServiceTest {

    @InjectMocks
    private UpdateAssessmentService service;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private UpdateAssessmentResultPort updateAssessmentResultPort;

    private final UUID id = UUID.randomUUID();
    private final UUID currentUserId = UUID.randomUUID();
    private UpdateAssessmentUseCase.Param param = createParam(UpdateAssessmentUseCase.Param.ParamBuilder::build);
    private final AssessmentResult assessmentResult = AssessmentResultMother.validResult();
    private final AssessmentKit assessmentKit = AssessmentKitMother.publicKit();

    @Test
    void testUpdateAssessment_whenUserHasNoAccessToAssessment_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), UPDATE_ASSESSMENT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.updateAssessment(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testUpdateAssessment_whenAssessmentResultDoesNotExist_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), UPDATE_ASSESSMENT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateAssessment(param));
        assertEquals(UPDATE_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testUpdateAssessment_whenAssessmentKitDoesNotExist_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), UPDATE_ASSESSMENT)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentKitPort.loadAssessmentKit(assessmentResult.getAssessment().getAssessmentKit().getId(), null)).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.updateAssessment(param));
        assertEquals(UPDATE_ASSESSMENT_ASSESSMENT_KIT_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testUpdateAssessment_whenParametersAreValid_thenUpdateAndReturnId() {
        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), UPDATE_ASSESSMENT)).thenReturn(true);
        when(updateAssessmentPort.update(any())).thenReturn(new UpdateAssessmentPort.Result(id));
        when(loadAssessmentResultPort.loadByAssessmentId(param.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentKitPort.loadAssessmentKit(assessmentResult.getAssessment().getAssessmentKit().getId(), null))
            .thenReturn(Optional.of(assessmentKit));

        UUID resultId = service.updateAssessment(param).id();
        assertEquals(id, resultId);

        ArgumentCaptor<UpdateAssessmentPort.AllParam> updatePortParam = ArgumentCaptor.forClass(UpdateAssessmentPort.AllParam.class);
        verify(updateAssessmentPort).update(updatePortParam.capture());
        verify(updateAssessmentResultPort).updateLanguage(eq(assessmentResult.getId()), eq(KitLanguage.FA));

        assertEquals(param.getId(), updatePortParam.getValue().id());
        assertEquals(param.getTitle(), updatePortParam.getValue().title());
        assertEquals(param.getCurrentUserId(), updatePortParam.getValue().lastModifiedBy());
        assertNotNull(updatePortParam.getValue().title());
        assertNotNull(updatePortParam.getValue().lastModificationTime());
    }

    @Test
    void testUpdateAssessment_whenParametersAreValidAndLangNotProvided_thenUpdateAndReturnId() {
        param = createParam(b -> b.lang(null));

        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), UPDATE_ASSESSMENT)).thenReturn(true);
        when(updateAssessmentPort.update(any())).thenReturn(new UpdateAssessmentPort.Result(id));
        when(loadAssessmentResultPort.loadByAssessmentId(param.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentKitPort.loadAssessmentKit(assessmentResult.getAssessment().getAssessmentKit().getId(), null))
            .thenReturn(Optional.of(assessmentKit));

        UUID resultId = service.updateAssessment(param).id();
        assertEquals(id, resultId);

        ArgumentCaptor<UpdateAssessmentPort.AllParam> updatePortParam = ArgumentCaptor.forClass(UpdateAssessmentPort.AllParam.class);
        verify(updateAssessmentPort).update(updatePortParam.capture());

        assertEquals(param.getId(), updatePortParam.getValue().id());
        assertEquals(param.getTitle(), updatePortParam.getValue().title());
        assertEquals(param.getCurrentUserId(), updatePortParam.getValue().lastModifiedBy());
        assertNotNull(updatePortParam.getValue().title());
        assertNotNull(updatePortParam.getValue().lastModificationTime());

        verifyNoInteractions(updateAssessmentResultPort);
    }

    @Test
    void testUpdateAssessment_whenParametersAreValidAndNewLangEqualsToOldLang_thenUpdateWithoutLangAndReturnId() {
        param = createParam(b -> b.lang("EN"));

        when(assessmentAccessChecker.isAuthorized(param.getId(), param.getCurrentUserId(), UPDATE_ASSESSMENT)).thenReturn(true);
        when(updateAssessmentPort.update(any())).thenReturn(new UpdateAssessmentPort.Result(id));
        when(loadAssessmentResultPort.loadByAssessmentId(param.getId())).thenReturn(Optional.of(assessmentResult));
        when(loadAssessmentKitPort.loadAssessmentKit(assessmentResult.getAssessment().getAssessmentKit().getId(), null))
            .thenReturn(Optional.of(assessmentKit));

        UUID resultId = service.updateAssessment(param).id();
        assertEquals(id, resultId);

        ArgumentCaptor<UpdateAssessmentPort.AllParam> updatePortParam = ArgumentCaptor.forClass(UpdateAssessmentPort.AllParam.class);
        verify(updateAssessmentPort).update(updatePortParam.capture());

        assertEquals(param.getId(), updatePortParam.getValue().id());
        assertEquals(param.getTitle(), updatePortParam.getValue().title());
        assertEquals(param.getCurrentUserId(), updatePortParam.getValue().lastModifiedBy());
        assertNotNull(updatePortParam.getValue().title());
        assertNotNull(updatePortParam.getValue().lastModificationTime());

        verifyNoInteractions(updateAssessmentResultPort);
    }

    private UpdateAssessmentUseCase.Param createParam(Consumer<UpdateAssessmentUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private UpdateAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAssessmentUseCase.Param.builder()
            .id(UUID.randomUUID())
            .title("title")
            .shortTitle("shortTitle")
            .lang("FA")
            .currentUserId(currentUserId);
    }
}
