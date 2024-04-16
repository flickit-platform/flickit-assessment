package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaRepository;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.port.out.questionnaire.CreateQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairesPort;
import org.flickit.assessment.kit.application.port.out.questionnaire.UpdateQuestionnairePort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.KIT_ID_NOT_FOUND;

@Component(value = "kitQuestionnairePersistenceJpaAdapter")
@RequiredArgsConstructor
public class QuestionnairePersistenceJpaAdapter implements
    CreateQuestionnairePort,
    UpdateQuestionnairePort,
    LoadQuestionnairesPort {

    private final QuestionnaireJpaRepository repository;
    private final AssessmentKitJpaRepository assessmentKitRepository;

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
    public List<Questionnaire> loadByKitId(Long kitId) {
        var kitVersionId = assessmentKitRepository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(KIT_ID_NOT_FOUND))
            .getKitVersionId();

        return repository.findAllByKitVersionIdOrderByIndex(kitVersionId).stream()
            .map(QuestionnaireMapper::mapToDomainModel)
            .toList();
    }
}
