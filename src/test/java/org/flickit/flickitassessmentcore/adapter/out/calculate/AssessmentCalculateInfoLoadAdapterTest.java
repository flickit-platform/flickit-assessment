package org.flickit.flickitassessmentcore.adapter.out.calculate;

import org.flickit.flickitassessmentcore.adapter.out.persistence.answer.AnswerJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.answer.AnswerJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue.SubjectValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue.SubjectValueJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.rest.answeroption.AnswerOptionDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.answeroption.AnswerOptionRestAdapter;
import org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel.MaturityLevelDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel.MaturityLevelRestAdapter;
import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionRestAdapter;
import org.flickit.flickitassessmentcore.adapter.out.rest.subject.SubjectDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.subject.SubjectRestAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.flickit.flickitassessmentcore.adapter.out.calculate.AssessmentCalculateInfoCreator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssessmentCalculateInfoLoadAdapterTest {

    private static final Long MATURITY_LEVEL_OF_ONE = 1L;
    private static final Long MATURITY_LEVEL_OF_TWO = 2L;
    private static final Long MATURITY_LEVEL_OF_THREE = 3L;
    private static final Long MATURITY_LEVEL_OF_FOUR = 4L;
    private static final Long MATURITY_LEVEL_OF_FIVE = 5L;
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

    private static Context createContext(Boolean hasNotApplicableAnswer) {
        var assessmentResultEntity = validSimpleAssessmentResultEntity(null, Boolean.FALSE);

        var subjectValue1 = subjectValueWithMaturityLevel(assessmentResultEntity, null);
        var subjectValue2 = subjectValueWithMaturityLevel(assessmentResultEntity, null);
        var subjectValue3 = subjectValueWithMaturityLevel(assessmentResultEntity, null);
        List<SubjectValueJpaEntity> subjectValues = List.of(subjectValue1, subjectValue2, subjectValue3);

        var qav1 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav2 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav3 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav4 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav5 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        var qav6 = qualityAttributeWithMaturityLevel(assessmentResultEntity, null);
        List<QualityAttributeValueJpaEntity> qualityAttributeValues = List.of(qav1, qav2, qav3, qav4, qav5, qav6);

        var subjectDto1 = createSubjectDto(subjectValue1.getSubjectId(), List.of(qav1, qav2));
        var subjectDto2 = createSubjectDto(subjectValue2.getSubjectId(), List.of(qav3, qav4));
        var subjectDto3 = createSubjectDto(subjectValue3.getSubjectId(), List.of(qav5, qav6));
        List<SubjectDto> subjectDtos = List.of(subjectDto1, subjectDto2, subjectDto3);

        var questionDto1 = createQuestionDto(1L, MATURITY_LEVEL_OF_TWO, qav1.getQualityAttributeId(), qav2.getQualityAttributeId());
        var questionDto2 = createQuestionDto(2L, MATURITY_LEVEL_OF_ONE, qav3.getQualityAttributeId(), qav4.getQualityAttributeId());
        var questionDto3 = createQuestionDto(3L, MATURITY_LEVEL_OF_FIVE, qav5.getQualityAttributeId(), qav6.getQualityAttributeId());
        List<QuestionDto> questionDtos = List.of(questionDto1, questionDto2, questionDto3);

        var answerEntity1 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto1.id(), 3L, Boolean.FALSE);
        var answerEntity2 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto2.id(), 4L, Boolean.FALSE);
        List<AnswerJpaEntity> answerEntities = new ArrayList<>(List.of(answerEntity1, answerEntity2));

        var answerOptionDto1 = createAnswerOptionDto(3L, questionDto1.id(), List.of(createQuestionImpactDto(MATURITY_LEVEL_OF_ONE, qav1.getQualityAttributeId())));
        var answerOptionDto2 = createAnswerOptionDto(4L, questionDto2.id(), questionDto2.questionImpacts());
        List<AnswerOptionDto> answerOptionDtos = new ArrayList<>(List.of(answerOptionDto1, answerOptionDto2));


        if (!hasNotApplicableAnswer) {
            var answerEntity3 = answerEntityWithAnswerOptionAndIsNotApplicable(assessmentResultEntity, questionDto3.id(), 2L, Boolean.FALSE);
            answerEntities.add(answerEntity3);

            var answerOptionDto3 = createAnswerOptionDto(2L, questionDto3.id(), List.of(createQuestionImpactDto(MATURITY_LEVEL_OF_ONE, qav1.getQualityAttributeId())));
            answerOptionDtos.add(answerOptionDto3);
        }


        return new Context(
            assessmentResultEntity,
            subjectValues,
            qualityAttributeValues,
            subjectDtos,
            questionDtos,
            answerEntities,
            answerOptionDtos);
    }

    @Test
    void testAssessmentCalculateInfoLoad_ValidInputWithThreeQuestions_ValidResults() {
        Context context = createContext(Boolean.FALSE);

        doMocks(context);
        mockMaturityLevelLoad(context.assessmentResultEntity());

        var loadedAssessmentResult = adapter.load(context.assessmentResultEntity().getAssessment().getId());

        assertEquals(context.assessmentResultEntity().getId(), loadedAssessmentResult.getId());
        assertEquals(context.assessmentResultEntity().getAssessment().getId(), loadedAssessmentResult.getAssessment().getId());
        assertEquals(context.subjectValues().size(), loadedAssessmentResult.getSubjectValues().size());

        var calculatedMaturityLevel = loadedAssessmentResult.calculate();
        assertEquals(4, calculatedMaturityLevel.getId());
    }

    @Test
    void testAssessmentCalculateInfoLoad_ValidInputWithOneNotApplicableQuestion_ValidResults() {
        Context context = createContext(Boolean.TRUE);

        doMocks(context);
        mockMaturityLevelLoad(context.assessmentResultEntity());

        var loadedAssessmentResult = adapter.load(context.assessmentResultEntity().getAssessment().getId());

        assertEquals(context.assessmentResultEntity().getId(), loadedAssessmentResult.getId());
        assertEquals(context.assessmentResultEntity().getAssessment().getId(), loadedAssessmentResult.getAssessment().getId());
//        assertEquals(context.subjectValues().size() - 1, loadedAssessmentResult.getSubjectValues().size());

        var calculatedMaturityLevel = loadedAssessmentResult.calculate();
        assertEquals(4, calculatedMaturityLevel.getId());
    }

    private void doMocks(Context context) {
        when(assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(context.assessmentResultEntity().getAssessment().getId()))
            .thenReturn(Optional.of(context.assessmentResultEntity()));
        when(subjectValueRepo.findByAssessmentResultId(context.assessmentResultEntity().getId()))
            .thenReturn(context.subjectValues());
        when(qualityAttrValueRepo.findByAssessmentResultId(context.assessmentResultEntity().getId()))
            .thenReturn(context.qualityAttributeValues());
        when(subjectRestAdapter.loadSubjectsDtoByAssessmentKitId(context.assessmentResultEntity().getAssessment().getAssessmentKitId()))
            .thenReturn(context.subjectDtos());
        when(questionRestAdapter.loadByAssessmentKitId(context.assessmentResultEntity().getAssessment().getAssessmentKitId()))
            .thenReturn(context.questionDtos());
        when(answerRepo.findByAssessmentResultId(context.assessmentResultEntity().getId()))
            .thenReturn(context.answerEntities());
        when(answerOptionRestAdapter.loadAnswerOptionByIds(any()))
            .thenReturn(context.answerOptionDtos());
    }

    private void mockMaturityLevelLoad(AssessmentResultJpaEntity assessmentResultEntity) {
        var maturityLevelsDto1 = createMaturityLevelDto(MATURITY_LEVEL_OF_ONE);
        var maturityLevelsDto2 = createMaturityLevelDto(MATURITY_LEVEL_OF_TWO);
        var maturityLevelsDto3 = createMaturityLevelDto(MATURITY_LEVEL_OF_THREE);
        var maturityLevelsDto4 = createMaturityLevelDto(MATURITY_LEVEL_OF_FOUR);
        var maturityLevelsDto5 = createMaturityLevelDto(MATURITY_LEVEL_OF_FIVE);
        List<MaturityLevelDto> maturityLevelsDtos = List.of(maturityLevelsDto1, maturityLevelsDto2, maturityLevelsDto3, maturityLevelsDto4, maturityLevelsDto5);
        when(maturityLevelRestAdapter.loadMaturityLevelsDtoByKitId(assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(maturityLevelsDtos);
    }
}
