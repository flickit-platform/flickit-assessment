package org.flickit.flickitassessmentcore.adapter.out.calculate;

import org.flickit.flickitassessmentcore.adapter.out.persistence.answer.AnswerJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue.SubjectValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue.SubjectValueJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.rest.answeroption.AnswerOptionRestAdapter;
import org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel.MaturityLevelRestAdapter;
import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionRestAdapter;
import org.flickit.flickitassessmentcore.adapter.out.rest.subject.SubjectRestAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.flickit.flickitassessmentcore.adapter.out.calculate.AssessmentCalculateInfoCreator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentCalculateInfoLoadAdapterTest {

    @InjectMocks
    AssessmentCalculateInfoLoadAdapter adapter;

    @Mock
    private AssessmentResultJpaRepository assessmentResultRepo;

    @Mock
    private AnswerJpaRepository answerRepo;

    @Mock
    private QualityAttributeValueJpaRepository qualityAttrValueRepo;

    @Mock
    private SubjectValueJpaRepository subjectValueRepo;


    @Mock
    private SubjectRestAdapter subjectRestAdapter;

    @Mock
    private QuestionRestAdapter questionRestAdapter;

    @Mock
    private AnswerOptionRestAdapter answerOptionRestAdapter;

    @Mock
    private MaturityLevelRestAdapter maturityLevelRestAdapter;

    @Test
    void testAssessmentCalculateInfoLoad_ValidInputsWithTwoQuestions_ValidResults() {
        Long maturityLevelOfOne = 1L;
        Long maturityLevelOfTwo = 2L;
        Long maturityLevelOfThree = 3L;
        Long maturityLevelOfFour = 4L;
        Long maturityLevelOfFive = 5L;
        var assessmentResultEntity = validSimpleAssessmentResultEntity(null, Boolean.FALSE);
        when(assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentResultEntity.getAssessment().getId()))
            .thenReturn(Optional.of(assessmentResultEntity));

        var subjectValue1 = subjectValueWithMaturityLevel(assessmentResultEntity, null);
        var subjectValue2 = subjectValueWithMaturityLevel(assessmentResultEntity, null);
        List<SubjectValueJpaEntity> subjectValues = List.of(subjectValue1, subjectValue2);
        when(subjectValueRepo.findByAssessmentResultId(assessmentResultEntity.getId())).thenReturn(subjectValues);

        var qav1 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav2 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav3 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav4 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        List<QualityAttributeValueJpaEntity> qualityAttributeValues = List.of(qav1, qav2, qav3, qav4);
        when(qualityAttrValueRepo.findByAssessmentResultId(assessmentResultEntity.getId())).thenReturn(qualityAttributeValues);

        var subjectDto1 = createSubjectDto(subjectValue1.getSubjectId(), List.of(qav1, qav2));
        var subjectDto2 = createSubjectDto(subjectValue2.getSubjectId(), List.of(qav3, qav4));
        when(subjectRestAdapter.loadSubjectsDtoByAssessmentKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(subjectDto1, subjectDto2));

        var questionDto1 = createQuestionDto(1L, maturityLevelOfTwo, qav1.getQualityAttributeId(), qav2.getQualityAttributeId());
        var questionDto2 = createQuestionDto(2L, maturityLevelOfFour, qav3.getQualityAttributeId(), qav4.getQualityAttributeId());
        when(questionRestAdapter.loadByAssessmentKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(questionDto1, questionDto2));

        var answerEntity1 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto1.id(), 3L, Boolean.FALSE);
        var answerEntity2 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto2.id(), 4L, Boolean.FALSE);
        when(answerRepo.findByAssessmentResultIdAndAnswerOptionIdNotNull(assessmentResultEntity.getId()))
            .thenReturn(List.of(answerEntity1, answerEntity2));

        var answerOptionDto1 = createAnswerOptionDto(3L, questionDto1.id(), List.of(createQuestionImpactDto(maturityLevelOfOne, qav1.getQualityAttributeId())));
        var answerOptionDto2 = createAnswerOptionDto(4L, questionDto2.id(), questionDto2.questionImpacts());
        when(answerOptionRestAdapter.loadAnswerOptionByIds(any())).thenReturn(List.of(answerOptionDto1, answerOptionDto2));

        var maturityLevelsDto1 = createMaturityLevelDto(maturityLevelOfOne);
        var maturityLevelsDto2 = createMaturityLevelDto(maturityLevelOfTwo);
        var maturityLevelsDto3 = createMaturityLevelDto(maturityLevelOfThree);
        var maturityLevelsDto4 = createMaturityLevelDto(maturityLevelOfFour);
        var maturityLevelsDto5 = createMaturityLevelDto(maturityLevelOfFive);
        when(maturityLevelRestAdapter.loadMaturityLevelsDtoByKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(maturityLevelsDto1, maturityLevelsDto2, maturityLevelsDto3, maturityLevelsDto4, maturityLevelsDto5));

        var loadedAssessmentResult = adapter.load(assessmentResultEntity.getAssessment().getId());

        assertEquals(assessmentResultEntity.getId(), loadedAssessmentResult.getId());
        assertEquals(assessmentResultEntity.getAssessment().getId(), loadedAssessmentResult.getAssessment().getId());

        var calculatedMaturityLevel = loadedAssessmentResult.calculate();
        System.out.println(calculatedMaturityLevel.getLevel());
    }

    @Test
    void testAssessmentCalculateInfoLoad_ValidInputWithThreeQuestions_ValidResults() {
        Long maturityLevelOfOne = 1L;
        Long maturityLevelOfTwo = 2L;
        Long maturityLevelOfThree = 3L;
        Long maturityLevelOfFour = 4L;
        Long maturityLevelOfFive = 5L;
        var assessmentResultEntity = validSimpleAssessmentResultEntity(null, Boolean.FALSE);
        when(assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentResultEntity.getAssessment().getId()))
            .thenReturn(Optional.of(assessmentResultEntity));

        var subjectValue1 = subjectValueWithMaturityLevel(assessmentResultEntity, null);
        var subjectValue2 = subjectValueWithMaturityLevel(assessmentResultEntity, null);
        var subjectValue3 = subjectValueWithMaturityLevel(assessmentResultEntity, null);
        List<SubjectValueJpaEntity> subjectValues = List.of(subjectValue1, subjectValue2, subjectValue3);
        when(subjectValueRepo.findByAssessmentResultId(assessmentResultEntity.getId())).thenReturn(subjectValues);

        var qav1 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav2 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav3 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav4 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav5 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav6 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        List<QualityAttributeValueJpaEntity> qualityAttributeValues = List.of(qav1, qav2, qav3, qav4, qav5, qav6);
        when(qualityAttrValueRepo.findByAssessmentResultId(assessmentResultEntity.getId())).thenReturn(qualityAttributeValues);

        var subjectDto1 = createSubjectDto(subjectValue1.getSubjectId(), List.of(qav1, qav2));
        var subjectDto2 = createSubjectDto(subjectValue2.getSubjectId(), List.of(qav3, qav4));
        var subjectDto3 = createSubjectDto(subjectValue3.getSubjectId(), List.of(qav5, qav6));
        when(subjectRestAdapter.loadSubjectsDtoByAssessmentKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(subjectDto1, subjectDto2, subjectDto3));

        var questionDto1 = createQuestionDto(1L, maturityLevelOfTwo, qav1.getQualityAttributeId(), qav2.getQualityAttributeId());
        var questionDto2 = createQuestionDto(2L, maturityLevelOfFour, qav3.getQualityAttributeId(), qav4.getQualityAttributeId());
        var questionDto3 = createQuestionDto(3L, maturityLevelOfOne, qav5.getQualityAttributeId(), qav6.getQualityAttributeId());
        when(questionRestAdapter.loadByAssessmentKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(questionDto1, questionDto2, questionDto3));

        var answerEntity1 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto1.id(), 3L, Boolean.FALSE);
        var answerEntity2 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto2.id(), 4L, Boolean.FALSE);
        var answerEntity3 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto3.id(), 2L, Boolean.FALSE);
        when(answerRepo.findByAssessmentResultIdAndAnswerOptionIdNotNull(assessmentResultEntity.getId()))
            .thenReturn(List.of(answerEntity1, answerEntity2
                , answerEntity3
            ));

        var answerOptionDto1 = createAnswerOptionDto(3L, questionDto1.id(), List.of(createQuestionImpactDto(maturityLevelOfOne, qav1.getQualityAttributeId())));
        var answerOptionDto2 = createAnswerOptionDto(4L, questionDto2.id(), questionDto2.questionImpacts());
        var answerOptionDto3 = createAnswerOptionDto(2L, questionDto3.id(), questionDto3.questionImpacts());
        when(answerOptionRestAdapter.loadAnswerOptionByIds(any())).thenReturn(List.of(answerOptionDto1, answerOptionDto2
            , answerOptionDto3
        ));

        var maturityLevelsDto1 = createMaturityLevelDto(maturityLevelOfOne);
        var maturityLevelsDto2 = createMaturityLevelDto(maturityLevelOfTwo);
        var maturityLevelsDto3 = createMaturityLevelDto(maturityLevelOfThree);
        var maturityLevelsDto4 = createMaturityLevelDto(maturityLevelOfFour);
        var maturityLevelsDto5 = createMaturityLevelDto(maturityLevelOfFive);
        when(maturityLevelRestAdapter.loadMaturityLevelsDtoByKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(maturityLevelsDto1, maturityLevelsDto2, maturityLevelsDto3, maturityLevelsDto4, maturityLevelsDto5));

        var loadedAssessmentResult = adapter.load(assessmentResultEntity.getAssessment().getId());

        assertEquals(assessmentResultEntity.getId(), loadedAssessmentResult.getId());
        assertEquals(assessmentResultEntity.getAssessment().getId(), loadedAssessmentResult.getAssessment().getId());

        var calculatedMaturityLevel = loadedAssessmentResult.calculate();
        System.out.println(calculatedMaturityLevel.getLevel());
    }

    @Test
    void testAssessmentCalculateInfoLoad_ValidInputWithOneNotApplicableQuestion_ValidResults() {
        Long maturityLevelOfOne = 1L;
        Long maturityLevelOfTwo = 2L;
        Long maturityLevelOfThree = 3L;
        Long maturityLevelOfFour = 4L;
        Long maturityLevelOfFive = 5L;
        var assessmentResultEntity = validSimpleAssessmentResultEntity(null, Boolean.FALSE);
        when(assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentResultEntity.getAssessment().getId()))
            .thenReturn(Optional.of(assessmentResultEntity));

        var subjectValue1 = subjectValueWithMaturityLevel(assessmentResultEntity, null);
        var subjectValue2 = subjectValueWithMaturityLevel(assessmentResultEntity, null);
        var subjectValue3 = subjectValueWithMaturityLevel(assessmentResultEntity, null);
        List<SubjectValueJpaEntity> subjectValues = List.of(subjectValue1, subjectValue2, subjectValue3);
        when(subjectValueRepo.findByAssessmentResultId(assessmentResultEntity.getId())).thenReturn(subjectValues);

        var qav1 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav2 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav3 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav4 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav5 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav6 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        List<QualityAttributeValueJpaEntity> qualityAttributeValues = List.of(qav1, qav2, qav3, qav4, qav5, qav6);
        when(qualityAttrValueRepo.findByAssessmentResultId(assessmentResultEntity.getId())).thenReturn(qualityAttributeValues);

        var subjectDto1 = createSubjectDto(subjectValue1.getSubjectId(), List.of(qav1, qav2));
        var subjectDto2 = createSubjectDto(subjectValue2.getSubjectId(), List.of(qav3, qav4));
        var subjectDto3 = createSubjectDto(subjectValue3.getSubjectId(), List.of(qav5, qav6));
        when(subjectRestAdapter.loadSubjectsDtoByAssessmentKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(subjectDto1, subjectDto2, subjectDto3));

        var questionDto1 = createQuestionDto(1L, maturityLevelOfTwo, qav1.getQualityAttributeId(), qav2.getQualityAttributeId());
        var questionDto2 = createQuestionDto(2L, maturityLevelOfFour, qav3.getQualityAttributeId(), qav4.getQualityAttributeId());
        var questionDto3 = createQuestionDto(3L, maturityLevelOfOne, qav5.getQualityAttributeId(), qav6.getQualityAttributeId());
        when(questionRestAdapter.loadByAssessmentKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(questionDto1, questionDto2, questionDto3));

        var answerEntity1 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto1.id(), 3L, Boolean.FALSE);
        var answerEntity2 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto2.id(), 4L, Boolean.FALSE);
        var answerEntity3 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto3.id(), 2L, Boolean.TRUE);
        when(answerRepo.findByAssessmentResultIdAndAnswerOptionIdNotNull(assessmentResultEntity.getId()))
            .thenReturn(List.of(answerEntity1, answerEntity2));

        var answerOptionDto1 = createAnswerOptionDto(3L, questionDto1.id(), List.of(createQuestionImpactDto(maturityLevelOfOne, qav1.getQualityAttributeId())));
        var answerOptionDto2 = createAnswerOptionDto(4L, questionDto2.id(), questionDto2.questionImpacts());
        var answerOptionDto3 = createAnswerOptionDto(null, questionDto3.id(), questionDto3.questionImpacts());
        when(answerOptionRestAdapter.loadAnswerOptionByIds(any())).thenReturn(List.of(answerOptionDto1, answerOptionDto2));

        var maturityLevelsDto1 = createMaturityLevelDto(maturityLevelOfOne);
        var maturityLevelsDto2 = createMaturityLevelDto(maturityLevelOfTwo);
        var maturityLevelsDto3 = createMaturityLevelDto(maturityLevelOfThree);
        var maturityLevelsDto4 = createMaturityLevelDto(maturityLevelOfFour);
        var maturityLevelsDto5 = createMaturityLevelDto(maturityLevelOfFive);
        when(maturityLevelRestAdapter.loadMaturityLevelsDtoByKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(maturityLevelsDto1, maturityLevelsDto2, maturityLevelsDto3, maturityLevelsDto4, maturityLevelsDto5));

        var loadedAssessmentResult = adapter.load(assessmentResultEntity.getAssessment().getId());

        assertEquals(assessmentResultEntity.getId(), loadedAssessmentResult.getId());
        assertEquals(assessmentResultEntity.getAssessment().getId(), loadedAssessmentResult.getAssessment().getId());

        var calculatedMaturityLevel = loadedAssessmentResult.calculate();
        System.out.println(calculatedMaturityLevel.getLevel());
    }
}
