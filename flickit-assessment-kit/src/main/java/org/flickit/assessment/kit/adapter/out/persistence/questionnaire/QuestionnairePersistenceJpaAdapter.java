package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.RequiredArgsConstructor;
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
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements
    CreateQuestionnairePort,
    UpdateQuestionnairePort,
    LoadKitQuestionnaireDetailPort,
    CheckQuestionnaireExistByIdPort,
    CheckQuestionnaireExistByIdAndKitIdPort {

    private final QuestionnaireJpaRepository repository;
    private final QuestionJpaRepository questionRepository;
    private final SubjectJpaRepository subjectRepository;

    @Override
    public Long persist(Questionnaire questionnaire, long kitVersionId, UUID createdBy) {
        return repository.save(QuestionnaireMapper.mapToJpaEntityToPersist(questionnaire, kitVersionId, createdBy)).getId();
    }

    @Override
    public void update(UpdateQuestionnairePort.Param param) {
        repository.update(param.id(),
            param.title(),
            param.index(),
            param.description(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public Result loadKitQuestionnaireDetail(Long questionnaireId, Long kitId) {
        QuestionnaireJpaEntity questionnaireEntity = repository.findQuestionnaireByIdAndKitId(questionnaireId, kitId).get();
        List<QuestionJpaEntity> questionEntities = questionRepository.findAllByQuestionnaireIdOrderByIndexAsc(questionnaireId);
        List<SubjectJpaEntity> subjectEntities = subjectRepository.findAllByQuestionnaireId(questionnaireId);

        List<String> relatedSubjects = subjectEntities.stream()
            .map(SubjectJpaEntity::getTitle)
            .toList();

        List<Question> questions = questionEntities.stream()
            .map(QuestionMapper::mapToDomainModel)
            .toList();

        return new Result(questionEntities.size(), relatedSubjects, questionnaireEntity.getDescription(), questions);
    }

    @Override
    public boolean checkQuestionnaireExistById(Long questionnaireId) {
        return repository.existsById(questionnaireId);
    }

    @Override
    public boolean checkQuestionnaireExistByIdAndKitId(Long questionnaireId, Long kitId) {
        return repository.existsByIdAndKitId(questionnaireId, kitId);
    }
}
