package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements
    CreateQuestionnairePort,
    UpdateQuestionnairePort,
    LoadQuestionnairePort {

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
    public Result loadQuestionnaire(Long questionnaireId, Long kitId) {
        QuestionnaireJpaEntity questionnaireEntity = repository.findQuestionnaireByIdAndKitId(questionnaireId, kitId)
            .orElseThrow(() -> new ResourceNotFoundException(GET_QUESTIONNAIRE_QUESTIONNAIRE_ID_NOT_FOUND));
        List<QuestionJpaEntity> questionEntities = questionRepository.findAllByQuestionnaireIdOrderByIndexAsc(questionnaireId);
        List<SubjectJpaEntity> subjectEntities = subjectRepository.findAllByQuestionnaireId(questionnaireId);

        List<String> relatedSubjects = subjectEntities.stream()
            .map(SubjectJpaEntity::getTitle)
            .toList();

        List<Result.Question> questions = questionEntities.stream()
            .map(e -> new Result.Question(e.getId(), e.getTitle(), e.getIndex(), e.getMayNotBeApplicable()))
            .toList();

        return new Result(questionEntities.size(), relatedSubjects, questionnaireEntity.getDescription(), questions);
    }
}
