package org.flickit.assessment.core.adapter.out.calculate;

import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelPersistenceJpaAdapter;
import org.flickit.assessment.core.adapter.out.rest.answeroption.AnswerOptionDto;
import org.flickit.assessment.core.adapter.out.rest.answeroption.AnswerOptionRestAdapter;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.test.fixture.adapter.jpa.AssessmentResultJpaEntityMother;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJoinQuestionImpactView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.test.fixture.adapter.dto.AnswerOptionDtoMother.answerOptionDto;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.AnswerJpaEntityMother.*;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.AttributeJapEntityMother.createAttributeEntity;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.AttributeValueJpaEntityMother.attributeValueWithNullMaturityLevel;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.QuestionImpactEntityMother.questionImpactEntity;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.QuestionJpaEntityMother.questionEntity;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.SubjectJpaEntityMother.subjectWithAttributes;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.SubjectValueJpaEntityMother.subjectValueWithNullMaturityLevel;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private SubjectJpaRepository subjectRepository;
    @Mock
    private QuestionJpaRepository questionRepository;
    @Mock
    private AnswerOptionRestAdapter answerOptionRestAdapter;
    @Mock
    private MaturityLevelPersistenceJpaAdapter maturityLevelJpaAdapter;


    @Test
    void testLoad() {
        Context context = createContext();

        doMocks(context);
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        when(maturityLevelJpaAdapter.loadByKitVersionIdWithCompetences(context.assessmentResultEntity.getKitVersionId()))
            .thenReturn(maturityLevels);

        var loadedAssessmentResult = adapter.load(context.assessmentResultEntity().getAssessment().getId());

        assertEquals(context.assessmentResultEntity().getId(), loadedAssessmentResult.getId());

        assertAssessment(context.assessmentResultEntity().getAssessment(), loadedAssessmentResult.getAssessment());

        assertSubjectValues(context.subjectValues, loadedAssessmentResult.getSubjectValues());

        var resultAttributeValues = loadedAssessmentResult.getSubjectValues().stream()
            .flatMap(sv -> sv.getQualityAttributeValues().stream())
            .toList();
        assertAttributeValues(context.qualityAttributeValues, resultAttributeValues);

        List<Answer> answers = new ArrayList<>(resultAttributeValues.stream()
            .flatMap(qav -> qav.getAnswers().stream())
            .collect(toMap(Answer::getId, Function.identity(), (a, b) -> a))
            .values());

        assertAnswers(context.answerEntities, answers);
    }

    private static void assertAssessment(AssessmentJpaEntity assessmentEntity, Assessment resultAssessment) {
        assertEquals(assessmentEntity.getId(), resultAssessment.getId());
        assertEquals(assessmentEntity.getAssessmentKitId(), resultAssessment.getAssessmentKit().getId());
        assertEquals(allLevels().size(), resultAssessment.getAssessmentKit().getMaturityLevels().size());
    }

    private static void assertSubjectValues(List<SubjectValueJpaEntity> subjectValueEntities, List<SubjectValue> resultSubjectValues) {
        subjectValueEntities.forEach(entity ->
            resultSubjectValues.stream()
                .filter(sv -> sv.getId() == entity.getId())
                .findFirst()
                .ifPresentOrElse(
                    sv -> assertEquals(entity.getSubjectId(), sv.getSubject().getId()),
                    Assertions::fail
                )
        );
    }

    private static void assertAttributeValues(List<QualityAttributeValueJpaEntity> attributeValueJpaEntities, List<QualityAttributeValue> resultAttributeValues) {
        attributeValueJpaEntities.forEach(entity ->
            resultAttributeValues.stream()
                .filter(av -> av.getId() == entity.getId())
                .findFirst()
                .ifPresentOrElse(
                    av -> {
                        assertNotNull(av.getQualityAttribute());
                        assertNotNull(av.getQualityAttribute().getQuestions());
                        assertEquals(5, av.getQualityAttribute().getQuestions().size());
                    },
                    Assertions::fail
                )
        );
    }

    private static void assertAnswers(List<AnswerJpaEntity> answerEntities, List<Answer> loadedAnswers) {
        assertEquals(answerEntities.size(), loadedAnswers.size());

        answerEntities.forEach(entity ->
            loadedAnswers.stream()
                .filter(a -> a.getId() == entity.getId())
                .findFirst()
                .ifPresentOrElse(
                    a -> {
                        assertEquals(entity.getQuestionId(), a.getQuestionId());
                        if (entity.getAnswerOptionId() == null)
                            assertNull(a.getSelectedOption());
                        else {
                            assertNotNull(a.getSelectedOption());
                            assertEquals(entity.getAnswerOptionId(), a.getSelectedOption().getId());
                        }
                        assertEquals(entity.getIsNotApplicable(), a.getIsNotApplicable());
                    },
                    Assertions::fail
                )
        );
    }

    private static Context createContext() {
        var assessmentResultEntity = AssessmentResultJpaEntityMother.validSimpleAssessmentResultEntity(null, Boolean.FALSE, Boolean.FALSE);

        var attributeId = 134L;
        var attribute1Id = attributeId++;
        var attribute2Id = attributeId++;
        var attribute3Id = attributeId++;
        var attribute4Id = attributeId++;
        var attribute5Id = attributeId++;
        var attribute6Id = attributeId;

        Long kitId = 123L;

        AttributeJpaEntity attribute1 = createAttributeEntity(attribute1Id, 1, kitId);
        AttributeJpaEntity attribute2 = createAttributeEntity(attribute2Id, 2, kitId);
        AttributeJpaEntity attribute3 = createAttributeEntity(attribute3Id, 3, kitId);
        AttributeJpaEntity attribute4 = createAttributeEntity(attribute4Id, 4, kitId);
        AttributeJpaEntity attribute5 = createAttributeEntity(attribute5Id, 5, kitId);
        AttributeJpaEntity attribute6 = createAttributeEntity(attribute6Id, 6, kitId);

        var qav1 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute1.getRefNum());
        var qav2 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute2.getRefNum());
        var qav3 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute3.getRefNum());
        var qav4 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute4.getRefNum());
        var qav5 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute5.getRefNum());
        var qav6 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute6.getRefNum());
        List<QualityAttributeValueJpaEntity> qualityAttributeValues = List.of(qav1, qav2, qav3, qav4, qav5, qav6);

        var subjectValue1 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        var subjectValue2 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        var subjectValue3 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        List<SubjectValueJpaEntity> subjectValues = List.of(subjectValue1, subjectValue2, subjectValue3);

        var subject1 = subjectWithAttributes(subjectValue1.getSubjectId(), 1, List.of(attribute1, attribute2));
        var subject2 = subjectWithAttributes(subjectValue2.getSubjectId(), 1, List.of(attribute3, attribute4));
        var subject3 = subjectWithAttributes(subjectValue3.getSubjectId(), 1, List.of(attribute5, attribute6));
        List<SubjectJpaEntity> subjects = List.of(subject1, subject2, subject3);

        var question1 = questionEntity(1L, kitId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question2 = questionEntity(2L, kitId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question3 = questionEntity(3L, kitId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question4 = questionEntity(4L, kitId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question5 = questionEntity(5L, kitId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question6 = questionEntity(6L, kitId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question7 = questionEntity(7L, kitId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question8 = questionEntity(8L, kitId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question9 = questionEntity(9L, kitId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question10 = questionEntity(10L, kitId, 1L, Boolean.FALSE, Boolean.TRUE);
        List<QuestionJpaEntity> questions = List.of(question1, question2, question3, question4, question5, question6, question7, question8, question9, question10);

        Map<Long, List<QuestionImpactJpaEntity>> questionIdToImpactsMap = new HashMap<>();
        var impact11 = questionImpactEntity(LEVEL_ONE_ID, question1.getId(), attribute1Id);
        var impact12 = questionImpactEntity(LEVEL_ONE_ID, question1.getId(), attribute2Id);
        var impact13 = questionImpactEntity(LEVEL_ONE_ID, question1.getId(), attribute3Id);
        questionIdToImpactsMap.put(question1.getId(), List.of(impact11, impact12, impact13));

        var impact21 = questionImpactEntity(LEVEL_ONE_ID, question2.getId(), attribute4Id);
        var impact22 = questionImpactEntity(LEVEL_ONE_ID, question2.getId(), attribute5Id);
        var impact23 = questionImpactEntity(LEVEL_ONE_ID, question2.getId(), attribute6Id);
        questionIdToImpactsMap.put(question2.getId(), List.of(impact21, impact22, impact23));

        var impact31 = questionImpactEntity(LEVEL_TWO_ID, question3.getId(), attribute1Id);
        var impact32 = questionImpactEntity(LEVEL_TWO_ID, question3.getId(), attribute2Id);
        var impact33 = questionImpactEntity(LEVEL_TWO_ID, question3.getId(), attribute3Id);
        questionIdToImpactsMap.put(question3.getId(), List.of(impact31, impact32, impact33));

        var impact41 = questionImpactEntity(LEVEL_TWO_ID, question4.getId(), attribute4Id);
        var impact42 = questionImpactEntity(LEVEL_TWO_ID, question4.getId(), attribute5Id);
        var impact43 = questionImpactEntity(LEVEL_TWO_ID, question4.getId(), attribute6Id);
        questionIdToImpactsMap.put(question4.getId(), List.of(impact41, impact42, impact43));

        var impact51 = questionImpactEntity(LEVEL_THREE_ID, question5.getId(), attribute1Id);
        var impact52 = questionImpactEntity(LEVEL_THREE_ID, question5.getId(), attribute2Id);
        var impact53 = questionImpactEntity(LEVEL_THREE_ID, question5.getId(), attribute3Id);
        questionIdToImpactsMap.put(question5.getId(), List.of(impact51, impact52, impact53));

        var impact61 = questionImpactEntity(LEVEL_THREE_ID, question6.getId(), attribute4Id);
        var impact62 = questionImpactEntity(LEVEL_THREE_ID, question6.getId(), attribute5Id);
        var impact63 = questionImpactEntity(LEVEL_THREE_ID, question6.getId(), attribute6Id);
        questionIdToImpactsMap.put(question6.getId(), List.of(impact61, impact62, impact63));

        var impact71 = questionImpactEntity(LEVEL_FOUR_ID, question7.getId(), attribute1Id);
        var impact72 = questionImpactEntity(LEVEL_FOUR_ID, question7.getId(), attribute2Id);
        var impact73 = questionImpactEntity(LEVEL_FOUR_ID, question7.getId(), attribute3Id);
        questionIdToImpactsMap.put(question7.getId(), List.of(impact71, impact72, impact73));

        var impact81 = questionImpactEntity(LEVEL_FOUR_ID, question8.getId(), attribute4Id);
        var impact82 = questionImpactEntity(LEVEL_FOUR_ID, question8.getId(), attribute5Id);
        var impact83 = questionImpactEntity(LEVEL_FOUR_ID, question8.getId(), attribute6Id);
        questionIdToImpactsMap.put(question8.getId(), List.of(impact81, impact82, impact83));

        var impact91 = questionImpactEntity(LEVEL_FIVE_ID, question9.getId(), attribute1Id);
        var impact92 = questionImpactEntity(LEVEL_FIVE_ID, question9.getId(), attribute2Id);
        var impact93 = questionImpactEntity(LEVEL_FIVE_ID, question9.getId(), attribute3Id);
        questionIdToImpactsMap.put(question9.getId(), List.of(impact91, impact92, impact93));

        var impact101 = questionImpactEntity(LEVEL_FIVE_ID, question10.getId(), attribute4Id);
        var impact102 = questionImpactEntity(LEVEL_FIVE_ID, question10.getId(), attribute5Id);
        var impact103 = questionImpactEntity(LEVEL_FIVE_ID, question10.getId(), attribute6Id);
        questionIdToImpactsMap.put(question10.getId(), List.of(impact101, impact102, impact103));


        var answerQ1 = answerEntityWithOption(assessmentResultEntity, question1.getId(), 1L);
        var answerQ2 = answerEntityWithOption(assessmentResultEntity, question2.getId(), 2L);
        // no answer for question 3
        var answerQ4 = answerEntityWithNoOption(assessmentResultEntity, question4.getId());
        var answerQ5 = answerEntityWithOption(assessmentResultEntity, question5.getId(), 5L);
        var answerQ6 = answerEntityWithIsNotApplicableTrue(assessmentResultEntity, question6.getId());
        // no answer question 7, 8
        var answerQ9 = answerEntityWithIsNotApplicableTrue(assessmentResultEntity, question9.getId());
        var answerQ10 = answerEntityWithNoOption(assessmentResultEntity, question10.getId());

        List<AnswerJpaEntity> answerEntities = new ArrayList<>(List.of(answerQ1, answerQ2, answerQ4, answerQ5, answerQ6, answerQ9, answerQ10));

        var answerOptionDto1 = answerOptionDto(1L, question1.getId(), questionIdToImpactsMap.get(question1.getId()));
        var answerOptionDto2 = answerOptionDto(2L, question2.getId(), questionIdToImpactsMap.get(question2.getId()));
        var answerOptionDto3 = answerOptionDto(5L, question5.getId(), questionIdToImpactsMap.get(question5.getId()));
        List<AnswerOptionDto> answerOptionDtos = new ArrayList<>(List.of(answerOptionDto1, answerOptionDto2, answerOptionDto3));

        return new Context(
            assessmentResultEntity,
            subjectValues,
            qualityAttributeValues,
            subjects,
            questions,
            questionIdToImpactsMap,
            answerEntities,
            answerOptionDtos);
    }

    private void doMocks(Context context) {
        when(assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(context.assessmentResultEntity().getAssessment().getId()))
            .thenReturn(Optional.of(context.assessmentResultEntity()));
        when(subjectValueRepo.findByAssessmentResultId(context.assessmentResultEntity().getId()))
            .thenReturn(context.subjectValues());
        when(qualityAttrValueRepo.findByAssessmentResultId(eq(context.assessmentResultEntity().getId())))
            .thenReturn(context.qualityAttributeValues());
        when(subjectRepository.loadByKitVersionIdWithAttributes(context.assessmentResultEntity().getKitVersionId()))
            .thenReturn(context.subjects);
        when(questionRepository.loadByKitVersionId(context.assessmentResultEntity().getKitVersionId()))
            .thenReturn(questionJoinImpactView(context.questionEntities, context.questionIdToImpactsMap));
        when(answerRepo.findByAssessmentResultId(context.assessmentResultEntity().getId()))
            .thenReturn(context.answerEntities());
        when(answerOptionRestAdapter.loadAnswerOptionByIds(any()))
            .thenReturn(context.answerOptionDtos());
    }

    private List<QuestionJoinQuestionImpactView> questionJoinImpactView(List<QuestionJpaEntity> questionEntities, Map<Long, List<QuestionImpactJpaEntity>> questionIdToImpactsMap) {
        Map<Long, QuestionJpaEntity> questionIdToQuestionEntityMap = questionEntities.stream()
            .collect(toMap(QuestionJpaEntity::getId, x -> x));
        List<QuestionImpactJpaEntity> impacts = questionIdToImpactsMap.values().stream().flatMap(Collection::stream).toList();

        return impacts.stream()
            .map(x -> {
                var questionEntity = questionIdToQuestionEntityMap.get(x.getQuestionId());
                QuestionJoinQuestionImpactView view = new QuestionJoinQuestionImpactView() {
                    @Override
                    public QuestionJpaEntity getQuestion() {
                        return questionEntity;
                    }

                    @Override
                    public QuestionImpactJpaEntity getQuestionImpact() {
                        return x;
                    }
                };
                return view;
            }).toList();
    }

    record Context(
        AssessmentResultJpaEntity assessmentResultEntity,
        List<SubjectValueJpaEntity> subjectValues,
        List<QualityAttributeValueJpaEntity> qualityAttributeValues,
        List<SubjectJpaEntity> subjects,
        List<QuestionJpaEntity> questionEntities,
        Map<Long, List<QuestionImpactJpaEntity>> questionIdToImpactsMap,
        List<AnswerJpaEntity> answerEntities,
        List<AnswerOptionDto> answerOptionDtos
    ) {
    }
}
