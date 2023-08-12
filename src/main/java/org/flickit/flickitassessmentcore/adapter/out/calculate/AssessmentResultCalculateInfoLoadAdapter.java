package org.flickit.flickitassessmentcore.adapter.out.calculate;

import lombok.AllArgsConstructor;
import org.flickit.flickitassessmentcore.adapter.out.persistence.answer.AnswerJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.answer.AnswerJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.qualityattributevalue.QualityAttributeValueJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue.SubjectValueJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue.SubjectValueJpaRepository;
import org.flickit.flickitassessmentcore.adapter.out.rest.answeroption.AnswerOptionAdapter;
import org.flickit.flickitassessmentcore.adapter.out.rest.answeroption.AnswerOptionDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel.MaturityLevelDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.maturitylevel.MaturityLevelRestAdapter;
import org.flickit.flickitassessmentcore.adapter.out.rest.qualityattribute.QualityAttributeDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.question.QuestionRestAdapter;
import org.flickit.flickitassessmentcore.adapter.out.rest.subject.SubjectDto;
import org.flickit.flickitassessmentcore.adapter.out.rest.subject.SubjectRestAdapter;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.flickitassessmentcore.domain.*;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentMapper.mapToDomainModel;

@Component
@AllArgsConstructor
public class AssessmentResultCalculateInfoLoadAdapter implements LoadCalculateInfoPort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final AnswerJpaRepository answerRepo;
    private final QualityAttributeValueJpaRepository qualityAttrValueRepo;
    private final SubjectValueJpaRepository subjectValueRepo;

    private final SubjectRestAdapter subjectRestAdapter;
    private final QuestionRestAdapter questionRestAdapter;
    private final AnswerOptionAdapter answerOptionAdapter;
    private final MaturityLevelRestAdapter maturityLevelAdapter;

    record Context(List<QuestionDto> allQuestionsDto,
                   List<AnswerJpaEntity> allAnswerEntities,
                   List<AnswerOptionDto> allAnswerOptionsDto,
                   List<QualityAttributeValueJpaEntity> allQualityAttributeValueEntities,
                   List<SubjectValueJpaEntity> subjectValueEntities,
                   Map<Long, SubjectDto> subjectIdToDto,
                   Map<Long, Integer> qaIdToWeightMap) {
    }

    @Override
    public AssessmentResult load(UUID assessmentId) {
        AssessmentResultJpaEntity assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId);
        Long assessmentKitId = assessmentResultEntity.getAssessment().getAssessmentKitId();

        // list qualityAttrVal and subjectVal(by assessmentResultId)
        List<QualityAttributeValueJpaEntity> allQualityAttributeValueEntities = qualityAttrValueRepo.findByAssessmentResultId(assessmentResultEntity.getId());
        List<SubjectValueJpaEntity> subjectValueEntities = subjectValueRepo.findByAssessmentResultId(assessmentResultEntity.getId());

        // list subject and quality attr (by assessmentKit)
        List<SubjectDto> subjectsDto = subjectRestAdapter.loadSubjectsDtoByAssessmentKitId(assessmentKitId);
        Map<Long, Integer> qaIdToWeightMap = subjectsDto.stream()
            .flatMap(x -> x.qualityAttributes().stream())
            .collect(toMap(QualityAttributeDto::id, QualityAttributeDto::weight));
        Map<Long, SubjectDto> subjectIdToDto = subjectsDto.stream()
            .collect(toMap(SubjectDto::id, x -> x));

        // list questions (with impact) by assessmentKit
        List<QuestionDto> allQuestionsDto = questionRestAdapter.loadByAssessmentKitId(assessmentKitId);

        // list all answers
        List<AnswerJpaEntity> allAnswerEntities = answerRepo.findByAssessmentResultId(assessmentResultEntity.getId());

        // list answerOptions (with impact) by answerOptionIds
        List<Long> allAnswerOptionIds = allAnswerEntities.stream().map(AnswerJpaEntity::getAnswerOptionId).toList();
        List<AnswerOptionDto> allAnswerOptionsDto = answerOptionAdapter.loadAnswerOptionByIds(allAnswerOptionIds);

        Context context = new Context(allQuestionsDto,
            allAnswerEntities,
            allAnswerOptionsDto,
            allQualityAttributeValueEntities,
            subjectValueEntities,
            subjectIdToDto,
            qaIdToWeightMap);

        // create QualityAttrId -> QualityAttrValues domain
        Map<Long, QualityAttributeValue> qualityAttrIdToValue = createQualityAttributeValues(context);

        // create SubjectValues domain
        List<SubjectValue> subjectValues = createSubjectValues(qualityAttrIdToValue, context);

        return new AssessmentResult(
            assessmentResultEntity.getId(),
            createAssessment(assessmentResultEntity.getAssessment()),
            subjectValues);
    }

    private Map<Long, QualityAttributeValue> createQualityAttributeValues(Context context) {
        Map<Long, QualityAttributeValue> qualityAttrIdToValue = new HashMap<>();
        for (QualityAttributeValueJpaEntity qavEntity : context.allQualityAttributeValueEntities) {
            List<Question> impactfulQuestions = questionsWithImpact(qavEntity.getQualityAttributeId(), context);
            List<Answer> impactfulAnswers = answersOfImpactfulQuestions(impactfulQuestions, context);
            QualityAttribute qualityAttribute = new QualityAttribute(
                qavEntity.getQualityAttributeId(),
                context.qaIdToWeightMap.get(qavEntity.getQualityAttributeId()),
                impactfulQuestions
            );

            QualityAttributeValue qualityAttributeValue = new QualityAttributeValue(qavEntity.getId(), qualityAttribute, impactfulAnswers);

            qualityAttrIdToValue.put(qualityAttribute.getId(), qualityAttributeValue);
        }
        return qualityAttrIdToValue;
    }

    private List<Question> questionsWithImpact(Long qualityAttributeId, Context context) {
        return context.allQuestionsDto.stream()
            .filter(q -> q.questionImpacts().stream().anyMatch(f -> f.qualityAttributeId().equals(qualityAttributeId)))
            .map(QuestionDto::dtoToDomain)
            .toList();
    }

    private List<Answer> answersOfImpactfulQuestions(List<Question> impactfulQuestions, Context context) {
        Set<Long> impactfulQuestionIds = impactfulQuestions.stream()
            .map(Question::getId)
            .collect(toSet());
        Map<Long, AnswerOptionDto> idToAnswerOptionDto = context.allAnswerOptionsDto.stream()
            .collect(toMap(AnswerOptionDto::id, x -> x));
        return context.allAnswerEntities.stream()
            .filter(a -> impactfulQuestionIds.contains(a.getQuestionId()))
            .map(entity -> {
                AnswerOptionDto optionDto = idToAnswerOptionDto.get(entity.getAnswerOptionId());
                return new Answer(entity.getId(), optionDto.dtoToDomain());
            }).toList();
    }

    private static List<SubjectValue> createSubjectValues(Map<Long, QualityAttributeValue> qualityAttrIdToValue, Context context) {
        List<SubjectValue> subjectValues = new ArrayList<>();
        for (SubjectValueJpaEntity svEntity : context.subjectValueEntities) {
            SubjectDto dto = context.subjectIdToDto.get(svEntity.getSubjectId());
            List<QualityAttributeValue> qavList = dto.qualityAttributes().stream()
                .map(q -> qualityAttrIdToValue.get(q.id()))
                .toList();
            subjectValues.add(new SubjectValue(svEntity.getId(), qavList));
        }
        return subjectValues;
    }

    private Assessment createAssessment(AssessmentJpaEntity assessmentEntity) {
        List<MaturityLevelDto> maturityLevelsDto = maturityLevelAdapter.loadByKitId(assessmentEntity.getAssessmentKitId());
        List<MaturityLevel> maturityLevels = maturityLevelsDto.stream()
            .map(MaturityLevelDto::dtoToDomain)
            .toList();
        AssessmentKit kit = new AssessmentKit(assessmentEntity.getAssessmentKitId(), maturityLevels);
        return mapToDomainModel(assessmentEntity, kit);
    }
}
