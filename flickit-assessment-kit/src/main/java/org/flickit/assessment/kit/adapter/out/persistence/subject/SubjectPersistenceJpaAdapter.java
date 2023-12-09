package org.flickit.assessment.kit.adapter.out.persistence.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements
    UpdateSubjectPort {

    private final SubjectJpaRepository repository;

    @Override
    public void update(List<Param> params) {
        Map<Long, Param> idToModel = params.stream().collect(Collectors.toMap(Param::id, Function.identity()));
        List<SubjectJpaEntity> entities = repository.findAllById(idToModel.keySet());

        entities.forEach(x -> {
            Param newSubject = idToModel.get(x.getId());
            x.setTitle(newSubject.title());
            x.setIndex(newSubject.index());
            x.setDescription(newSubject.description());
            x.setLastModificationTime(newSubject.lastModificationTime());
        });

        repository.saveAll(entities);
        repository.flush();
    }
}
