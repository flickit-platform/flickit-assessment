package org.flickit.assessment.kit.adapter.out.persistence.kitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.KitDslJpaRepository;
import org.flickit.assessment.kit.application.domain.AssessmentKitDsl;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.LoadJsonKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.UpdateAssessmentKitDslPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateKitDslPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.flickit.assessment.kit.adapter.out.persistence.kitdsl.KitDslMapper.toJpaEntity;

@Component
@RequiredArgsConstructor
public class KitDslPersistenceJpaAdapter implements
    CreateKitDslPort,
    LoadJsonKitDslPort,
    UpdateAssessmentKitDslPort {

    private final KitDslJpaRepository repository;

    @Override
    public Long create(String dslFilePath, String jsonFilePath) {
        return repository.save(toJpaEntity(dslFilePath, jsonFilePath)).getId();
    }

    @Override
    public Optional<AssessmentKitDsl> load(Long id) {
        return repository.findById(id).map(KitDslMapper::toDomainModel);
    }

    @Override
    public void update(UpdateAssessmentKitDslPort.Param param) {
        repository.updateById(param.id(), param.kitId());
    }
}
