package org.flickit.assessment.kit.adapter.out.persistence.subjectquestionnaire;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.subjectquestionnaire.SubjectQuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.subjectquestionnaire.SubjectQuestionnaireJpaRepository;
import org.flickit.assessment.kit.application.domain.SubjectQuestionnaire;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.CreateSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.DeleteSubjectQuestionnairePort;
import org.flickit.assessment.kit.application.port.out.subjectquestionnaire.LoadSubjectQuestionnairePort;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class SubjectQuestionnairePersistenceJpaAdapter implements
    LoadSubjectQuestionnairePort,
    DeleteSubjectQuestionnairePort,
    CreateSubjectQuestionnairePort {

    private final SubjectQuestionnaireJpaRepository repository;

    @Override
    public List<SubjectQuestionnaire> loadByKitId(long assessmentKitId) {
        List<SubjectQuestionnaireJpaEntity> entities = repository.findAllByAssessmentKitId(assessmentKitId);
        return entities.stream().map(SubjectQuestionnaireMapper::mapToDomainModel).toList();
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public long persist(long subjectId, long questionnaireId) {
        SubjectQuestionnaireJpaEntity entity = repository.save(new SubjectQuestionnaireJpaEntity(
            null,
            subjectId,
            questionnaireId));
        return entity.getId();
    }
}
