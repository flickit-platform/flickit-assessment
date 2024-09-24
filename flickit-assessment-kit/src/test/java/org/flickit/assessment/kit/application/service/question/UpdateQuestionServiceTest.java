package org.flickit.assessment.kit.application.service.question;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.in.question.UpdateQuestionUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupOwnerPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.flickit.assessment.kit.test.fixture.application.AssessmentKitMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateQuestionServiceTest {

    @InjectMocks
    private UpdateQuestionService service;

    @Mock
    private UpdateQuestionPort updateQuestionPort;

    @Mock
    private LoadAssessmentKitPort loadAssessmentKitPort;

    @Mock
    private LoadExpertGroupOwnerPort loadExpertGroupOwnerPort;

    @Test
    void testUpdateQuestion_WhenCurrentUserIsNotOwner_ThenThrowAccessDeniedException() {
        AssessmentKit kit = AssessmentKitMother.simpleKit();
        int index = 2;
        String title = "team";
        String hint = "hint";
        boolean mayNotBeApplicable = true;
        boolean advisable = true;
        UUID currentUserId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UpdateQuestionUseCase.Param param = new UpdateQuestionUseCase.Param(kit.getId(),
            kit.getKitVersionId(),
            index,
            title,
            hint,
            mayNotBeApplicable,
            advisable,
            currentUserId);

        when(loadAssessmentKitPort.load(kit.getId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(ownerId);

        AccessDeniedException throwable = assertThrows(AccessDeniedException.class, () -> service.updateQuestion(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testUpdateQuestion_WhenCurrentUserIsOwner_ThenUpdateQuestion() {
        AssessmentKit kit = AssessmentKitMother.simpleKit();
        int index = 2;
        String title = "team";
        String hint = "hint";
        boolean mayNotBeApplicable = true;
        boolean advisable = true;
        UUID currentUserId = UUID.randomUUID();
        UpdateQuestionUseCase.Param param = new UpdateQuestionUseCase.Param(kit.getId(),
            kit.getKitVersionId(),
            index,
            title,
            hint,
            mayNotBeApplicable,
            advisable,
            currentUserId);

        when(loadAssessmentKitPort.load(kit.getId())).thenReturn(kit);
        when(loadExpertGroupOwnerPort.loadOwnerId(kit.getExpertGroupId())).thenReturn(currentUserId);

        service.updateQuestion(param);

        verify(updateQuestionPort).update(any(UpdateQuestionPort.Param.class));
    }
}
