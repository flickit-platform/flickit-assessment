package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentListItem;
import org.flickit.assessment.core.application.port.in.assessment.GetSpaceAssessmentListUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentListPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentKitMother;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSpaceAssessmentListServiceTest {

    @InjectMocks
    private GetSpaceAssessmentListService service;

    @Mock
    private LoadAssessmentListPort loadAssessmentPort;

    @Mock
    private CheckSpaceAccessPort checkSpaceAccessPort;

    @Mock
    private AssessmentPermissionChecker assessmentPermissionChecker;

    @Test
    void testGetSpaceAssessmentList_NoResultsFound_NoItemReturned() {
        Long spaceId = 2L;

        PaginatedResponse<AssessmentListItem> paginatedResponse = new PaginatedResponse<>(
            new ArrayList<>(),
            0,
            0,
            "lastModificationTime",
            "DESC",
            0);

        UUID currentUserId = UUID.randomUUID();
        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadAssessmentPort.loadSpaceAssessments(spaceId, currentUserId, 0, 10)).thenReturn(paginatedResponse);

        var param = new GetSpaceAssessmentListUseCase.Param(spaceId, currentUserId, 10, 0);
        PaginatedResponse<GetSpaceAssessmentListUseCase.SpaceAssessmentListItem> result = service.getAssessmentList(param);
        assertEquals(0, result.getItems().size());
    }

    @Test
    void testGetSpaceAssessmentList_ResultsFoundForSpaceId_ItemsReturned() {
        Long spaceId = 123L;
        var assessment1 = AssessmentMother.assessmentListItem(spaceId, AssessmentKitMother.kit().getId());
        var assessment2 = AssessmentMother.assessmentListItem(spaceId, AssessmentKitMother.kit().getId());

        var paginatedRes = new PaginatedResponse<>(
            List.of(assessment1, assessment2),
            0,
            20,
            AssessmentJpaEntity.Fields.lastModificationTime,
            Sort.Direction.DESC.name().toLowerCase(),
            2
        );

        UUID currentUserId = UUID.randomUUID();

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadAssessmentPort.loadSpaceAssessments(spaceId, currentUserId, 0, 20))
            .thenReturn(paginatedRes);
        when(assessmentPermissionChecker.isAuthorized(any(UUID.class), any(UUID.class), any(AssessmentPermission.class)))
            .thenReturn(true);

        var param = new GetSpaceAssessmentListUseCase.Param(spaceId, currentUserId, 20, 0);
        var assessments = service.getAssessmentList(param);

        ArgumentCaptor<Long> spaceIdArgument = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Integer> sizeArgument = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<UUID> currentUserIdArgument = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> pageArgument = ArgumentCaptor.forClass(Integer.class);
        verify(loadAssessmentPort).loadSpaceAssessments(
            spaceIdArgument.capture(),
            currentUserIdArgument.capture(),
            pageArgument.capture(),
            sizeArgument.capture());

        assertEquals(spaceId, spaceIdArgument.getValue());
        assertEquals(currentUserId, currentUserIdArgument.getValue());
        assertEquals(20, sizeArgument.getValue());
        assertEquals(0, pageArgument.getValue());

        assertEquals(2, assessments.getItems().size());
        assertEquals(20, assessments.getSize());
        assertEquals(0, assessments.getPage());
        assertEquals(2, assessments.getTotal());
        assertFalse(assessments.getItems().getFirst().permissions().canManageSettings());
        assertTrue(assessments.getItems().getFirst().permissions().canViewDashboard());
        assertTrue(assessments.getItems().getFirst().permissions().canViewReport());
        assertTrue(assessments.getItems().getFirst().permissions().canViewQuestionnaires());
        assertFalse(assessments.getItems().getLast().permissions().canManageSettings());
        assertTrue(assessments.getItems().getLast().permissions().canViewDashboard());
        assertTrue(assessments.getItems().getLast().permissions().canViewReport());
        assertTrue(assessments.getItems().getLast().permissions().canViewQuestionnaires());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), assessments.getOrder());
        assertEquals(AssessmentJpaEntity.Fields.lastModificationTime, assessments.getSort());

        verify(loadAssessmentPort, times(1)).loadSpaceAssessments(any(), any(), anyInt(), anyInt());
        verify(assessmentPermissionChecker, times(8)).isAuthorized(any(UUID.class), any(UUID.class), any(AssessmentPermission.class));
    }

    @Test
    void testGetSpaceAssessmentList_WhenUserDoesNotHaveAnyAssessmentPermission_ThenItemsMaturityLevelAndConfidenceValueAreNull() {
        Long spaceId = 123L;
        var assessment1 = AssessmentMother.assessmentListItem(spaceId, AssessmentKitMother.kit().getId());

        var paginatedRes = new PaginatedResponse<>(
            List.of(assessment1),
            0,
            20,
            AssessmentJpaEntity.Fields.lastModificationTime,
            Sort.Direction.DESC.name().toLowerCase(),
            1
        );

        UUID currentUserId = UUID.randomUUID();

        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(true);
        when(loadAssessmentPort.loadSpaceAssessments(spaceId, currentUserId, 0, 20))
            .thenReturn(paginatedRes);
        when(assessmentPermissionChecker.isAuthorized(any(UUID.class), any(UUID.class), any(AssessmentPermission.class)))
            .thenReturn(false);

        var param = new GetSpaceAssessmentListUseCase.Param(spaceId, currentUserId, 20, 0);
        var assessments = service.getAssessmentList(param);

        List<GetSpaceAssessmentListUseCase.SpaceAssessmentListItem> items = assessments.getItems();
        assertEquals(1, items.size());
        assertNull(items.getFirst().maturityLevel());
        assertNull(items.getFirst().confidenceValue());
        assertFalse(assessments.getItems().getFirst().permissions().canManageSettings());
        assertFalse(assessments.getItems().getFirst().permissions().canViewDashboard());
        assertFalse(assessments.getItems().getFirst().permissions().canViewReport());
        assertFalse(assessments.getItems().getFirst().permissions().canViewQuestionnaires());

        verify(assessmentPermissionChecker, times(4))
            .isAuthorized(any(UUID.class), any(UUID.class), any(AssessmentPermission.class));
    }

    @Test
    void testGetSpaceAssessmentList_CurrentUserNotAllowed_ThrowAccessDeniedException() {
        long spaceId = 2L;
        UUID currentUserId = UUID.randomUUID();
        when(checkSpaceAccessPort.checkIsMember(spaceId, currentUserId)).thenReturn(false);

        var param = new GetSpaceAssessmentListUseCase.Param(spaceId, currentUserId, 10, 0);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }
}
