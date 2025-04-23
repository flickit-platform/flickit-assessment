package org.flickit.assessment.core.adapter.out.persistence.attributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.answer.AnswerMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.answeroption.AnswerOptionMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.attribute.AttributeMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.question.QuestionMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.questionimpact.QuestionImpactMapper;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
import org.flickit.assessment.core.application.port.out.attributevalue.LoadAttributeValuePort;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaRepository;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaRepository;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.AttributeImpactfulQuestionsView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_KIT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.core.adapter.out.persistence.attributevalue.AttributeValueMapper.mapToDomainModel;
import static org.flickit.assessment.core.common.ErrorMessageKey.ATTRIBUTE_ID_NOT_FOUND;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class AttributeValuePersistenceJpaAdapter implements
    CreateAttributeValuePort,
    LoadAttributeValuePort {

    private final AttributeValueJpaRepository repository;
    private final AssessmentResultJpaRepository assessmentResultRepository;
    private final AttributeJpaRepository attributeRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final QuestionJpaRepository questionRepository;
    private final AnswerJpaRepository answerRepository;
    private final AnswerOptionJpaRepository answerOptionRepository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

    @Override
    public List<AttributeValue> persistAll(Set<Long> attributeIds, UUID assessmentResultId) {
        AssessmentResultJpaEntity assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(CREATE_ATTRIBUTE_VALUE_ASSESSMENT_RESULT_ID_NOT_FOUND));

        List<AttributeJpaEntity> attributeEntities = attributeRepository.findAllByIdInAndKitVersionId(attributeIds, assessmentResult.getKitVersionId());
        Map<Long, AttributeJpaEntity> attrIdToAttrEntity = attributeEntities.stream()
            .collect(toMap(AttributeJpaEntity::getId, Function.identity()));

        List<AttributeValueJpaEntity> entities = attributeIds.stream().map(attributeId -> {
            AttributeValueJpaEntity attributeValue = AttributeValueMapper.mapToJpaEntity(attributeId);
            attributeValue.setAssessmentResult(assessmentResult);
            return attributeValue;
        }).toList();

        var persistedEntities = repository.saveAll(entities);

        return persistedEntities.stream().map(q -> {
            AttributeJpaEntity attributeEntity = attrIdToAttrEntity.get(q.getAttributeId());
            return mapToDomainModel(q, attributeEntity);
        }).toList();
    }

    @Override
    public AttributeValue load(UUID assessmentResultId, Long attributeId) {
        return this.load(assessmentResultId, List.of(attributeId)).getFirst();
    }

    @Override
    public List<AttributeValue> load(UUID assessmentResultId, List<Long> attributeIds) {
        var assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var kitVersionId = assessmentResult.getKitVersionId();
        var attributeValueMap = repository
            .findByAssessmentResult_assessment_IdAndAttributeIdIn(assessmentResult.getAssessment().getId(), attributeIds).stream()
            .collect(toMap(AttributeValueJpaEntity::getAttributeId, Function.identity()));
        var attributeEntities = attributeRepository.findAllByIdInAndKitVersionId(attributeIds, kitVersionId);

        if (attributeEntities.size() != attributeIds.size())
            throw new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND);

        var translationLanguage = resolveLanguage(assessmentResult);

        var questions = loadQuestionsByAttributeIdInAndKitVersionId(attributeIds, kitVersionId, translationLanguage);

        var attributeIdToQuestionsMap = attributeEntities.stream()
            .collect(toMap(AttributeJpaEntity::getId, attribute -> questions.stream()
                .filter(q -> q.getImpacts().stream()
                    .anyMatch(i -> Objects.equals(i.getAttributeId(), attribute.getId())))
                .toList()));

        var attributes = attributeEntities.stream()
            .map(entity -> AttributeMapper.mapToDomainWithQuestions(entity, attributeIdToQuestionsMap.get(entity.getId()), translationLanguage))
            .toList();

        var questionIds = questions.stream()
            .map(Question::getId)
            .toList();

        var answers = loadAnswersByAssessmentResultIdAndQuestionIdIn(assessmentResult, questionIds);

        var attributeIdToQuestionIdsMap = attributeIds.stream()
            .collect(toMap(Function.identity(), attributeId -> attributeIdToQuestionsMap.get(attributeId).stream()
                .map(Question::getId)
                .toList()));

        var attributeIdToAnswersMap = attributeIdToQuestionIdsMap.entrySet().stream()
            .collect(toMap(Map.Entry::getKey,
                entrySet -> answers.stream()
                    .filter(answer -> entrySet.getValue().contains(answer.getQuestionId()))
                    .toList()));

        var maturityLevelIds = attributeValueMap.values().stream()
            .map(AttributeValueJpaEntity::getMaturityLevelId)
            .collect(Collectors.toSet());

        var maturityLevelsMap = maturityLevelRepository.findAllByIdInAndKitVersionId(maturityLevelIds, kitVersionId).stream()
            .map(entity -> MaturityLevelMapper.mapToDomainModel(entity, translationLanguage))
            .collect(toMap(MaturityLevel::getId, Function.identity()));

        return attributes.stream()
            .map(attribute -> {
                var attributeValueEntity = attributeValueMap.get(attribute.getId());
                return mapToDomainModel(attributeValueEntity,
                    attribute,
                    attributeIdToAnswersMap.get(attribute.getId()),
                    maturityLevelsMap.get(attributeValueEntity.getMaturityLevelId()));
            })
            .toList();
    }

    @Override
    public List<AttributeValue> loadAll(UUID assessmentResultId) {
        var assessmentResult = assessmentResultRepository.findById(assessmentResultId)
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND));
        var translationLanguage = resolveLanguage(assessmentResult);

        var views = repository.findAllWithAttributeByAssessmentResultId(assessmentResultId);

        var maturityLevelMap = maturityLevelRepository.findAllByKitVersionId(assessmentResult.getKitVersionId()).stream()
            .collect(toMap(MaturityLevelJpaEntity::getId, entity -> MaturityLevelMapper.mapToDomainModel(entity, translationLanguage)));

        return views.stream()
            .map(view -> mapToDomainModel(view.getAttributeValue(),
                AttributeMapper.mapToDomainModel(view.getAttribute(), translationLanguage),
                null,
                maturityLevelMap.get(view.getAttributeValue().getMaturityLevelId())))
            .toList();
    }

    private List<Question> loadQuestionsByAttributeIdInAndKitVersionId(List<Long> attributeIds, Long kitVersionId, KitLanguage translationLanguage) {
        var questionWithImpactViews = questionRepository.findByAttributeIdInAndKitVersionId(attributeIds, kitVersionId);

        var questionToImpactsMap = questionWithImpactViews.stream()
            .collect(groupingBy(AttributeImpactfulQuestionsView::getQuestion));

        return questionToImpactsMap.entrySet().stream()
            .map(e -> {
                var questionImpacts = e.getValue().stream()
                    .map(v -> QuestionImpactMapper.mapToDomainModel(v.getQuestionImpact()))
                    .toList();
                return QuestionMapper.mapToDomainModel(e.getKey(), questionImpacts, translationLanguage);
            })
            .toList();
    }

    private List<Answer> loadAnswersByAssessmentResultIdAndQuestionIdIn(AssessmentResultJpaEntity assessmentResult, List<Long> questionIds) {
        var kitVersionId = assessmentResult.getKitVersionId();
        var answerEntities = answerRepository.findByAssessmentResultIdAndQuestionIdIn(assessmentResult.getId(), questionIds);

        var answerOptionIds = answerEntities.stream()
            .map(AnswerJpaEntity::getAnswerOptionId)
            .toList();
        var answerOptionEntities = answerOptionRepository.findAllByIdInAndKitVersionId(answerOptionIds, kitVersionId);
        var optionIdToOptionEntityMap = answerOptionEntities.stream()
            .collect(toMap(AnswerOptionJpaEntity::getId, Function.identity()));

        return answerEntities.stream()
            .map(answerEntity -> {
                AnswerOption answerOption = null;
                if (answerEntity.getAnswerOptionId() != null) {
                    var answerOptionEntity = optionIdToOptionEntityMap.get(answerEntity.getAnswerOptionId());
                    answerOption = AnswerOptionMapper.mapToDomainModel(answerOptionEntity);
                }
                return AnswerMapper.mapToDomainModel(answerEntity, answerOption);
            })
            .toList();
    }

    private KitLanguage resolveLanguage(AssessmentResultJpaEntity assessmentResult) {
        var kit = assessmentKitRepository.findByKitVersionId(assessmentResult.getKitVersionId())
            .orElseThrow(() -> new ResourceNotFoundException(COMMON_ASSESSMENT_KIT_NOT_FOUND));
        return Objects.equals(assessmentResult.getLangId(), kit.getLanguageId()) ? null
            : KitLanguage.valueOfById(assessmentResult.getLangId());
    }
}
