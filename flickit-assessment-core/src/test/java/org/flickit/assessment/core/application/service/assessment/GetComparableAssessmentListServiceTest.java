package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.core.application.domain.AssessmentListItem;
import org.flickit.assessment.core.application.port.in.assessment.GetComparableAssessmentListUseCase;
import org.flickit.assessment.core.application.port.out.assessment.LoadAssessmentListPort;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetComparableAssessmentListServiceTest {

    @InjectMocks
    private GetComparableAssessmentListService service;

    @Mock
    private LoadAssessmentListPort loadAssessmentPort;

    @Test
    void testGetComparableAssessmentList_ResultsFoundWhenKitIdIsNull_ThenAllUserAssessmentsReturned() {
        UUID currentUserId = UUID.randomUUID();
        int page = 0;
        int size = 20;

        var assessment1 = AssessmentMother.assessmentListItem(1L, AssessmentKitMother.kit().getId());
        var assessment2 = AssessmentMother.assessmentListItem(2L, AssessmentKitMother.kit().getId());

        var paginatedRes = new PaginatedResponse<>(
            List.of(assessment1, assessment2),
            page,
            size,
            AssessmentJpaEntity.Fields.lastModificationTime,
            Sort.Direction.DESC.name().toLowerCase(),
            2
        );

        when(loadAssessmentPort.loadComparableAssessments(null, currentUserId, page, size))
            .thenReturn(paginatedRes);

        var param = new GetComparableAssessmentListUseCase.Param(null, currentUserId, size, page);
        var assessments = service.getComparableAssessmentList(param);

        ArgumentCaptor<Long> kitIdArgument = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UUID> currentUserIdArgument = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> sizeArgument = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> pageArgument = ArgumentCaptor.forClass(Integer.class);
        verify(loadAssessmentPort).loadComparableAssessments(
            kitIdArgument.capture(),
            currentUserIdArgument.capture(),
            pageArgument.capture(),
            sizeArgument.capture());

        assertNull(kitIdArgument.getValue());
        assertEquals(currentUserId, currentUserIdArgument.getValue());
        assertEquals(20, sizeArgument.getValue());
        assertEquals(0, pageArgument.getValue());

        assertEquals(2, assessments.getItems().size());
        assertEquals(20, assessments.getSize());
        assertEquals(0, assessments.getPage());
        assertEquals(2, assessments.getTotal());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), assessments.getOrder());
        assertEquals(AssessmentJpaEntity.Fields.lastModificationTime, assessments.getSort());

        verify(loadAssessmentPort, times(1)).loadComparableAssessments(any(), any(), anyInt(), anyInt());
    }

    @Test
    void testGetComparableAssessmentList_ResultsFoundWithKitId_ItemsReturned() {
        Long kitId = AssessmentKitMother.kit().getId();
        UUID currentUserId = UUID.randomUUID();
        int page = 0;
        int size = 20;

        var assessment1 = AssessmentMother.assessmentListItem(1L, kitId);
        var assessment2 = AssessmentMother.assessmentListItem(2L, kitId);

        var paginatedRes = new PaginatedResponse<>(
            List.of(assessment1, assessment2),
            page,
            size,
            AssessmentJpaEntity.Fields.lastModificationTime,
            Sort.Direction.DESC.name().toLowerCase(),
            2
        );

        when(loadAssessmentPort.loadComparableAssessments(kitId, currentUserId, page, size))
            .thenReturn(paginatedRes);

        var param = new GetComparableAssessmentListUseCase.Param(kitId, currentUserId, size, page);
        var assessments = service.getComparableAssessmentList(param);

        ArgumentCaptor<Long> kitIdArgument = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<UUID> currentUserIdArgument = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<Integer> sizeArgument = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> pageArgument = ArgumentCaptor.forClass(Integer.class);
        verify(loadAssessmentPort).loadComparableAssessments(
            kitIdArgument.capture(),
            currentUserIdArgument.capture(),
            pageArgument.capture(),
            sizeArgument.capture());

        assertEquals(kitId, kitIdArgument.getValue());
        assertEquals(currentUserId, currentUserIdArgument.getValue());
        assertEquals(20, sizeArgument.getValue());
        assertEquals(0, pageArgument.getValue());

        assertEquals(2, assessments.getItems().size());
        assertEquals(20, assessments.getSize());
        assertEquals(0, assessments.getPage());
        assertEquals(2, assessments.getTotal());
        assertEquals(Sort.Direction.DESC.name().toLowerCase(), assessments.getOrder());
        assertEquals(AssessmentJpaEntity.Fields.lastModificationTime, assessments.getSort());

        verify(loadAssessmentPort, times(1)).loadComparableAssessments(any(), any(), anyInt(), anyInt());
    }

    @Test
    void testGetComparableAssessmentList_NoResultsFound_NoItemReturned() {
        long kitId = 123L;
        UUID currentUserId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        PaginatedResponse<AssessmentListItem> paginatedResponse = new PaginatedResponse<>(
            new ArrayList<>(),
            page,
            size,
            "lastModificationTime",
            "DESC",
            2);

        when(loadAssessmentPort.loadComparableAssessments(kitId, currentUserId, page, size)).thenReturn(paginatedResponse);
        var param = new GetComparableAssessmentListUseCase.Param(kitId, currentUserId, size, page);

        PaginatedResponse<GetComparableAssessmentListUseCase.ComparableAssessmentListItem> result = service.getComparableAssessmentList(param);
        assertEquals(0, result.getItems().size());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
    }
}
