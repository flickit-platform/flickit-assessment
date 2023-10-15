package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.domain.*;
import org.flickit.flickitassessmentcore.application.domain.crud.AssessmentListItem;
import org.flickit.flickitassessmentcore.application.domain.mother.*;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.application.domain.report.TopAttribute;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CompareAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CompareAssessmentsUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelsByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.flickitassessmentcore.application.service.exception.AssessmentsNotComparableException;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.COMPARE_ASSESSMENTS_ASSESSMENTS_NOT_COMPARABLE;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ASSESSMENT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompareAssessmentsServiceTest {

    @InjectMocks
    private CompareAssessmentsService service;
    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;
    @Mock
    private LoadMaturityLevelsByKitPort loadMaturityLevelsByKitPort;
    @Mock
    private LoadAttributeValueListPort loadAttributeValueListPort;
    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    private UUID assessmentId1;
    private UUID assessmentId2;
    private AssessmentKit kit;
    private Subject subject;
    private AssessmentResult assessmentResult1;
    private AssessmentResult assessmentResult2;
    private List<QualityAttributeValue> qualityAttributeValues1;
    private List<QualityAttributeValue> qualityAttributeValues2;
    private final static int ASSESSMENT_RESULT_1_ANSWERED_QUESTIONS = 10;
    private final static int ASSESSMENT_RESULT_2_ANSWERED_QUESTIONS = 10;

    @Test
    void testCompareAssessments_ValidIds_ReturnCompareObjects() {
        createTwoAssessmentResult();
        doMocks();
        var param = new Param(List.of(assessmentId1, assessmentId2));

        var compareListItems = service.compareAssessments(param);

//        assert first assessment compare item attributes
        CompareAssessmentsUseCase.CompareListItem compareItem1 = compareListItems.get(0);
        AssessmentListItem assessment1 = compareItem1.assessment();
        assertEquals(assessmentId1, assessment1.id());
        assertEquals(assessmentResult1.getAssessment().getTitle(), assessment1.title());
        assertEquals(kit.getId(), assessment1.assessmentKitId());
        assertEquals(assessmentResult1.getAssessment().getSpaceId(), assessment1.spaceId());
        assertEquals(assessmentResult1.getAssessment().getColorId(), assessment1.color().getId());
        assertEquals(assessmentResult1.getAssessment().getLastModificationTime(), assessment1.lastModificationTime());
        assertEquals(assessmentResult1.getMaturityLevel().getId(), assessment1.maturityLevelId());
        assertEquals(assessmentResult1.isValid(), assessment1.isCalculateValid());
        assertEquals(ASSESSMENT_RESULT_1_ANSWERED_QUESTIONS, compareItem1.answeredQuestions());

//        level of both attribute is more than mid-level, so they are strengths of assessment
        assertEquals(
            List.of(
                new TopAttribute(qualityAttributeValues1.get(0).getQualityAttribute().getId()),
                new TopAttribute(qualityAttributeValues1.get(1).getQualityAttribute().getId())
            )
            , compareItem1.topStrengths());
        assertEquals(List.of(), compareItem1.topWeaknesses());

//        assert second assessment compare item attributes
        CompareAssessmentsUseCase.CompareListItem compareItem2 = compareListItems.get(1);
        AssessmentListItem assessment2 = compareItem2.assessment();
        assertEquals(assessmentId2, assessment2.id());
        assertEquals(assessmentResult2.getAssessment().getTitle(), assessment2.title());
        assertEquals(kit.getId(), assessment2.assessmentKitId());
        assertEquals(assessmentResult2.getAssessment().getSpaceId(), assessment2.spaceId());
        assertEquals(assessmentResult2.getAssessment().getColorId(), assessment2.color().getId());
        assertEquals(assessmentResult2.getAssessment().getLastModificationTime(), assessment2.lastModificationTime());
        assertEquals(assessmentResult2.getMaturityLevel().getId(), assessment2.maturityLevelId());
        assertEquals(assessmentResult2.isValid(), assessment2.isCalculateValid());
        assertEquals(ASSESSMENT_RESULT_2_ANSWERED_QUESTIONS, compareItem2.answeredQuestions());

//        level of first attribute is more than mid-level, so it is strength
        assertEquals(List.of(new TopAttribute(qualityAttributeValues2.get(0).getQualityAttribute().getId())), compareItem2.topStrengths());

//        level of second attribute is less than mid-level, so it is weakness
        assertEquals(List.of(new TopAttribute(qualityAttributeValues2.get(1).getQualityAttribute().getId())), compareItem2.topWeaknesses());
        SubjectReport subjectReport2 = new SubjectReport(
            new SubjectReport.SubjectReportItem(subject.getId(), (long) MaturityLevelMother.LEVEL_TWO_ID, true),
            List.of(new TopAttribute(qualityAttributeValues2.get(0).getQualityAttribute().getId())),
            List.of(new TopAttribute(qualityAttributeValues2.get(1).getQualityAttribute().getId())),
            List.of(
                new SubjectReport.AttributeReportItem(qualityAttributeValues2.get(0).getQualityAttribute().getId(), (long) MaturityLevelMother.LEVEL_THREE_ID),
                new SubjectReport.AttributeReportItem(qualityAttributeValues2.get(1).getQualityAttribute().getId(), (long) MaturityLevelMother.LEVEL_ONE_ID)
            )
        );
    }

    @Test
    void testCompareAssessments_AssessmentsWithTwoDifferentKits_ThrowException() {
//        create two different kits
        var kit1 = AssessmentKitMother.kit();
        var kit2 = AssessmentKitMother.kit();

//        create first assessment result
        List<SubjectValue> subjectValues1 = List.of(
            SubjectValueMother.withQAValuesAndMaturityLevel(qualityAttributeValues1, MaturityLevelMother.levelThree()));
        var assessmentResult1 =
            AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevelAndAssessmentKit(subjectValues1, MaturityLevelMother.levelThree(), kit1);

//        create second assessment result
        List<SubjectValue> subjectValues2 = List.of(
            SubjectValueMother.withQAValuesAndMaturityLevelAndSubject(qualityAttributeValues2, MaturityLevelMother.levelTwo(), subject));
        var assessmentResult2 =
            AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevelAndAssessmentKit(subjectValues2, MaturityLevelMother.levelTwo(), kit2);

        var assessmentId1 = assessmentResult1.getAssessment().getId();
        var assessmentId2 = assessmentResult2.getAssessment().getId();
        var param = new Param(List.of(assessmentId1, assessmentId2));

//        do mocks
        doAnswer(invocation -> {
            UUID assessmentId = invocation.getArgument(0, UUID.class);
            if (assessmentId.equals(assessmentId1))
                return Optional.of(assessmentResult1);
            else if (assessmentId.equals(assessmentId2))
                return Optional.of(assessmentResult2);
            return Optional.empty();
        }).when(loadAssessmentResultPort).loadByAssessmentId(any(UUID.class));

        var exception = assertThrows(AssessmentsNotComparableException.class, () -> service.compareAssessments(param));

        assertEquals(COMPARE_ASSESSMENTS_ASSESSMENTS_NOT_COMPARABLE, exception.getMessage());
        verifyNoInteractions(
            loadMaturityLevelsByKitPort,
            loadAttributeValueListPort,
            getAssessmentProgressPort
        );

    }

    private void createTwoAssessmentResult() {
//        create assessmentKit
//        assessment kit should be same with in all assessments for compare
        kit = AssessmentKitMother.kit();

//        create first assessment result
        qualityAttributeValues1 = List.of(
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1));
        qualityAttributeValues1.get(0).setMaturityLevel(MaturityLevelMother.levelThree());
        qualityAttributeValues1.get(1).setMaturityLevel(MaturityLevelMother.levelThree());
        List<SubjectValue> subjectValues1 = List.of(
            SubjectValueMother.withQAValuesAndMaturityLevel(qualityAttributeValues1, MaturityLevelMother.levelThree()));
//        subject of all assessments should be same
        subject = subjectValues1.get(0).getSubject();
        assessmentResult1 =
            AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevelAndAssessmentKit(subjectValues1, MaturityLevelMother.levelThree(), kit);

//        create second assessment result
        qualityAttributeValues2 = List.of(
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1));
        qualityAttributeValues2.get(0).setMaturityLevel(MaturityLevelMother.levelThree());
        qualityAttributeValues2.get(1).setMaturityLevel(MaturityLevelMother.levelOne());
        List<SubjectValue> subjectValues2 = List.of(
            SubjectValueMother.withQAValuesAndMaturityLevelAndSubject(qualityAttributeValues2, MaturityLevelMother.levelTwo(), subject));
        assessmentResult2 =
            AssessmentResultMother.validResultWithSubjectValuesAndMaturityLevelAndAssessmentKit(subjectValues2, MaturityLevelMother.levelTwo(), kit);

//        assign assessmentIds
        assessmentId1 = assessmentResult1.getAssessment().getId();
        assessmentId2 = assessmentResult2.getAssessment().getId();
    }

    private void doMocks() {
//        Mock loadAssessmentResultPort
        doAnswer(invocation -> {
            UUID assessmentId = invocation.getArgument(0, UUID.class);
            if (assessmentId.equals(assessmentId1))
                return Optional.of(assessmentResult1);
            else if (assessmentId.equals(assessmentId2))
                return Optional.of(assessmentResult2);
            return Optional.empty();
        }).when(loadAssessmentResultPort).loadByAssessmentId(any(UUID.class));

//        Mock loadMaturityLevelsByKitPort
        when(loadMaturityLevelsByKitPort.loadByKitId(kit.getId())).thenReturn(MaturityLevelMother.allLevels());

//        Mock loadAttributeValueListPort
        doAnswer(invocation -> {
            UUID assessmentResultId = invocation.getArgument(0, UUID.class);
            if (assessmentResultId.equals(assessmentResult1.getId()))
                return qualityAttributeValues1;
            else if (assessmentResultId.equals(assessmentResult2.getId()))
                return qualityAttributeValues2;
            return List.of();
        }).when(loadAttributeValueListPort).loadAttributeValues(any(UUID.class), any());

//        Mock getAssessmentProgressPort
        doAnswer(invocation -> {
            UUID assessmentResultId = invocation.getArgument(0, UUID.class);
            if (assessmentResultId.equals(assessmentId1))
                return new GetAssessmentProgressPort.Result(assessmentResult1.getId(), ASSESSMENT_RESULT_1_ANSWERED_QUESTIONS);
            else if (assessmentResultId.equals(assessmentId2))
                return new GetAssessmentProgressPort.Result(assessmentResult2.getId(), ASSESSMENT_RESULT_2_ANSWERED_QUESTIONS);
            throw new ResourceNotFoundException(GET_ASSESSMENT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND);
        }).when(getAssessmentProgressPort).getAssessmentProgressById(any(UUID.class));
    }
}
