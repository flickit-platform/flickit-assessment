package org.flickit.flickitassessmentcore.application.service.assessment;

import org.flickit.flickitassessmentcore.application.domain.*;
import org.flickit.flickitassessmentcore.application.domain.mother.*;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.application.domain.report.TopAttribute;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CompareAssessmentsUseCase;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CompareAssessmentsUseCase.Param;
import org.flickit.flickitassessmentcore.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelsByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadAttributeValueListPort;
import org.flickit.flickitassessmentcore.application.port.out.subject.LoadSubjectReportInfoWithMaturityLevelsPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectsPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_ASSESSMENT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

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
    @Mock
    private LoadSubjectsPort loadSubjectsPort;
    @Mock
    private LoadSubjectReportInfoWithMaturityLevelsPort loadSubjectReportInfoPort;

    private UUID assessmentId1;
    private UUID assessmentId2;
    private AssessmentKit kit;
    private Subject subject;
    private AssessmentResult assessmentResult1;
    private AssessmentResult assessmentResult2;
    private List<QualityAttributeValue> qualityAttributeValues1;
    private List<QualityAttributeValue> qualityAttributeValues2;
    private int assessmentResult1AnsweredQuestions = 10;
    private int assessmentResult2AnsweredQuestions = 10;

    @Test
    void testCompareAssessments_ValidIds_ReturnCompareObjects() {
        createTwoAssessmentResult();
        doMocks();
        var param = new Param(new LinkedHashSet<>(List.of(assessmentId1, assessmentId2)));

        var compareListItems = service.compareAssessments(param);

//        assert first assessment compare item attributes
        CompareAssessmentsUseCase.CompareListItem compareItem1 = compareListItems.get(0);
        assertEquals(assessmentId1, compareItem1.id());
        assertEquals(assessmentResult1.getAssessment().getTitle(), compareItem1.title());
        assertEquals(kit.getId(), compareItem1.assessmentKitId());
        assertEquals(assessmentResult1.getAssessment().getSpaceId(), compareItem1.spaceId());
        assertEquals(assessmentResult1.getAssessment().getColorId(), compareItem1.color().getId());
        assertEquals(assessmentResult1.getMaturityLevel().getId(), compareItem1.maturityLevelId());
        assertEquals(assessmentResult1AnsweredQuestions, compareItem1.answeredQuestions());

//        level of both attribute is more than mid-level, so they are strengths of assessment
        assertEquals(
            List.of(
                new TopAttribute(qualityAttributeValues1.get(0).getQualityAttribute().getId()),
                new TopAttribute(qualityAttributeValues1.get(1).getQualityAttribute().getId())
            )
            , compareItem1.topStrengths());
        assertEquals(List.of(), compareItem1.topWeaknesses());
        SubjectReport subjectReport = new SubjectReport(
            new SubjectReport.SubjectReportItem(subject.getId(), (long) MaturityLevelMother.LEVEL_THREE_ID, true),
            List.of(
                new TopAttribute(qualityAttributeValues1.get(0).getQualityAttribute().getId()),
                new TopAttribute(qualityAttributeValues1.get(1).getQualityAttribute().getId())
            ),
            List.of(),
            List.of(
                new SubjectReport.AttributeReportItem(qualityAttributeValues1.get(0).getQualityAttribute().getId(), (long) MaturityLevelMother.LEVEL_THREE_ID),
                new SubjectReport.AttributeReportItem(qualityAttributeValues1.get(1).getQualityAttribute().getId(), (long) MaturityLevelMother.LEVEL_THREE_ID)
            )
        );
        assertEquals(List.of(subjectReport), compareItem1.subjects());

//        assert second assessment compare item attributes
        CompareAssessmentsUseCase.CompareListItem compareItem2 = compareListItems.get(1);
        assertEquals(assessmentId2, compareItem2.id());
        assertEquals(assessmentResult2.getAssessment().getTitle(), compareItem2.title());
        assertEquals(kit.getId(), compareItem2.assessmentKitId());
        assertEquals(assessmentResult2.getAssessment().getSpaceId(), compareItem2.spaceId());
        assertEquals(assessmentResult2.getAssessment().getColorId(), compareItem2.color().getId());
        assertEquals(assessmentResult2.getMaturityLevel().getId(), compareItem2.maturityLevelId());
        assertEquals(assessmentResult2AnsweredQuestions, compareItem2.answeredQuestions());

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
        assertEquals(List.of(subjectReport2), compareItem2.subjects());
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
                return new GetAssessmentProgressPort.Result(assessmentResult1.getId(), assessmentResult1AnsweredQuestions);
            else if (assessmentResultId.equals(assessmentId2))
                return new GetAssessmentProgressPort.Result(assessmentResult2.getId(), assessmentResult2AnsweredQuestions);
            throw new ResourceNotFoundException(GET_ASSESSMENT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND);
        }).when(getAssessmentProgressPort).getAssessmentProgressById(any(UUID.class));

//        Mock loadSubjectsPort
        when(loadSubjectsPort.loadSubjectIdsByAssessmentId(any())).thenReturn(List.of(subject.getId()));

//        Mock loadSubjectReportInfoPort
        doAnswer(invocation -> {
            UUID assessmentId = invocation.getArgument(0, UUID.class);
            if (assessmentId.equals(assessmentId1))
                return assessmentResult1;
            else if (assessmentId.equals(assessmentId2))
                return assessmentResult2;
            return null;
        }).when(loadSubjectReportInfoPort).loadWithMaturityLevels(any(UUID.class), any(), any());
    }
}
