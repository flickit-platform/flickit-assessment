package org.flickit.flickitassessmentcore.adapter.out.calculate;

import org.flickit.flickitassessmentcore.adapter.out.persistence.answer.AnswerJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaRepository;
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
import java.util.UUID;

import static org.flickit.flickitassessmentcore.adapter.out.calculate.AssessmentCalculateInfoCreator.*;
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
    void testAssessmentCalculateInfoLoad_ValidInput_ValidResults() {
        Long maturityLevelOfOne = 1L;
        Long maturityLevelOfTwo = 2L;
        Long maturityLevelOfThree = 3L;
        Long maturityLevelOfFour = 4L;
        Long maturityLevelOfFive = 5L;
        var assessmentResultEntity = validSimpleAssessmentResultEntity(maturityLevelOfFive);
        UUID assessmentId = assessmentResultEntity.getAssessment().getId();
        when(assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId))
            .thenReturn(Optional.of(assessmentResultEntity));

        var subjectValue = subjectValueWithMaturityLevel(assessmentResultEntity, maturityLevelOfFive);
        when(subjectValueRepo.findByAssessmentResultId(assessmentResultEntity.getId())).thenReturn(List.of(subjectValue));

        var qav1 = qualityAttributeWithMaturityLevel(assessmentResultEntity, maturityLevelOfOne);
        var qav2 = qualityAttributeWithMaturityLevel(assessmentResultEntity, maturityLevelOfTwo);
        var qav3 = qualityAttributeWithMaturityLevel(assessmentResultEntity, maturityLevelOfThree);
        var qav4 = qualityAttributeWithMaturityLevel(assessmentResultEntity, maturityLevelOfFour);
        var qav5 = qualityAttributeWithMaturityLevel(assessmentResultEntity, maturityLevelOfFive);
        List<QualityAttributeValueJpaEntity> qualityAttributeValues = List.of(qav1, qav2, qav3, qav4, qav5);
        when(qualityAttrValueRepo.findByAssessmentResultId(assessmentResultEntity.getId())).thenReturn(qualityAttributeValues);

        var subjectDto = createSubjectDto(subjectValue.getSubjectId(), qualityAttributeValues);
        when(subjectRestAdapter.loadSubjectsDtoByAssessmentKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(subjectDto));

        var questionDto1 = createQuestionDto(1L);
        var questionDto2 = createQuestionDto(2L);
        when(questionRestAdapter.loadByAssessmentKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(questionDto1, questionDto2));

        var answerEntity1 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto1.id(), 3L, Boolean.FALSE);
        var answerEntity2 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto2.id(), 4L, Boolean.FALSE);
        when(answerRepo.findByAssessmentResultIdAndAnswerOptionIdNotNull(assessmentResultEntity.getId()))
            .thenReturn(List.of(answerEntity1, answerEntity2));

        var answerOptionDto1 = createAnswerOptionDto(3L, questionDto1.id(), questionDto1.questionImpacts().get(0));
        var answerOptionDto2 = createAnswerOptionDto(4L, questionDto2.id(), questionDto2.questionImpacts().get(0));
        when(answerOptionRestAdapter.loadAnswerOptionByIds(any())).thenReturn(List.of(answerOptionDto1, answerOptionDto2));

        var maturityLevelsDto1 = createMaturityLevelDto(maturityLevelOfOne);
        var maturityLevelsDto2 = createMaturityLevelDto(maturityLevelOfTwo);
        var maturityLevelsDto3 = createMaturityLevelDto(maturityLevelOfThree);
        var maturityLevelsDto4 = createMaturityLevelDto(maturityLevelOfFour);
        var maturityLevelsDto5 = createMaturityLevelDto(maturityLevelOfFive);
        when(maturityLevelRestAdapter.loadMaturityLevelsDtoByKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(List.of(maturityLevelsDto1, maturityLevelsDto2, maturityLevelsDto3, maturityLevelsDto4, maturityLevelsDto5));

        var loadedAssessmentResult = adapter.load(assessmentId);
    }
}
