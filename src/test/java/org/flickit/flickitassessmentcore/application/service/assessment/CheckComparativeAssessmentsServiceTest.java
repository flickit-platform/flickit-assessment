package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.domain.mother.AssessmentMother;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CheckComparativeAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentsPort;
import org.flickit.flickitassessmentcore.application.service.exception.AssessmentsNotComparableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CHECK_COMPARATIVE_ASSESSMENTS_ASSESSMENTS_NOT_COMPARABLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckComparativeAssessmentsServiceTest {

    @InjectMocks
    private CheckComparativeAssessmentsService service;

    @Mock
    private LoadAssessmentsPort loadAssessmentsPort;

    @Test
    void testCheckComparativeAssessments_ValidInput() {
        var listItem1 = AssessmentMother.createComparativeAssessmentListItem(1L);
        var listItem2 = AssessmentMother.createComparativeAssessmentListItem(1L);
        var listItem3 = AssessmentMother.createComparativeAssessmentListItem(1L);
        var assessmentItemsList = List.of(listItem1, listItem2, listItem3);
        var assessmentIds = List.of(
            listItem1.id(),
            listItem2.id(),
            listItem3.id()
        );
        when(loadAssessmentsPort.load(anyList())).thenReturn(assessmentItemsList);

        var param = new CheckComparativeAssessmentsUseCase.Param(assessmentIds);
        var result = service.checkComparativeAssessments(param);

        assertEquals(assessmentItemsList.size(), result.size());
        assertDoesNotThrow(() -> service.checkComparativeAssessments(param));
    }

    @Test
    void testCheckComparativeAssessments_InValidInput() {
        var listItem1 = AssessmentMother.createComparativeAssessmentListItem(1L);
        var listItem2 = AssessmentMother.createComparativeAssessmentListItem(2L);
        var listItem3 = AssessmentMother.createComparativeAssessmentListItem(1L);
        var assessmentItemsList = List.of(listItem1, listItem2, listItem3);
        var assessmentIds = List.of(
            listItem1.id(),
            listItem2.id(),
            listItem3.id()
        );
        when(loadAssessmentsPort.load(anyList())).thenReturn(assessmentItemsList);

        var param = new CheckComparativeAssessmentsUseCase.Param(assessmentIds);

        var throwable = assertThrows(AssessmentsNotComparableException.class,
            () -> service.checkComparativeAssessments(param));
        assertThat(throwable).hasMessage(CHECK_COMPARATIVE_ASSESSMENTS_ASSESSMENTS_NOT_COMPARABLE);
    }
}
