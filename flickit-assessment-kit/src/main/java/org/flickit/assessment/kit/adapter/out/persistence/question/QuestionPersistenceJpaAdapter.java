package org.flickit.assessment.kit.adapter.out.persistence.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.AttributeLevelImpactfulQuestionsView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.seq.KitDbSequenceGenerators;
import org.flickit.assessment.kit.adapter.out.persistence.answeroption.AnswerOptionMapper;
import org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact.AnswerOptionImpactMapper;
import org.flickit.assessment.kit.adapter.out.persistence.questionimpact.QuestionImpactMapper;
import org.flickit.assessment.kit.application.domain.AnswerOptionImpact;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.QuestionImpact;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.out.question.*;
import org.flickit.assessment.kit.application.port.out.subject.CountSubjectQuestionsPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.flickit.assessment.kit.adapter.out.persistence.question.QuestionMapper.mapToJpaEntity;
import static org.flickit.assessment.kit.adapter.out.persistence.questionnaire.QuestionnaireMapper.mapToDomainModel;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@Component
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    UpdateQuestionPort,
    CreateQuestionPort,
    CountSubjectQuestionsPort,
    LoadQuestionPort,
    LoadAttributeLevelQuestionsPort,
    DeleteQuestionPort,
    LoadQuestionnaireQuestionsPort {

    private final QuestionJpaRepository repository;
    private final QuestionImpactJpaRepository questionImpactRepository;
    private final AnswerOptionImpactJpaRepository answerOptionImpactRepository;
    private final AnswerOptionJpaRepository answerOptionRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final AttributeJpaRepository attributeRepository;
    private final KitDbSequenceGenerators sequenceGenerators;

    @Override
    public void update(UpdateQuestionPort.Param param) {
        if (!repository.existsByIdAndKitVersionId(param.id(), param.kitVersionId()))
            throw new ResourceNotFoundException(QUESTION_ID_NOT_FOUND);

        repository.update(param.id(),
            param.kitVersionId(),
            param.title(),
            param.code(),
            param.index(),
            param.hint(),
            param.mayNotBeApplicable(),
            param.advisable(),
            param.answerRangeId(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public Long persist(CreateQuestionPort.Param param) {
        var entity = mapToJpaEntity(param);
        entity.setId(sequenceGenerators.generateQuestionId());
        return repository.save(entity).getId();
    }

    @Override
    public int countBySubjectId(long subjectId, long kitVersionId) {
        return repository.countDistinctBySubjectId(subjectId, kitVersionId);
    }

    @Override
    public Question load(long id, long kitVersionId) {
        var questionEntity = repository.findByIdAndKitVersionId(id, kitVersionId)
            .orElseThrow(() -> new ResourceNotFoundException(QUESTION_ID_NOT_FOUND));
        Question question = QuestionMapper.mapToDomainModel(questionEntity);
        if (question.getAnswerRangeId() == null)
            return question;

        var optionEntities = answerOptionRepository.findAllByAnswerRangeIdAndKitVersionIdOrderByIndex(questionEntity.getAnswerRangeId(), kitVersionId);
        var options = optionEntities.stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList();

        var impacts = questionImpactRepository.findAllByQuestionIdAndKitVersionId(id, kitVersionId).stream()
            .map(QuestionImpactMapper::mapToDomainModel)
            .map(impact -> setOptionImpacts(impact, optionEntities))
            .toList();

        question.setImpacts(impacts);
        question.setOptions(options);
        return question;
    }

    private QuestionImpact setOptionImpacts(QuestionImpact impact, List<AnswerOptionJpaEntity> optionEntities) {
        var optionImpactsEntities = answerOptionImpactRepository.findAllByQuestionImpactIdAndKitVersionId(impact.getId(), impact.getKitVersionId());
        var optionIdToOptionValueMap = optionEntities.stream()
            .collect(toMap(AnswerOptionJpaEntity::getId, AnswerOptionJpaEntity::getValue));
        var optionImpacts = optionImpactsEntities.stream()
            .map(entity -> AnswerOptionImpactMapper.mapToDomainModel(entity, optionIdToOptionValueMap.get(entity.getOptionId())))
            .toList();
        impact.setOptionImpacts(optionImpacts);
        return impact;
    }

    @Override
    public List<LoadAttributeLevelQuestionsPort.Result> loadAttributeLevelQuestions(long kitVersionId, long attributeId, long maturityLevelId) {
        if (!attributeRepository.existsByIdAndKitVersionId(attributeId, kitVersionId))
            throw new ResourceNotFoundException(ATTRIBUTE_ID_NOT_FOUND);

        if (!maturityLevelRepository.existsByIdAndKitVersionId(maturityLevelId, kitVersionId))
            throw new ResourceNotFoundException(MATURITY_LEVEL_ID_NOT_FOUND);
        var views = repository.findByAttributeIdAndMaturityLevelIdAndKitVersionId(attributeId, maturityLevelId, kitVersionId);

        Map<QuestionJpaEntity, List<AttributeLevelImpactfulQuestionsView>> myMap = views.stream()
            .collect(Collectors.groupingBy(AttributeLevelImpactfulQuestionsView::getQuestion));

        return myMap.entrySet().stream()
            .map(entry -> {
                Question question = QuestionMapper.mapToDomainModel(entry.getKey());
                Questionnaire questionnaire = mapToDomainModel(entry.getValue().getFirst().getQuestionnaire());

                var answerOptionEntities = entry.getValue().stream()
                    .collect(toMap(e -> e.getAnswerOption().getId(), AttributeLevelImpactfulQuestionsView::getAnswerOption,
                        (existing, replacement) -> existing))
                    .values();
                var optionIdToOptionValueMap = answerOptionEntities.stream()
                    .collect(toMap(AnswerOptionJpaEntity::getId, AnswerOptionJpaEntity::getValue));
                var options = answerOptionEntities.stream().map(AnswerOptionMapper::mapToDomainModel).toList();

                QuestionImpact impact = QuestionImpactMapper.mapToDomainModel(entry.getValue().getFirst().getQuestionImpact());
                Map<Long, AnswerOptionImpactJpaEntity> optionMap = entry.getValue().stream()
                    .collect(toMap(e -> e.getOptionImpact().getId(), AttributeLevelImpactfulQuestionsView::getOptionImpact,
                        (existing, replacement) -> existing));
                List<AnswerOptionImpact> optionImpacts = optionMap.values().stream()
                    .map(entity -> AnswerOptionImpactMapper.mapToDomainModel(entity, optionIdToOptionValueMap.get(entity.getOptionId())))
                    .toList();
                impact.setOptionImpacts(optionImpacts);
                question.setImpacts(List.of(impact));
                question.setOptions(options);

                return new Result(question, questionnaire);
            }).toList();
    }

    @Override
    public void delete(long questionId, long kitVersionId) {
        if (repository.existsByIdAndKitVersionId(questionId, kitVersionId))
            repository.deleteByIdAndKitVersionId(questionId, kitVersionId);
        else
            throw new ResourceNotFoundException(DELETE_QUESTION_ID_NOT_FOUND);
    }

    @Override
    public void updateOrders(UpdateOrderParam param) {
        List<Long> ids = param.orders().stream()
            .map(UpdateOrderParam.QuestionOrder::questionId)
            .toList();

        Map<QuestionJpaEntity.EntityId, UpdateOrderParam.QuestionOrder> idToOrder = param.orders().stream()
            .collect(toMap(e ->
                new QuestionJpaEntity.EntityId(e.questionId(), param.kitVersionId()), Function.identity()));

        List<QuestionJpaEntity> entities = repository.findAllByIdInAndKitVersionIdAndQuestionnaireId(ids,
            param.kitVersionId(),
            param.questionnaireId());
        if (entities.size() != ids.size())
            throw new ResourceNotFoundException(QUESTION_ID_NOT_FOUND);

        entities.forEach(e -> {
            UpdateOrderParam.QuestionOrder newOrder =
                idToOrder.get(new QuestionJpaEntity.EntityId(e.getId(), param.kitVersionId()));
            e.setIndex(newOrder.index());
            e.setCode(newOrder.code());
            e.setLastModificationTime(param.lastModificationTime());
            e.setLastModifiedBy(param.lastModifiedBy());
        });
        repository.saveAll(entities);
    }

    @Override
    public void updateAnswerRange(UpdateAnswerRangeParam param) {
        if (!repository.existsByIdAndKitVersionId(param.id(), param.kitVersionId())) {
            throw new ResourceNotFoundException(QUESTION_ID_NOT_FOUND);
        }
        repository.update(param.id(),
            param.kitVersionId(),
            param.answerRangeId(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public PaginatedResponse<Question> loadQuestionnaireQuestions(LoadQuestionnaireQuestionsPort.Param param) {
        var pageResult = repository.findAllByQuestionnaireIdAndKitVersionIdOrderByIndex(param.questionnaireId(),
            param.kitVersionId(),
            PageRequest.of(param.page(), param.size()));
        List<Question> items = pageResult.getContent().stream()
            .map(QuestionMapper::mapToDomainModel)
            .toList();

        return new PaginatedResponse<>(items,
            pageResult.getNumber(),
            pageResult.getSize(),
            QuestionJpaEntity.Fields.INDEX,
            Sort.Direction.ASC.name().toLowerCase(),
            (int) pageResult.getTotalElements());
    }
}
