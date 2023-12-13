package org.flickit.assessment.core.adapter.out.calculate;

import lombok.AllArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.rest.answeroption.AnswerOptionDto;
import org.flickit.assessment.core.adapter.out.rest.answeroption.AnswerOptionRestAdapter;
import org.flickit.assessment.core.adapter.out.rest.maturitylevel.MaturityLevelDto;
import org.flickit.assessment.core.adapter.out.rest.maturitylevel.MaturityLevelRestAdapter;
import org.flickit.assessment.core.adapter.out.rest.qualityattribute.QualityAttributeDto;
import org.flickit.assessment.core.adapter.out.rest.question.QuestionDto;
import org.flickit.assessment.core.adapter.out.rest.question.QuestionRestAdapter;
import org.flickit.assessment.core.adapter.out.rest.subject.SubjectDto;
import org.flickit.assessment.core.adapter.out.rest.subject.SubjectRestAdapter;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaEntity;
import org.flickit.assessment.data.jpa.core.subjectvalue.SubjectValueJpaRepository;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.flickit.assessment.core.adapter.out.persistence.assessment.AssessmentMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.CALCULATE_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@AllArgsConstructor
public class AssessmentCalculateInfoLoadAdapter implements LoadCalculateInfoPort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final AnswerJpaRepository answerRepo;
    private final QualityAttributeValueJpaRepository qualityAttrValueRepo;
    private final SubjectValueJpaRepository subjectValueRepo;

    private final SubjectRestAdapter subjectRestAdapter;
    private final QuestionRestAdapter questionRestAdapter;
    private final AnswerOptionRestAdapter answerOptionRestAdapter;
    private final MaturityLevelRestAdapter maturityLevelRestAdapter;

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
        AssessmentResultJpaEntity assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(CALCULATE_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND));
        UUID assessmentResultId = assessmentResultEntity.getId();
        Long assessmentKitId = assessmentResultEntity.getAssessment().getAssessmentKitId();

        /*
         load all subjectValue and attributeValue entities
         that are already saved with this assessmentResult
         */
        List<SubjectValueJpaEntity> subjectValueEntities = subjectValueRepo.findByAssessmentResultId(assessmentResultId);
        List<QualityAttributeValueJpaEntity> allQualityAttributeValueEntities = qualityAttrValueRepo.findByAssessmentResultId(assessmentResultId);

        /*
        load all subjects and their related attributes (by assessmentKit)
        and create some useful utility maps
        */
        List<SubjectDto> subjectsDto = subjectRestAdapter.loadSubjectsDtoByAssessmentKitId(assessmentKitId);
        Map<Long, Integer> qaIdToWeightMap = subjectsDto.stream()
            .flatMap(x -> x.qualityAttributes().stream())
            .collect(toMap(QualityAttributeDto::id, QualityAttributeDto::weight));
        Map<Long, SubjectDto> subjectIdToDto = subjectsDto.stream()
            .collect(toMap(SubjectDto::id, x -> x));

        // load all questions with their impacts (by assessmentKit)
        List<QuestionDto> allQuestionsDto = questionRestAdapter.loadByAssessmentKitId(assessmentKitId);

        // load all answers submitted with this assessmentResult
        List<AnswerJpaEntity> allAnswerEntities = answerRepo.findByAssessmentResultId(assessmentResultId);

        /*
        based on answers, extract all selected options
        and load all those answerOptions with their impacts
        */
        List<Long> allAnswerOptionIds = allAnswerEntities.stream().map(AnswerJpaEntity::getAnswerOptionId).toList();
        List<AnswerOptionDto> allAnswerOptionsDto = answerOptionRestAdapter.loadAnswerOptionByIds(allAnswerOptionIds);

        Context context = new Context(allQuestionsDto,
            allAnswerEntities,
            allAnswerOptionsDto,
            allQualityAttributeValueEntities,
            subjectValueEntities,
            subjectIdToDto,
            qaIdToWeightMap);

        Map<Long, QualityAttributeValue> qualityAttrIdToValue = buildQualityAttributeValues(context);

        List<SubjectValue> subjectValues = buildSubjectValues(qualityAttrIdToValue, context);

        return new AssessmentResult(
            assessmentResultId,
            buildAssessment(assessmentResultEntity.getAssessment()),
            subjectValues);
    }

    /**
     * build attributeValues domain
     * with all information needed for calculate their maturity levels
     * @param context all previously loaded data
     * @return a map of each attributeId to it's corresponding attributeValue
     */
    private Map<Long, QualityAttributeValue> buildQualityAttributeValues(Context context) {
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

    /**
     * @param qualityAttributeId id of intended attribute to extract its impactful questions
     * @param context all previously loaded data
     * @return list of questions with at least one impact on the given attribute
     */
    private List<Question> questionsWithImpact(Long qualityAttributeId, Context context) {
        return context.allQuestionsDto.stream()
            .filter(q -> q.questionImpacts().stream().anyMatch(f -> f.qualityAttributeId().equals(qualityAttributeId)))
            .map(QuestionDto::dtoToDomain)
            .toList();
    }

    /**
     * @param impactfulQuestions subset of questions extracted in {@linkplain AssessmentCalculateInfoLoadAdapter#questionsWithImpact} method
     * @param context all previously loaded data
     * @return list of answers related to the given list of questions,
     * it is possible that no answer is submitted for any of these questions
     * returning list has {minSize = 0}, {maxSize = size of input questionList}
     */
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
                AnswerOption answerOption = optionDto != null ? optionDto.dtoToDomain() : null;
                return new Answer(
                    entity.getId(),
                    answerOption,
                    entity.getQuestionId(),
                    entity.getConfidenceLevelId(),
                    entity.getIsNotApplicable());
            }).toList();
    }

    /**
     * build subjectValues domain with all information needed for calculate their maturity levels
     * @param qualityAttrIdToValue map of attributeIds to their corresponding value
     * @param context all previously loaded data
     * @return list of subjectValues
     */
    private static List<SubjectValue> buildSubjectValues(Map<Long, QualityAttributeValue> qualityAttrIdToValue, Context context) {
        List<SubjectValue> subjectValues = new ArrayList<>();
        for (SubjectValueJpaEntity svEntity : context.subjectValueEntities) {
            SubjectDto dto = context.subjectIdToDto.get(svEntity.getSubjectId());
            List<QualityAttributeValue> qavList = dto.qualityAttributes().stream()
                .map(q -> qualityAttrIdToValue.get(q.id()))
                .filter(Objects::nonNull)
                .toList();
            if (qavList.isEmpty()) {
                continue;
            }
            subjectValues.add(new SubjectValue(svEntity.getId(), dto.dtoToDomain(), qavList));
        }
        return subjectValues;
    }

    /**
     * @param assessmentEntity loaded assessment entity
     * @return assessment with all information needed for calculation
     */
    private Assessment buildAssessment(AssessmentJpaEntity assessmentEntity) {
        List<MaturityLevelDto> maturityLevelsDto = maturityLevelRestAdapter.loadMaturityLevelsDtoByKitId(assessmentEntity.getAssessmentKitId());
        List<MaturityLevel> maturityLevels = maturityLevelsDto.stream()
            .map(MaturityLevelDto::dtoToDomain)
            .toList();
        AssessmentKit kit = new AssessmentKit(assessmentEntity.getAssessmentKitId(), maturityLevels);
        return mapToDomainModel(assessmentEntity, kit);
    }
}
