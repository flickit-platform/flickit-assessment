package org.flickit.assessment.core.adapter.out.calculate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.common.application.domain.kitcustom.KitCustomData;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.kit.answeroption.AnswerOptionMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.answeroptionimpact.AnswerOptionImpactMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.attribute.AttributeMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelPersistenceJpaAdapter;
import org.flickit.assessment.core.adapter.out.persistence.kit.question.QuestionMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.questionimpact.QuestionImpactMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.subject.SubjectMapper;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
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
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.OptionImpactWithQuestionImpactView;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.kitcustom.KitCustomJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJoinQuestionImpactView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.flickit.assessment.core.adapter.out.persistence.assessment.AssessmentMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.CALCULATE_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.KIT_CUSTOM_ID_NOT_FOUND;

@Component
@AllArgsConstructor
public class AssessmentCalculateInfoLoadAdapter implements LoadCalculateInfoPort {

    private final AssessmentResultJpaRepository assessmentResultRepo;
    private final AnswerJpaRepository answerRepo;
    private final AttributeValueJpaRepository attrValueRepository;
    private final SubjectValueJpaRepository subjectValueRepo;
    private final SubjectJpaRepository subjectRepository;
    private final QuestionJpaRepository questionRepository;
    private final AttributeJpaRepository attributeRepository;
    private final AnswerOptionJpaRepository answerOptionRepository;
    private final AnswerOptionImpactJpaRepository answerOptionImpactRepository;
    private final MaturityLevelPersistenceJpaAdapter maturityLevelJpaAdapter;
    private final KitCustomJpaRepository kitCustomRepository;
    private final ObjectMapper objectMapper;

    record Context(List<AnswerJpaEntity> allAnswerEntities,
                   List<AnswerOptionJpaEntity> allAnswerOptionsEntities,
                   Map<Long, List<OptionImpactWithQuestionImpactView>> optionIdToAnswerOptionImpactsMap,
                   List<AttributeValueJpaEntity> allAttributeValueEntities,
                   Map<Long, Map<Long, QuestionWithImpacts>> impactfulQuestions) {
    }

    @Override
    @SneakyThrows
    public AssessmentResult load(UUID assessmentId) {
        AssessmentResultJpaEntity assessmentResultEntity = assessmentResultRepo.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(CALCULATE_ASSESSMENT_ASSESSMENT_RESULT_NOT_FOUND));
        UUID assessmentResultId = assessmentResultEntity.getId();
        long kitVersionId = assessmentResultEntity.getKitVersionId();

        /*
         load all subjectValue and attributeValue entities
         that are already saved with this assessmentResult
         */
        var subjectValueEntities = subjectValueRepo.findByAssessmentResultId(assessmentResultId);
        var allAttributeValueEntities = attrValueRepository.findByAssessmentResultId(assessmentResultId);

        AssessmentJpaEntity assessment = assessmentResultEntity.getAssessment();
        Long kitCustomId = assessment.getKitCustomId();
        KitCustomData kitCustomData = null;
        if (kitCustomId != null) {
            var kitCustomEntity = kitCustomRepository.findByIdAndKitId(kitCustomId, assessment.getAssessmentKitId())
                .orElseThrow(() -> new ResourceNotFoundException(KIT_CUSTOM_ID_NOT_FOUND));
            kitCustomData = objectMapper.readValue(kitCustomEntity.getCustomData(), KitCustomData.class);
        }

        // load all subjects by kitVersionId
        List<SubjectJpaEntity> subjectEntities = subjectRepository.findAllByKitVersionIdOrderByIndex(kitVersionId);
        if (kitCustomData != null && kitCustomData.subjects() != null) {
            Map<Long, Integer> subjectIdToWeight = kitCustomData.subjects().stream()
                .collect(toMap(KitCustomData.Subject::id, KitCustomData.Subject::weight));
            subjectEntities.forEach(e -> {
                if (subjectIdToWeight.containsKey(e.getId()))
                    e.setWeight(subjectIdToWeight.get(e.getId()));
            });
        }

        // load all questions with their impacts (by assessmentKit)
        List<QuestionJoinQuestionImpactView> allQuestionsJoinImpactViews = questionRepository.loadByKitVersionId(kitVersionId);
        Map<Long, Map<Long, QuestionWithImpacts>> impactfulQuestions = mapQuestionToImpacts(allQuestionsJoinImpactViews);

        // load all answers submitted with this assessmentResult
        List<AnswerJpaEntity> allAnswerEntities = answerRepo.findByAssessmentResultId(assessmentResultId);

        /*
        based on answers, extract all selected options
        and load all those answerOptions with their impacts
        */
        var allAnswerOptionIds = allAnswerEntities.stream().map(AnswerJpaEntity::getAnswerOptionId).toList();
        var allAnswerOptionEntities = answerOptionRepository.findAllByIdInAndKitVersionId(allAnswerOptionIds, kitVersionId);
        var optionIdToAnswerOptionImpactEntitiesMap = answerOptionImpactRepository.findAllByOptionIdInAndKitVersionId(allAnswerOptionIds, kitVersionId).stream()
            .collect(groupingBy(e -> e.getOptionImpact().getOptionId()));

        Context context = new Context(
            allAnswerEntities,
            allAnswerOptionEntities,
            optionIdToAnswerOptionImpactEntitiesMap,
            allAttributeValueEntities,
            impactfulQuestions);

        Map<Long, AttributeValue> attributeIdToValue = buildAttributeValues(context,
            subjectEntities,
            kitCustomData,
            kitVersionId);

        List<SubjectValue> subjectValues = buildSubjectValues(attributeIdToValue,
            subjectEntities,
            subjectValueEntities,
            kitCustomData,
            kitVersionId);

        return new AssessmentResult(
            assessmentResultId,
            buildAssessment(assessment, kitVersionId),
            kitVersionId,
            subjectValues,
            assessmentResultEntity.getLastCalculationTime(),
            assessmentResultEntity.getLastConfidenceCalculationTime());
    }

    private Map<Long, Map<Long, QuestionWithImpacts>> mapQuestionToImpacts(List<QuestionJoinQuestionImpactView> questionJoinImpactViews) {
        Map<Long, Map<Long, QuestionWithImpacts>> impactfulQuestionsWithImpact = new HashMap<>();

        for (QuestionJoinQuestionImpactView view : questionJoinImpactViews) {
            QuestionJpaEntity question = view.getQuestion();
            QuestionImpactJpaEntity questionImpact = view.getQuestionImpact();

            if (questionImpact == null)
                continue;

            Long attributeId = questionImpact.getAttributeId();
            Long questionId = question.getId();

            impactfulQuestionsWithImpact.computeIfAbsent(attributeId, k -> new HashMap<>())
                .computeIfAbsent(questionId, k -> new QuestionWithImpacts(question, new ArrayList<>()))
                .add(questionImpact);
        }
        return  impactfulQuestionsWithImpact;
    }

    /**
     * build attributeValues domain
     * with all information needed for calculate their maturity levels
     *
     * @param context      all previously loaded data
     * @param subjectEntities list of all subject entities
     * @param kitVersionId the intended version of kit
     * @return a map of each attributeId to it's corresponding attributeValue
     */
    private Map<Long, AttributeValue> buildAttributeValues(Context context,
                                                           List<SubjectJpaEntity> subjectEntities,
                                                           KitCustomData kitCustomData,
                                                           long kitVersionId) {
        List<Long> subjectIds = subjectEntities.stream().map(SubjectJpaEntity::getId).toList();
        List<AttributeJpaEntity> attributeEntities = attributeRepository.findAllBySubjectIdInAndKitVersionId(subjectIds, kitVersionId);
        if (kitCustomData != null && kitCustomData.attributes() != null) {
            Map<Long, Integer> attributeIdToWeight = kitCustomData.attributes().stream()
                .collect(toMap(KitCustomData.Attribute::id, KitCustomData.Attribute::weight));
            attributeEntities.forEach(e -> {
                if (attributeIdToWeight.containsKey(e.getId()))
                    e.setWeight(attributeIdToWeight.get(e.getId()));
            });
        }

        Map<Long, AttributeJpaEntity> attributeIdToEntityMap = attributeEntities.stream()
            .collect(toMap(AttributeJpaEntity::getId, Function.identity()));

        Map<Long, AttributeValue> attrIdToValue = new HashMap<>();
        for (AttributeValueJpaEntity qavEntity : context.allAttributeValueEntities) {
            Long attributeId = qavEntity.getAttributeId();
            List<Question> impactfulQuestions = questionsWithImpact(context.impactfulQuestions.get(attributeId));
            List<Answer> impactfulAnswers = answersOfImpactfulQuestions(impactfulQuestions, context);
            Attribute attribute = new Attribute(
                attributeId,
                attributeIdToEntityMap.get(attributeId).getTitle(),
                null,
                attributeIdToEntityMap.get(attributeId).getWeight(),
                impactfulQuestions
            );

            AttributeValue attributeValue = new AttributeValue(qavEntity.getId(), attribute, impactfulAnswers);
            attrIdToValue.put(attribute.getId(), attributeValue);
        }
        return attrIdToValue;
    }

    /**
     * @param impactfulQuestions map of impactful questionId to it's impacts
     * @return list of questions with at least one impact on the given attribute
     */
    private List<Question> questionsWithImpact(Map<Long, QuestionWithImpacts> impactfulQuestions) {
        if (impactfulQuestions == null || impactfulQuestions.isEmpty())
            return List.of();

        return impactfulQuestions.values().stream()
            .filter(Objects::nonNull)
            .map(questionWithImpacts -> QuestionMapper.mapToDomainModel(questionWithImpacts.question(),
                questionWithImpacts.impacts().stream()
                    .map(QuestionImpactMapper::mapToDomainModel)
                    .toList()))
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
        Map<Long, AnswerOptionJpaEntity> idToAnswerOptionEntity = context.allAnswerOptionsEntities.stream()
            .collect(toMap(AnswerOptionJpaEntity::getId, x -> x));
        return context.allAnswerEntities.stream()
            .filter(a -> impactfulQuestionIds.contains(a.getQuestionId()))
            .map(entity -> {
                AnswerOptionJpaEntity option = idToAnswerOptionEntity.get(entity.getAnswerOptionId());
                AnswerOption answerOption = null;
                if (option != null) {
                    var impactsEntities = context.optionIdToAnswerOptionImpactsMap.get(option.getId());
                    var optionImpacts = impactsEntities.stream()
                        .map(e -> AnswerOptionImpactMapper.mapToDomainModel(e.getOptionImpact(), e.getQuestionImpact(), option.getValue()))
                        .toList();
                    answerOption = AnswerOptionMapper.mapToDomainModel(option, optionImpacts);
                }
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
     * @param attrIdToValue        map of attributeIds to their corresponding value
     * @param subjectEntities    list of subjects
     * @param subjectValueEntities list of subjectValue entities
     * @param kitVersionId         the intended version of kit
     * @return list of subjectValues
     */
    private List<SubjectValue> buildSubjectValues(Map<Long, AttributeValue> attrIdToValue,
                                                  List<SubjectJpaEntity> subjectEntities,
                                                  List<SubjectValueJpaEntity> subjectValueEntities,
                                                  KitCustomData kitCustomData,
                                                  long kitVersionId) {
        List<SubjectValue> subjectValues = new ArrayList<>();
        Map<Long, SubjectValueJpaEntity> subjectIdToValue = subjectValueEntities.stream()
            .collect(toMap(SubjectValueJpaEntity::getSubjectId, sv -> sv));

        List<Long> subjectIds = subjectEntities.stream().map(SubjectJpaEntity::getId).toList();
        List<AttributeJpaEntity> attributeEntities = attributeRepository.findAllBySubjectIdInAndKitVersionId(subjectIds, kitVersionId);
        if (kitCustomData != null && kitCustomData.attributes() != null) {
            Map<Long, Integer> attributeIdToWeight = kitCustomData.attributes().stream()
                .collect(toMap(KitCustomData.Attribute::id, KitCustomData.Attribute::weight));
            attributeEntities.forEach(e -> {
                if (attributeIdToWeight.containsKey(e.getId()))
                    e.setWeight(attributeIdToWeight.get(e.getId()));
            });
        }
        Map<Long, List<AttributeJpaEntity>> subjectIdToAttrEntities = attributeEntities.stream()
            .collect(Collectors.groupingBy(AttributeJpaEntity::getSubjectId));

        for (SubjectJpaEntity sEntity : subjectEntities) {
            List<Attribute> attributes = List.of();
            if (subjectIdToAttrEntities.get(sEntity.getId()) != null) {
                attributes = subjectIdToAttrEntities.get(sEntity.getId()).stream()
                    .map(AttributeMapper::mapToDomainModel).toList();
            }
            List<AttributeValue> qavList = attributes.stream()
                .map(q -> attrIdToValue.get(q.getId()))
                .filter(Objects::nonNull)
                .toList();
            if (qavList.isEmpty())
                continue;
            SubjectValueJpaEntity svEntity = subjectIdToValue.get(sEntity.getId());
            subjectValues.add(new SubjectValue(svEntity.getId(), SubjectMapper.mapToDomainModel(sEntity, attributes), qavList));
        }
        return subjectValues;
    }

    /**
     * @param assessmentEntity loaded assessment entity
     * @return assessment with all information needed for calculation
     */
    private Assessment buildAssessment(AssessmentJpaEntity assessmentEntity, long kitVersionId) {
        Long kitId = assessmentEntity.getAssessmentKitId();
        List<MaturityLevel> maturityLevels = maturityLevelJpaAdapter.loadByKitVersionIdWithCompetences(kitVersionId);
        AssessmentKit kit = new AssessmentKit(kitId, null, kitVersionId, maturityLevels);
        return mapToDomainModel(assessmentEntity, kit, null);
    }

    private record QuestionWithImpacts(QuestionJpaEntity question, List<QuestionImpactJpaEntity> impacts) {

        void add(QuestionImpactJpaEntity impact) {
            impacts.add(impact);
        }
    }
}
