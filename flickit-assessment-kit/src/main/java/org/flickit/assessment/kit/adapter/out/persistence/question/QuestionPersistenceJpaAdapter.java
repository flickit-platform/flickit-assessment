package org.flickit.assessment.kit.adapter.out.persistence.question;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaRepository;
import org.flickit.assessment.kit.application.port.out.question.CreateQuestionPort;
import org.flickit.assessment.kit.application.port.out.question.UpdateQuestionPort;
import org.springframework.stereotype.Component;

import static org.flickit.assessment.kit.adapter.out.persistence.question.QuestionMapper.mapToJpaEntity;

@Component
@RequiredArgsConstructor
public class QuestionPersistenceJpaAdapter implements
    UpdateQuestionPort,
    CreateQuestionPort {

    private final QuestionJpaRepository repository;

    @Override
    public void update(UpdateQuestionPort.Param param) {
        repository.update(param.id(),
            param.title(),
            param.index(),
            param.hint(),
            param.mayNotBeApplicable(),
            param.lastModificationTime(),
            param.lastModifiedBy());
    }

    @Override
    public Long persist(CreateQuestionPort.Param param) {
        Long lastReferenceNumber = repository.findLastReferenceNumberByKitId(param.kitId());
        return repository.save(mapToJpaEntity(param, lastReferenceNumber)).getId();
    }

}
