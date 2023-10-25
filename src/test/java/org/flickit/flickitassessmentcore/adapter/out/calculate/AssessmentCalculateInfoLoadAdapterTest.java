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
import org.flickit.flickitassessmentcore.application.domain.Answer;
import org.flickit.flickitassessmentcore.application.domain.QualityAttributeValue;
import org.flickit.flickitassessmentcore.application.domain.SubjectValue;
import org.flickit.flickitassessmentcore.test.fixture.adapter.dto.MaturityLevelDtoMother;
import org.flickit.flickitassessmentcore.test.fixture.adapter.jpa.AssessmentResultJpaEntityMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.flickit.flickitassessmentcore.test.fixture.adapter.dto.AnswerOptionDtoMother.createAnswerOptionDto;
import static org.flickit.flickitassessmentcore.test.fixture.adapter.dto.MaturityLevelDtoMother.*;
import static org.flickit.flickitassessmentcore.test.fixture.adapter.dto.QuestionDtoMother.createQuestionDtoWithAffectedLevelAndAttributes;
import static org.flickit.flickitassessmentcore.test.fixture.adapter.dto.SubjectDtoMother.createSubjectDto;
import static org.flickit.flickitassessmentcore.test.fixture.adapter.jpa.AnswerJpaEntityMother.*;
import static org.flickit.flickitassessmentcore.test.fixture.adapter.jpa.AttributeValueJpaEntityMother.attributeValueWithNullMaturityLevel;
import static org.flickit.flickitassessmentcore.test.fixture.adapter.jpa.SubjectValueJpaEntityMother.subjectValueWithNullMaturityLevel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    private static Context createContext() {
        var assessmentResultEntity = AssessmentResultJpaEntityMother.validSimpleAssessmentResultEntity(null, Boolean.FALSE);

        var attributeId = 134L;
        var attribute1Id = attributeId++;
        var attribute2Id = attributeId++;
        var attribute3Id = attributeId++;
        var attribute4Id = attributeId++;
        var attribute5Id = attributeId++;
        var attribute6Id = attributeId;

        var qav1 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute1Id);
        var qav2 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute2Id);
        var qav3 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute3Id);
        var qav4 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute4Id);
        var qav5 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute5Id);
        var qav6 = attributeValueWithNullMaturityLevel(assessmentResultEntity, attribute6Id);
        List<QualityAttributeValueJpaEntity> qualityAttributeValues = List.of(qav1, qav2, qav3, qav4, qav5, qav6);

        var subjectValue1 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        var subjectValue2 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        var subjectValue3 = subjectValueWithNullMaturityLevel(assessmentResultEntity);
        List<SubjectValueJpaEntity> subjectValues = List.of(subjectValue1, subjectValue2, subjectValue3);

        var subjectDto1 = createSubjectDto(subjectValue1.getSubjectId(), List.of(qav1, qav2));
        var subjectDto2 = createSubjectDto(subjectValue2.getSubjectId(), List.of(qav3, qav4));
        var subjectDto3 = createSubjectDto(subjectValue3.getSubjectId(), List.of(qav5, qav6));
        List<SubjectDto> subjectDtos = List.of(subjectDto1, subjectDto2, subjectDto3);

        var questionDto1 = createQuestionDtoWithAffectedLevelAndAttributes(1L, levelOne().id(), attribute1Id, attribute2Id, attribute3Id);
        var questionDto2 = createQuestionDtoWithAffectedLevelAndAttributes(2L, levelOne().id(), attribute4Id, attribute5Id, attribute6Id);
        var questionDto3 = createQuestionDtoWithAffectedLevelAndAttributes(3L, levelTwo().id(), attribute1Id, attribute2Id, attribute3Id);
        var questionDto4 = createQuestionDtoWithAffectedLevelAndAttributes(4L, levelTwo().id(), attribute4Id, attribute5Id, attribute6Id);
        var questionDto5 = createQuestionDtoWithAffectedLevelAndAttributes(5L, levelThree().id(), attribute1Id, attribute2Id, attribute3Id);
        var questionDto6 = createQuestionDtoWithAffectedLevelAndAttributes(6L, levelThree().id(), attribute4Id, attribute5Id, attribute6Id);
        var questionDto7 = createQuestionDtoWithAffectedLevelAndAttributes(7L, levelFour().id(), attribute1Id, attribute2Id, attribute3Id);
        var questionDto8 = createQuestionDtoWithAffectedLevelAndAttributes(8L, levelFour().id(), attribute4Id, attribute5Id, attribute6Id);
        var questionDto9 = createQuestionDtoWithAffectedLevelAndAttributes(9L, levelFive().id(), attribute1Id, attribute2Id, attribute3Id);
        var questionDto10 = createQuestionDtoWithAffectedLevelAndAttributes(10L, levelFive().id(), attribute4Id, attribute5Id, attribute6Id);

        List<QuestionDto> questionDtos = List.of(questionDto1, questionDto2, questionDto3, questionDto4, questionDto5, questionDto6, questionDto7, questionDto8, questionDto9, questionDto10);

        var answerQ1 = answerEntityWithOption(assessmentResultEntity, questionDto1.id(), 1L);
        var answerQ2 = answerEntityWithOption(assessmentResultEntity, questionDto2.id(), 2L);
        // no answer for question 3
        var answerQ4 = answerEntityWithNoOption(assessmentResultEntity, questionDto4.id());
        var answerQ5 = answerEntityWithOption(assessmentResultEntity, questionDto5.id(), 5L);
        var answerQ6 = answerEntityWithIsNotApplicableTrue(assessmentResultEntity, questionDto6.id());
        // no answer question 7, 8
        var answerQ9 = answerEntityWithIsNotApplicableTrue(assessmentResultEntity, questionDto9.id());
        var answerQ10 = answerEntityWithNoOption(assessmentResultEntity, questionDto10.id());

        List<AnswerJpaEntity> answerEntities = new ArrayList<>(List.of(answerQ1, answerQ2, answerQ4, answerQ5, answerQ6, answerQ9, answerQ10));

        var answerOptionDto1 = createAnswerOptionDto(1L, questionDto1);
        var answerOptionDto2 = createAnswerOptionDto(2L, questionDto2);
        var answerOptionDto3 = createAnswerOptionDto(5L, questionDto5);
        List<AnswerOptionDto> answerOptionDtos = new ArrayList<>(List.of(answerOptionDto1, answerOptionDto2, answerOptionDto3));

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
    void testLoad() {
        Context context = createContext();

        doMocks(context);
        List<MaturityLevelDto> maturityLevelsDtos = MaturityLevelDtoMother.allLevels();
        when(maturityLevelRestAdapter.loadMaturityLevelsDtoByKitId(context.assessmentResultEntity.getAssessment().getAssessmentKitId()))
            .thenReturn(maturityLevelsDtos);

        var loadedAssessmentResult = adapter.load(context.assessmentResultEntity().getAssessment().getId());

        assertEquals(context.assessmentResultEntity().getId(), loadedAssessmentResult.getId());

        assertEquals(context.assessmentResultEntity().getAssessment().getId(), loadedAssessmentResult.getAssessment().getId());

        var loadedSubjectValues = loadedAssessmentResult.getSubjectValues().stream()
            .map(SubjectValue::getId)
            .toList();
        assertTrue(context.subjectValues.stream()
            .map(SubjectValueJpaEntity::getId)
            .allMatch(loadedSubjectValues::contains));

        var loadedSubjects = loadedAssessmentResult.getSubjectValues().stream()
            .map(sv -> sv.getSubject().getId())
            .toList();
        assertTrue(context.subjectDtos.stream()
            .map(SubjectDto::id)
            .allMatch(loadedSubjects::contains));

        var loadedQualityAttributeValues = loadedAssessmentResult.getSubjectValues().stream()
            .flatMap(sv -> sv.getQualityAttributeValues().stream())
            .toList();
        assertTrue(context.qualityAttributeValues.stream()
            .map(QualityAttributeValueJpaEntity::getId)
            .allMatch(qav -> loadedQualityAttributeValues.stream()
                .map(QualityAttributeValue::getId)
                .toList()
                .contains(qav)));

        var loadedAnswers = loadedQualityAttributeValues.stream()
            .flatMap(qav -> qav.getAnswers().stream())
            .toList();
        assertTrue(context.answerEntities.stream()
            .map(AnswerJpaEntity::getId)
            .allMatch(a -> loadedAnswers.stream()
                .map(Answer::getId)
                .toList()
                .contains(a)));

        var loadedQuestions = loadedAnswers.stream()
            .map(Answer::getQuestionId)
            .toList();
        assertTrue(context.answerEntities.stream()
            .map(AnswerJpaEntity::getQuestionId)
            .allMatch(loadedQuestions::contains));

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

    record Context(
        AssessmentResultJpaEntity assessmentResultEntity,
        List<SubjectValueJpaEntity> subjectValues,
        List<QualityAttributeValueJpaEntity> qualityAttributeValues,
        List<SubjectDto> subjectDtos,
        List<QuestionDto> questionDtos,
        List<AnswerJpaEntity> answerEntities,
        List<AnswerOptionDto> answerOptionDtos
    ) {
    }
}
