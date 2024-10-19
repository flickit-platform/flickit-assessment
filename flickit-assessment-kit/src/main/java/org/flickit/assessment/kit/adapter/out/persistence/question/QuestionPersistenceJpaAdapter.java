package org.flickit.assessment.kit.adapter.out.persistence.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaRepository;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaEntity;
import org.flickit.assessment.data.jpa.kit.asnweroptionimpact.AnswerOptionImpactJpaRepository;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaRepository;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.AttributeLevelImpactfulQuestionsView;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.answeroption.AnswerOptionMapper;
import org.flickit.assessment.kit.adapter.out.persistence.answeroptionimpact.AnswerOptionImpactMapper;
import org.flickit.assessment.kit.adapter.out.persistence.questionimpact.QuestionImpactMapper;
import org.flickit.assessment.kit.application.domain.*;
import org.flickit.assessment.kit.application.port.out.question.*;
import org.flickit.assessment.kit.application.port.out.subject.CountSubjectQuestionsPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    DeleteQuestionPort {

    private final QuestionJpaRepository repository;
    private final QuestionImpactJpaRepository questionImpactRepository;
    private final AnswerOptionImpactJpaRepository answerOptionImpactRepository;
    private final AnswerOptionJpaRepository answerOptionRepository;
    private final MaturityLevelJpaRepository maturityLevelRepository;
    private final AttributeJpaRepository attributeRepository;

    @Override
    public void update(UpdateQuestionPort.Param param) {
        repository.update(param.id(),
            param.kitVersionId(),
            param.title(),
            param.index(),
            param.hint(),
            param.mayNotBeApplicable(),
            param.advisable(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public Long persist(CreateQuestionPort.Param param) {
        return repository.save(mapToJpaEntity(param)).getId();
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

        var impacts = questionImpactRepository.findAllByQuestionIdAndKitVersionId(id, kitVersionId).stream()
            .map(QuestionImpactMapper::mapToDomainModel)
            .map(this::setOptionImpacts)
            .toList();

        var options = answerOptionRepository.findByQuestionIdAndKitVersionId(id, kitVersionId).stream()
            .map(AnswerOptionMapper::mapToDomainModel)
            .toList();

        question.setImpacts(impacts);
        question.setOptions(options);
        return question;
    }

    private QuestionImpact setOptionImpacts(QuestionImpact impact) {
        var optionImpacts = answerOptionImpactRepository.findAllByQuestionImpactIdAndKitVersionId(impact.getId(), impact.getKitVersionId()).stream()
            .map(AnswerOptionImpactMapper::mapToDomainModel)
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

                QuestionImpact impact = QuestionImpactMapper.mapToDomainModel(entry.getValue().getFirst().getQuestionImpact());
                Map<Long, AnswerOptionImpactJpaEntity> optionMap = entry.getValue().stream()
                    .collect(Collectors.toMap(e -> e.getOptionImpact().getId(), AttributeLevelImpactfulQuestionsView::getOptionImpact,
                        (existing, replacement) -> existing));
                List<AnswerOptionImpact> optionImpacts = optionMap.values()
                    .stream().map(AnswerOptionImpactMapper::mapToDomainModel).toList();
                impact.setOptionImpacts(optionImpacts);
                question.setImpacts(List.of(impact));

                List<AnswerOption> options = entry.getValue().stream()
                    .collect(Collectors.toMap(e -> e.getAnswerOption().getId(), AttributeLevelImpactfulQuestionsView::getAnswerOption,
                        (existing, replacement) -> existing))
                    .values()
                    .stream().map(AnswerOptionMapper::mapToDomainModel).toList();
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
}
