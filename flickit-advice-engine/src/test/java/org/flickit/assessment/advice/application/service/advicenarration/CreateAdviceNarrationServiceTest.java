package org.flickit.assessment.advice.application.service.advicenarration;

import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAdviceNarrationUseCase;
import org.flickit.assessment.advice.test.fixture.application.AdviceListItemMother;
import org.flickit.assessment.advice.test.fixture.application.AttributeLevelTargetMother;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAdviceNarrationServiceTest {

    @InjectMocks
    CreateAdviceNarrationService createAdviceNarrationService;
    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Test
    void testCreateAdviceNarration_UserHasNotAccess_ShouldReturnAccessDenied() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var param = new CreateAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.CREATE_ADVICE)).thenReturn(false);
        var throwable = assertThrows(AccessDeniedException.class, () -> createAdviceNarrationService.createAdviceNarration(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }
}
