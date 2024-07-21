package org.flickit.assessment.core.application.service.assessmentinvitee;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentInvitee;
import org.flickit.assessment.core.application.domain.AssessmentUserRole;
import org.flickit.assessment.core.application.port.in.assessmentinvitee.GetAssessmentInviteeListUseCase;
import org.flickit.assessment.core.application.port.out.assessmentinvitee.LoadAssessmentInviteeListPort;
import org.flickit.assessment.data.jpa.core.assessmentinvitee.AssessmentInviteeJpaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_ASSESSMENT_INVITEE_LIST;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAssessmentInviteeListServiceTest {

    @InjectMocks
    private GetAssessmentInviteeListService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadAssessmentInviteeListPort loadAssessmentInviteeListPort;

    @Test
    void testGetAssessmentInviteeList_WhenDoesNotHaveRequiredPermission_ThenThrowException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAssessmentInviteeListUseCase.Param(assessmentId, currentUserId, 10, 0);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_INVITEE_LIST)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getInvitees(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetAssessmentInviteeList_WhenValidInput_ThenValidResult() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var param = new GetAssessmentInviteeListUseCase.Param(assessmentId, currentUserId, 10, 0);

        var assessmentInvitee = new AssessmentInvitee(UUID.randomUUID(),
            "flickit@mail.com",
            AssessmentUserRole.VIEWER,
            LocalDateTime.now(),
            LocalDateTime.now(),
            currentUserId);

        var expectedPageResult = new PaginatedResponse<>(
            List.of(assessmentInvitee),
            0,
            10,
            Sort.Direction.DESC.name().toLowerCase(),
            AssessmentInviteeJpaEntity.Fields.creationTime,
            1);

        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, VIEW_ASSESSMENT_INVITEE_LIST)).thenReturn(true);
        when(loadAssessmentInviteeListPort.loadByAssessmentId(assessmentId, param.getSize(), param.getPage()))
            .thenReturn(expectedPageResult);

        var response = service.getInvitees(param);

        assertEquals(expectedPageResult.getPage(), response.getPage());
        assertEquals(expectedPageResult.getSize(), response.getSize());
        assertEquals(expectedPageResult.getSort(), response.getSort());
        assertEquals(expectedPageResult.getOrder(), response.getOrder());
        assertEquals(expectedPageResult.getTotal(), response.getTotal());
        assertEquals(1, response.getItems().size());

        var item = response.getItems().get(0);
        assertEquals(assessmentInvitee.getId(), item.id());
        assertEquals(assessmentInvitee.getEmail(), item.email());
        assertEquals(assessmentInvitee.getRole().getId(), item.role().id());
        assertEquals(assessmentInvitee.getRole().getTitle(), item.role().title());
        assertEquals(assessmentInvitee.getExpirationTime(), item.expirationTime());
        assertEquals(assessmentInvitee.getCreationTime(), item.creationTime());
    }
}
