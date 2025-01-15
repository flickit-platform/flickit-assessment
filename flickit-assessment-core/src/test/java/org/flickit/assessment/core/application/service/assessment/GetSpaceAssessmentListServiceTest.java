package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentPermissionChecker;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentListItem;
import org.flickit.assessment.core.application.port.in.assessment.GetSpaceAssessmentListUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentListPort;
import org.flickit.assessment.core.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentKitMother.kit;
import static org.flickit.assessment.core.test.fixture.application.AssessmentMother.assessmentListItem;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

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
    void testGetSpaceAssessmentList_whenCurrentUserIsNotSpaceMember_thenThrowAccessDeniedException() {
        var param = createParam(GetSpaceAssessmentListUseCase.Param.ParamBuilder::build);

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getAssessmentList(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testGetSpaceAssessmentList_NoResultsFound_NoItemReturned() {
        var param = createParam(GetSpaceAssessmentListUseCase.Param.ParamBuilder::build);

        PaginatedResponse<AssessmentListItem> paginatedResponse = new PaginatedResponse<>(
            List.of(),
            param.getPage(),
            param.getSize(),
            "lastModificationTime",
            "DESC",
            0);

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAssessmentPort.loadSpaceAssessments(param.getSpaceId(), param.getCurrentUserId(), param.getPage(), param.getSize())).thenReturn(paginatedResponse);

        var result = service.getAssessmentList(param);

        assertPaginationProperties(paginatedResponse, result);
    }

    @Test
    void testGetSpaceAssessmentList_ResultsFoundForSpaceId_ItemsReturned() {
        var param = createParam(GetSpaceAssessmentListUseCase.Param.ParamBuilder::build);

        var assessment1 = assessmentListItem(param.getSpaceId(), kit().getId(), false);
        var assessment2 = assessmentListItem(param.getSpaceId(), kit().getId(), true);
        var assessment3 = assessmentListItem(param.getSpaceId(), kit().getId(), true);

        var paginatedRes = new PaginatedResponse<>(
            List.of(assessment1, assessment2, assessment3),
            param.getPage(),
            param.getSize(),
            AssessmentJpaEntity.Fields.lastModificationTime,
            Sort.Direction.DESC.name().toLowerCase(),
            3
        );

        when(checkSpaceAccessPort.checkIsMember(param.getSpaceId(), param.getCurrentUserId())).thenReturn(true);
        when(loadAssessmentPort.loadSpaceAssessments(param.getSpaceId(), param.getCurrentUserId(), param.getPage(), param.getSize()))
            .thenReturn(paginatedRes);

        when(assessmentPermissionChecker.isAuthorized(assessment1.id(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(false);
        when(assessmentPermissionChecker.isAuthorized(assessment1.id(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(false);
        when(assessmentPermissionChecker.isAuthorized(assessment1.id(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(true);

        when(assessmentPermissionChecker.isAuthorized(assessment2.id(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);
        when(assessmentPermissionChecker.isAuthorized(assessment2.id(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(false);
        when(assessmentPermissionChecker.isAuthorized(assessment2.id(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(true);

        when(assessmentPermissionChecker.isAuthorized(assessment3.id(), param.getCurrentUserId(), VIEW_GRAPHICAL_REPORT))
            .thenReturn(true);
        when(assessmentPermissionChecker.isAuthorized(assessment3.id(), param.getCurrentUserId(), VIEW_DASHBOARD))
            .thenReturn(true);
        when(assessmentPermissionChecker.isAuthorized(assessment3.id(), param.getCurrentUserId(), VIEW_ASSESSMENT_QUESTIONNAIRE_LIST))
            .thenReturn(true);

        var result = service.getAssessmentList(param);

        assertPaginationProperties(paginatedRes, result);

        assertThat(result.getItems())
            .zipSatisfy(paginatedRes.getItems(), (actual, expected) -> {
                assertEquals(expected.id(), actual.id());
                assertEquals(expected.title(), actual.title());
                assertEquals(expected.kit(), actual.kit());
                assertEquals(expected.lastModificationTime(), actual.lastModificationTime());
                assertTrue(actual.isCalculateValid());
                assertTrue(actual.isConfidenceValid());
                assertNotNull(actual.permissions());
            });

        // for first assessment
        var firstAssessment = result.getItems().getFirst();
        assertNull(firstAssessment.maturityLevel());
        assertNull(firstAssessment.confidenceValue());
        assertFalse(firstAssessment.permissions().canManageSettings());
        assertFalse(firstAssessment.permissions().canViewReport());
        assertFalse(firstAssessment.permissions().canViewDashboard());
        assertTrue(firstAssessment.permissions().canViewQuestionnaires());

        // for second assessment
        var secondAssessment = result.getItems().get(1);
        assertEquals(assessment2.maturityLevel(), secondAssessment.maturityLevel());
        assertEquals(assessment2.confidenceValue(), secondAssessment.confidenceValue());
        assertTrue(secondAssessment.permissions().canManageSettings());
        assertTrue(secondAssessment.permissions().canViewReport());
        assertFalse(secondAssessment.permissions().canViewDashboard());
        assertTrue(secondAssessment.permissions().canViewQuestionnaires());

        // for third assessment
        var thirdAssessment = result.getItems().getLast();
        assertEquals(assessment3.maturityLevel(), thirdAssessment.maturityLevel());
        assertEquals(assessment3.confidenceValue(), thirdAssessment.confidenceValue());
        assertTrue(thirdAssessment.permissions().canManageSettings());
        assertTrue(thirdAssessment.permissions().canViewReport());
        assertTrue(thirdAssessment.permissions().canViewDashboard());
        assertTrue(thirdAssessment.permissions().canViewQuestionnaires());
    }

    private static void assertPaginationProperties(PaginatedResponse<AssessmentListItem> expected,
                                                   PaginatedResponse<GetSpaceAssessmentListUseCase.SpaceAssessmentListItem> result) {
        assertEquals(expected.getItems().size(), result.getItems().size());
        assertEquals(expected.getSize(), result.getSize());
        assertEquals(expected.getPage(), result.getPage());
        assertEquals(expected.getTotal(), result.getTotal());
    }

    private GetSpaceAssessmentListUseCase.Param createParam(Consumer<GetSpaceAssessmentListUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private GetSpaceAssessmentListUseCase.Param.ParamBuilder paramBuilder() {
        return GetSpaceAssessmentListUseCase.Param.builder()
            .spaceId(123L)
            .currentUserId(UUID.randomUUID())
            .size(10)
            .page(0);
    }
}
