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
import java.util.Map;
import java.util.Set;

import static org.flickit.assessment.kit.adapter.out.persistence.subjectquestionnaire.SubjectQuestionnaireMapper.mapToJpaEntity;


@Component
@RequiredArgsConstructor
public class SubjectQuestionnairePersistenceJpaAdapter implements
    LoadSubjectQuestionnairePort,
    DeleteSubjectQuestionnairePort,
    CreateSubjectQuestionnairePort {

    private final SubjectQuestionnaireJpaRepository repository;

    @Override
    public List<SubjectQuestionnaire> loadByKitVersionId(long kitVersionId) {
        List<SubjectQuestionnaireJpaEntity> entities = repository.findAllByKitVersionId(kitVersionId);
        return entities.stream().map(SubjectQuestionnaireMapper::mapToDomainModel).toList();
    }

    @Override
    public List<SubjectQuestionnaire> extractPairs(long kitVersionId) {
        return repository.findSubjectQuestionnairePairs(kitVersionId)
            .stream().map(SubjectQuestionnaireMapper::mapSubjectQuestionnaireViewToDomainModel)
            .toList();
    }

    @Override
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Override
    public long persist(long subjectId, long questionnaireId, Long kitVersionId) {
        return repository.save(mapToJpaEntity(subjectId, questionnaireId, kitVersionId)).getId();
    }

    @Override
    public void persistAll(Map<Long, Set<Long>> questionnaireIdToSubjectIdsMap, Long kitVersionId) {
        List<SubjectQuestionnaireJpaEntity> entities = questionnaireIdToSubjectIdsMap.keySet().stream()
            .flatMap(questionnaireId -> questionnaireIdToSubjectIdsMap.get(questionnaireId).stream()
                .map(subjectId -> mapToJpaEntity(subjectId, questionnaireId, kitVersionId)))
            .toList();
        repository.saveAll(entities);
    }
}
