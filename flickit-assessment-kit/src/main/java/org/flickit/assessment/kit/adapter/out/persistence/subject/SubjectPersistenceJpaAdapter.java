package org.flickit.assessment.kit.adapter.out.persistence.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaRepository;
import org.flickit.assessment.kit.application.port.out.subject.CreateSubjectPort;
import org.flickit.assessment.kit.application.port.out.subject.UpdateSubjectPort;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class SubjectPersistenceJpaAdapter implements
    UpdateSubjectPort,
    CreateSubjectPort {

    private final SubjectJpaRepository repository;

    @Override
    public void update(UpdateSubjectPort.Param param) {
        repository.update(param.id(),
            param.title(),
            param.index(),
            param.description(),
            param.lastModificationTime(),
            param.lastModifiedBy()
        );
    }

    @Override
    public Long persist(CreateSubjectPort.Param param) {
        Long lastReferenceNumber = repository.findLastReferenceNumberByKitId(param.kitId());
        return repository.save(SubjectMapper.mapToJpaEntity(param, lastReferenceNumber)).getId();
    }

}
