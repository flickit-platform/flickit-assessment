package org.flickit.assessment.core.adapter.out.persistence.attributevalue;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.adapter.out.persistence.answer.AnswerMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.answeroption.AnswerOptionMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.attribute.AttributeMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.question.QuestionMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.questionimpact.QuestionImpactMapper;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.Question;
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
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.AttributeImpactfulQuestionsView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

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
            return AttributeValueMapper.mapToDomainModel(q, attributeEntity);
        }).toList();
    }

    @Override
    public AttributeValue load(UUID assessmentResultId, Long attributeId) {
        var attributeValueEntity = repository.findByAttributeIdAndAssessmentResultId(attributeId, assessmentResultId);
        var kitVersionId = attributeValueEntity.getAssessmentResult().getKitVersionId();

        var attributeEntity = attributeRepository.findByIdAndKitVersionId(attributeValueEntity.getAttributeId(), kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND));
        var questions = loadQuestionsByAttributeIdAndKitVersionId(attributeEntity.getId(), kitVersionId);
        var attribute = AttributeMapper.mapToDomainModel(attributeEntity, questions);

        var questionIds = questions.stream()
            .map(Question::getId)
            .toList();
        var answers = loadAnswersByAssessmentResultIdAndQuestionIdIn(attributeValueEntity.getAssessmentResult(), questionIds);

        var maturityLevel = maturityLevelRepository.findByIdAndKitVersionId(attributeValueEntity.getMaturityLevelId(), kitVersionId)
            .map(ml -> MaturityLevelMapper.mapToDomainModel(ml, null))
            .orElseThrow(() -> new ResourceNotFoundException(MATURITY_LEVEL_ID_NOT_FOUND));

        return AttributeValueMapper.mapToDomainModel(attributeValueEntity, attribute, answers, maturityLevel);
    }

    private List<Question> loadQuestionsByAttributeIdAndKitVersionId(Long attributeId, Long kitVersionId) {
        var questionWithImpactViews = questionRepository.findByAttributeIdAndKitVersionId(attributeId, kitVersionId);

        var questionToImpactsMap = questionWithImpactViews.stream()
            .collect(groupingBy(AttributeImpactfulQuestionsView::getQuestion));

        return questionToImpactsMap.entrySet().stream()
            .map(e -> {
                var questionImpacts = e.getValue().stream()
                    .map(v -> QuestionImpactMapper.mapToDomainModel(v.getQuestionImpact()))
                    .toList();
                return QuestionMapper.mapToDomainModel(e.getKey(), questionImpacts);
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
}
