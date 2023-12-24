package org.flickit.assessment.core.adapter.out.calculate;

import lombok.AllArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelPersistenceJpaAdapter;
import org.flickit.assessment.core.adapter.out.persistence.kit.qualityattribute.QualityAttributeMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.subject.SubjectMapper;
import org.flickit.assessment.core.adapter.out.rest.answeroption.AnswerOptionDto;
import org.flickit.assessment.core.adapter.out.rest.answeroption.AnswerOptionRestAdapter;
import org.flickit.assessment.core.adapter.out.rest.question.QuestionDto;
import org.flickit.assessment.core.adapter.out.rest.question.QuestionRestAdapter;
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
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJoinAttributeView;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.core.adapter.out.persistence.assessment.AssessmentMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.CALCULATE_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND;

@Component
@AllArgsConstructor
public class AssessmentCalculateInfoLoadAdapter implements LoadCalculateInfoPort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final AnswerJpaRepository answerRepo;
    private final QualityAttributeValueJpaRepository qualityAttrValueRepo;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final SubjectJpaRepository subjectRepository;

    private final QuestionRestAdapter questionRestAdapter;
    private final AnswerOptionRestAdapter answerOptionRestAdapter;
    private final MaturityLevelPersistenceJpaAdapter maturityLevelJpaAdapter;

    record Context(List<QuestionDto> allQuestionsDto,
                   List<AnswerJpaEntity> allAnswerEntities,
                   List<AnswerOptionDto> allAnswerOptionsDto,
                   List<QualityAttributeValueJpaEntity> allQualityAttributeValueEntities,
                   List<SubjectValueJpaEntity> subjectValueEntities,
                   Map<Long, SubjectJpaEntity> subjectIdToEntity,
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
        List<SubjectJoinAttributeView> subjectsWithAttributes = subjectRepository.loadByAssessmentKitId(assessmentKitId);
        Map<Long, SubjectJpaEntity> subjectIdToEntity = subjectsWithAttributes.stream()
            .map(SubjectJoinAttributeView::getSubject)
            .collect(toMap(SubjectJpaEntity::getId, x -> x));
        Map<Long, Integer> qaIdToWeightMap = subjectsWithAttributes.stream()
            .map(SubjectJoinAttributeView::getAttribute)
            .collect(toMap(AttributeJpaEntity::getId, AttributeJpaEntity::getWeight));
        Map<Long, List<SubjectJoinAttributeView>> subjectIdToJoinView = subjectsWithAttributes.stream()
            .collect(groupingBy(x -> x.getSubject().getId()));
        Map<Long, List<QualityAttribute>> subjectIdToAttribute = subjectIdToJoinView.values().stream()
            .collect(toMap(map -> map.stream().findFirst().orElseThrow().getSubject().getId(),
                map -> map.stream().map(SubjectJoinAttributeView::getAttribute).filter(Objects::nonNull).map(QualityAttributeMapper::mapToDomainModel).toList()
            ));

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
            subjectIdToEntity,
            qaIdToWeightMap);

        Map<Long, QualityAttributeValue> qualityAttrIdToValue = buildQualityAttributeValues(context);

        List<SubjectValue> subjectValues = buildSubjectValues(qualityAttrIdToValue, subjectIdToAttribute, context);

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
     *
     * @param qualityAttrIdToValue map of attributeIds to their corresponding value
     * @param subjectIdToAttribute
     * @param context              all previously loaded data
     * @return list of subjectValues
     */
    private static List<SubjectValue> buildSubjectValues(Map<Long, QualityAttributeValue> qualityAttrIdToValue, Map<Long, List<QualityAttribute>> subjectIdToAttribute, Context context) {
        List<SubjectValue> subjectValues = new ArrayList<>();
        for (SubjectValueJpaEntity svEntity : context.subjectValueEntities) {
            SubjectJpaEntity entity = context.subjectIdToEntity.get(svEntity.getSubjectId());
            List<QualityAttribute> attributes = subjectIdToAttribute.get(entity.getId());
            List<QualityAttributeValue> qavList = attributes.stream()
                .map(q -> qualityAttrIdToValue.get(q.getId()))
                .filter(Objects::nonNull)
                .toList();
            if (qavList.isEmpty()) {
                continue;
            }
            subjectValues.add(new SubjectValue(svEntity.getId(), SubjectMapper.mapToDomainModel(entity, attributes), qavList));
        }
        return subjectValues;
    }

    /**
     * @param assessmentEntity loaded assessment entity
     * @return assessment with all information needed for calculation
     */
    private Assessment buildAssessment(AssessmentJpaEntity assessmentEntity) {
        Long kitId = assessmentEntity.getAssessmentKitId();
        List<MaturityLevel> maturityLevels = maturityLevelJpaAdapter.loadByKitIdWithCompetences(kitId);
        AssessmentKit kit = new AssessmentKit(kitId, maturityLevels);
        return mapToDomainModel(assessmentEntity, kit);
    }
}
