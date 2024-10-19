package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.adapter.out.persistence.question.QuestionMapper;
import org.flickit.assessment.kit.application.domain.Question;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.out.questionnaire.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;
import static org.flickit.assessment.kit.common.ErrorMessageKey.QUESTIONNAIRE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements
    CreateQuestionnairePort,
    UpdateQuestionnairePort,
    LoadQuestionnairesPort,
    LoadKitQuestionnaireDetailPort,
    DeleteQuestionnairePort {

    private final QuestionnaireJpaRepository repository;
    private final AssessmentKitJpaRepository assessmentKitRepository;
    private final QuestionJpaRepository questionRepository;
    private final SubjectJpaRepository subjectRepository;

    @Override
    public Long persist(Questionnaire questionnaire, long kitVersionId, UUID createdBy) {
        return repository.save(QuestionnaireMapper.mapToJpaEntityToPersist(questionnaire, kitVersionId, createdBy)).getId();
    }

    @Override
    public void update(UpdateQuestionnairePort.Param param) {
        if (!repository.existsByIdAndKitVersionId(param.id(), param.kitVersionId()))
            throw new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND);

        repository.update(param.id(),
            param.kitVersionId(),
            param.title(),
            param.code(),
            param.index(),
            param.description(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public void updateOrders(UpdateQuestionnairePort.UpdateOrderParam param) {
        Map<QuestionnaireJpaEntity.EntityId, Integer> idToIndex = param.orders().stream()
            .collect(Collectors.toMap(
                qs -> new QuestionnaireJpaEntity.EntityId(qs.questionnaireId(), param.kitVersionId()),
                UpdateOrderParam.QuestionnaireOrder::index
            ));
        List<QuestionnaireJpaEntity> entities = repository.findAllById(idToIndex.keySet());
        if (entities.size() != param.orders().size())
            throw new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND);

        entities.forEach(x -> {
            int newIndex = idToIndex.get(new QuestionnaireJpaEntity.EntityId(x.getId(), param.kitVersionId()));
            x.setIndex(newIndex);
            x.setLastModificationTime(param.lastModificationTime());
            x.setLastModifiedBy(param.lastModifiedBy());
        });
        repository.saveAll(entities);
    }

    @Override
    public List<Questionnaire> loadByKitId(Long kitId) {
        var kitVersionId = assessmentKitRepository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND))
            .getKitVersionId();

        return repository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(QuestionnaireMapper::mapToDomainModel)
            .toList();
    }

    @Override
    public Result loadKitQuestionnaireDetail(Long questionnaireId, Long kitVersionId) {
        QuestionnaireJpaEntity questionnaireEntity = repository.findByIdAndKitVersionId(questionnaireId, kitVersionId)
            .orElseThrow(() ->  new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND));

        List<QuestionJpaEntity> questionEntities = questionRepository.findAllByQuestionnaireIdAndKitVersionIdOrderByIndex(questionnaireId, kitVersionId, null)
            .getContent();
        List<SubjectJpaEntity> subjectEntities = subjectRepository.findAllByQuestionnaireIdAndKitVersionId(questionnaireId, kitVersionId);

        List<String> relatedSubjects = subjectEntities.stream()
            .map(SubjectJpaEntity::getTitle)
            .toList();

        List<Question> questions = questionEntities.stream()
            .map(QuestionMapper::mapToDomainModel)
            .toList();

        return new Result(questionEntities.size(), relatedSubjects, questionnaireEntity.getDescription(), questions);
    }

    @Override
    public void delete(long kitVersionId, long questionnaireId) {
        if (!repository.existsByIdAndKitVersionId(questionnaireId, kitVersionId))
            throw new ResourceNotFoundException(QUESTIONNAIRE_ID_NOT_FOUND);

        repository.deleteByIdAndKitVersionId(questionnaireId, kitVersionId);
    }
}
