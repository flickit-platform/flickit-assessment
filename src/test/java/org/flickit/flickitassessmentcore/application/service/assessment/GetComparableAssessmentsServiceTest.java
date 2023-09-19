package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.application.domain.mother.AssessmentMother;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetComparableAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.GetComparableAssessmentsUseCase.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentListItemsBySpaceAndKitPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetComparableAssessmentsServiceTest {

    @InjectMocks
    private GetComparableAssessmentsService service;

    @Mock
    private LoadAssessmentListItemsBySpaceAndKitPort loadAssessmentListItemsBySpaceAndKitPort;

    @Test
    void getComparableAssessments_ValidResults() {
        var assessment1 = AssessmentMother.assessmentListItem();
        var assessment2 = AssessmentMother.assessmentListItem();

        var spaceIds = List.of(assessment1.spaceId());
        var paginatedRes = new PaginatedResponse<>(
            List.of(assessment1, assessment2),
            0,
            20,
            AssessmentJpaEntity.Fields.LAST_MODIFICATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            2
        );

        when(loadAssessmentListItemsBySpaceAndKitPort.loadBySpaceIdAndKitId(
            spaceIds,
            assessment1.assessmentKitId(),
            0,
            20)).thenReturn(paginatedRes);

        var param = new GetComparableAssessmentsUseCase.Param(spaceIds, assessment1.assessmentKitId(), 20, 0);
        var assessments = service.getComparableAssessments(param);

        assertEquals(assessments.getItems().size(), 2);
        assertEquals(assessments.getSize(), 20);
        assertEquals(assessments.getPage(), 0);
        assertEquals(assessments.getTotal(), 2);
        assertEquals(assessments.getOrder(), Sort.Direction.DESC.name().toLowerCase());
        assertEquals(assessments.getSort(), AssessmentJpaEntity.Fields.LAST_MODIFICATION_TIME);
    }

    @Test
    void getComparableAssessments_ValidResultsWithKit() {
        var assessment1 = AssessmentMother.assessmentListItem();
        var assessment2 = AssessmentMother.assessmentListItem();

        var spaceIds = List.of(assessment1.spaceId());
        var paginatedRes = new PaginatedResponse<>(
            List.of(assessment1),
            0,
            20,
            AssessmentJpaEntity.Fields.LAST_MODIFICATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            1
        );

        when(loadAssessmentListItemsBySpaceAndKitPort.loadBySpaceIdAndKitId(
            spaceIds,
            assessment1.assessmentKitId(),
            0,
            20)).thenReturn(paginatedRes);

        var param = new GetComparableAssessmentsUseCase.Param(spaceIds, assessment1.assessmentKitId(), 20, 0);
        var assessments = service.getComparableAssessments(param);

        assertEquals(assessments.getItems().size(), 1);
        assertEquals(assessments.getSize(), 20);
        assertEquals(assessments.getPage(), 0);
        assertEquals(assessments.getTotal(), 1);
        assertEquals(assessments.getOrder(), Sort.Direction.DESC.name().toLowerCase());
        assertEquals(assessments.getSort(), AssessmentJpaEntity.Fields.LAST_MODIFICATION_TIME);
    }

    @Test
    void getComparableAssessments_NoResultsFound() {
        var spaceIds = List.of(10L, 11L);
        var paginatedRes = new PaginatedResponse<AssessmentListItem>(
            List.of(),
            0,
            20,
            AssessmentJpaEntity.Fields.LAST_MODIFICATION_TIME,
            Sort.Direction.DESC.name().toLowerCase(),
            0
        );

        when(loadAssessmentListItemsBySpaceAndKitPort.loadBySpaceIdAndKitId(
            spaceIds,
            null,
            0,
            20)).thenReturn(paginatedRes);

        var param = new GetComparableAssessmentsUseCase.Param(spaceIds, null, 20, 0);
        var assessments = service.getComparableAssessments(param);

        assertEquals(assessments.getItems().size(), 0);
        assertEquals(assessments.getSize(), 20);
        assertEquals(assessments.getPage(), 0);
        assertEquals(assessments.getTotal(), 0);
        assertEquals(assessments.getOrder(), Sort.Direction.DESC.name().toLowerCase());
        assertEquals(assessments.getSort(), AssessmentJpaEntity.Fields.LAST_MODIFICATION_TIME);
    }
}
