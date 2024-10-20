package org.flickit.assessment.core.adapter.out.calculate;

import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelPersistenceJpaAdapter;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.test.fixture.application.MaturityLevelMother;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.OptionImpactWithQuestionImpactView;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
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
import static org.flickit.assessment.core.test.fixture.adapter.jpa.AnswerJpaEntityMother.*;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.AssessmentResultJpaEntityMother.validSimpleAssessmentResultEntity;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.AttributeJapEntityMother.createAttributeEntity;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.AttributeValueJpaEntityMother.attributeValueWithNullMaturityLevel;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.QuestionImpactEntityMother.questionImpactEntity;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.QuestionJpaEntityMother.questionEntity;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.SubjectJpaEntityMother.subjectWithAttributes;
import static org.flickit.assessment.core.test.fixture.adapter.jpa.SubjectValueJpaEntityMother.subjectValueWithNullMaturityLevel;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private AttributeValueJpaRepository attrValueRepository;
    @Mock
    private SubjectValueJpaRepository subjectValueRepo;
    @Mock
    private SubjectJpaRepository subjectRepository;
    @Mock
    private QuestionJpaRepository questionRepository;
    @Mock
    private AttributeJpaRepository attributeRepository;
    @Mock
    private AnswerOptionJpaRepository answerOptionRepository;
    @Mock
    private AnswerOptionImpactJpaRepository answerOptionImpactRepository;
    @Mock
    private MaturityLevelPersistenceJpaAdapter maturityLevelJpaAdapter;

    @Test
    void testLoad() {
        Context context = createContext();
        long kitVersionId = context.assessmentResultEntity.getKitVersionId();

        doMocks(context, kitVersionId);
        List<MaturityLevel> maturityLevels = MaturityLevelMother.allLevels();
        when(maturityLevelJpaAdapter.loadByKitVersionIdWithCompetences(kitVersionId))
            .thenReturn(maturityLevels);

        var loadedAssessmentResult = adapter.load(context.assessmentResultEntity().getAssessment().getId());

        assertEquals(context.assessmentResultEntity().getId(), loadedAssessmentResult.getId());

        assertAssessment(context.assessmentResultEntity().getAssessment(), loadedAssessmentResult.getAssessment());

        assertSubjectValues(context.subjectValues, loadedAssessmentResult.getSubjectValues());

        var resultAttributeValues = loadedAssessmentResult.getSubjectValues().stream()
            .flatMap(sv -> sv.getAttributeValues().stream())
            .toList();
        assertAttributeValues(context.attributeValues, resultAttributeValues);

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

    private static void assertAttributeValues(List<AttributeValueJpaEntity> attributeValueJpaEntities, List<AttributeValue> resultAttributeValues) {
        attributeValueJpaEntities.forEach(entity ->
            resultAttributeValues.stream()
                .filter(av -> av.getId() == entity.getId())
                .findFirst()
                .ifPresentOrElse(
                    av -> {
                        assertNotNull(av.getAttribute());
                        assertNotNull(av.getAttribute().getQuestions());
                        assertEquals(5, av.getAttribute().getQuestions().size());
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
        var assessmentResultEntity = validSimpleAssessmentResultEntity(null, Boolean.FALSE, Boolean.FALSE);
        Long kitVersionId = assessmentResultEntity.getKitVersionId();

        var attributeId = 134L;
        var attribute1Id = attributeId++;
        var attribute2Id = attributeId++;
        var attribute3Id = attributeId++;
        var attribute4Id = attributeId++;
        var attribute5Id = attributeId++;
        var attribute6Id = attributeId;

        var subjectValue1 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        var subjectValue2 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        var subjectValue3 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        List<SubjectValueJpaEntity> subjectValues = List.of(subjectValue1, subjectValue2, subjectValue3);

        AttributeJpaEntity attribute1 = createAttributeEntity(attribute1Id, 1, kitVersionId, subjectValue1.getSubjectId());
        AttributeJpaEntity attribute2 = createAttributeEntity(attribute2Id, 2, kitVersionId, subjectValue1.getSubjectId());
        AttributeJpaEntity attribute3 = createAttributeEntity(attribute3Id, 3, kitVersionId, subjectValue2.getSubjectId());
        AttributeJpaEntity attribute4 = createAttributeEntity(attribute4Id, 4, kitVersionId, subjectValue2.getSubjectId());
        AttributeJpaEntity attribute5 = createAttributeEntity(attribute5Id, 5, kitVersionId, subjectValue3.getSubjectId());
        AttributeJpaEntity attribute6 = createAttributeEntity(attribute6Id, 6, kitVersionId, subjectValue3.getSubjectId());
        List<AttributeJpaEntity> attributes = List.of(attribute1, attribute2, attribute3, attribute4, attribute5, attribute6);

        var qav1 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute1Id);
        var qav2 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute2Id);
        var qav3 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute3Id);
        var qav4 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute4Id);
        var qav5 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute5Id);
        var qav6 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute6Id);
        List<AttributeValueJpaEntity> attributeValues = List.of(qav1, qav2, qav3, qav4, qav5, qav6);

        var subject1 = subjectWithAttributes(subjectValue1.getSubjectId(), kitVersionId, 1);
        var subject2 = subjectWithAttributes(subjectValue2.getSubjectId(), kitVersionId, 1);
        var subject3 = subjectWithAttributes(subjectValue3.getSubjectId(), kitVersionId, 1);
        List<SubjectJpaEntity> subjects = List.of(subject1, subject2, subject3);

        var question1 = questionEntity(1L, kitVersionId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question2 = questionEntity(2L, kitVersionId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question3 = questionEntity(3L, kitVersionId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question4 = questionEntity(4L, kitVersionId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question5 = questionEntity(5L, kitVersionId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question6 = questionEntity(6L, kitVersionId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question7 = questionEntity(7L, kitVersionId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question8 = questionEntity(8L, kitVersionId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question9 = questionEntity(9L, kitVersionId, 1L, Boolean.FALSE, Boolean.TRUE);
        var question10 = questionEntity(10L, kitVersionId, 1L, Boolean.FALSE, Boolean.TRUE);
        List<QuestionJpaEntity> questions = List.of(question1,
            question2,
            question3,
            question4,
            question5,
            question6,
            question7,
            question8,
            question9,
            question10);

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

        var answerOptionEntity1 = new AnswerOptionJpaEntity(1L, null, null, null, question1.getId(), null, null, null, null);
        var answerOptionEntity2 = new AnswerOptionJpaEntity(2L, null, null, null, question2.getId(), null, null, null, null);
        var answerOptionEntity3 = new AnswerOptionJpaEntity(5L, null, null, null, question5.getId(), null, null, null, null);
        List<AnswerOptionJpaEntity> answerOptionEntities = new ArrayList<>(List.of(answerOptionEntity1, answerOptionEntity2, answerOptionEntity3));

        var answerImpact11 = new AnswerOptionImpactJpaEntity(1L, kitVersionId, 1L, impact11.getId(), 1, null, null, null, null);
        var answerImpact21 = new AnswerOptionImpactJpaEntity(2L, kitVersionId, 2L, impact21.getId(), 1, null, null, null, null);
        var answerImpact31 = new AnswerOptionImpactJpaEntity(3L, kitVersionId, 5L, impact31.getId(), 1, null, null, null, null);

        var optionImpactToQuestionImpactMap = Map.of(answerImpact11, impact11,
            answerImpact21, impact21,
            answerImpact31, impact31);
        List<OptionImpactWithQuestionImpactView> answerOptionImpactEntities = optionImpactWithQuestionImpactView(optionImpactToQuestionImpactMap);

        return new Context(
            assessmentResultEntity,
            subjectValues,
            attributeValues,
            subjects,
            attributes,
            questions,
            questionIdToImpactsMap,
            answerEntities,
            answerOptionEntities,
            answerOptionImpactEntities
        );
    }

    private void doMocks(Context context, long kitVersionId) {
        when(assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(context.assessmentResultEntity().getAssessment().getId()))
            .thenReturn(Optional.of(context.assessmentResultEntity()));
        when(subjectValueRepo.findByAssessmentResultId(context.assessmentResultEntity().getId()))
            .thenReturn(context.subjectValues());
        when(attrValueRepository.findByAssessmentResultId((context.assessmentResultEntity().getId())))
            .thenReturn(context.attributeValues());
        when(subjectRepository.findAllByKitVersionIdOrderByIndex(context.assessmentResultEntity().getKitVersionId()))
            .thenReturn(context.subjects);
        when(questionRepository.loadByKitVersionId(context.assessmentResultEntity().getKitVersionId()))
            .thenReturn(questionJoinImpactView(context.questionEntities, context.questionIdToImpactsMap));
        when(attributeRepository.findAllBySubjectIdInAndKitVersionId(context.subjects.stream().map(SubjectJpaEntity::getId).toList(), kitVersionId))
            .thenReturn(context.attributes);
        when(answerRepo.findByAssessmentResultId(context.assessmentResultEntity().getId()))
            .thenReturn(context.answerEntities());
        when(answerOptionRepository.findAllByIdInAndKitVersionId(any(), anyLong()))
            .thenReturn(context.answerOptionEntities());
        when(answerOptionImpactRepository.findAllByOptionIdInAndKitVersionId(any(), eq(kitVersionId)))
            .thenReturn(context.answerOptionImpactEntities);
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

    private static List<OptionImpactWithQuestionImpactView> optionImpactWithQuestionImpactView(Map<AnswerOptionImpactJpaEntity, QuestionImpactJpaEntity> optionImpactToQuestionImpactMap) {
        return optionImpactToQuestionImpactMap.entrySet().stream()
            .map(x -> {
                OptionImpactWithQuestionImpactView view = new OptionImpactWithQuestionImpactView() {
                    @Override
                    public AnswerOptionImpactJpaEntity getOptionImpact() {
                        return x.getKey();
                    }

                    @Override
                    public QuestionImpactJpaEntity getQuestionImpact() {
                        return x.getValue();
                    }
                };
                return view;
            }).toList();
    }

    record Context(
        AssessmentResultJpaEntity assessmentResultEntity,
        List<SubjectValueJpaEntity> subjectValues,
        List<AttributeValueJpaEntity> attributeValues,
        List<SubjectJpaEntity> subjects,
        List<AttributeJpaEntity> attributes,
        List<QuestionJpaEntity> questionEntities,
        Map<Long, List<QuestionImpactJpaEntity>> questionIdToImpactsMap,
        List<AnswerJpaEntity> answerEntities,
        List<AnswerOptionJpaEntity> answerOptionEntities,
        List<OptionImpactWithQuestionImpactView> answerOptionImpactEntities) {
    }
}
