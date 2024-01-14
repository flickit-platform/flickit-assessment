package org.flickit.assessment.kit.adapter.out.persistence.assessmentkitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.AssessmentKitDslJpaRepository;
import org.flickit.assessment.kit.application.domain.AssessmentKitDsl;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.LoadJsonKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.UpdateAssessmentKitDslPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AssessmentKitDslPersistenceJpaAdapter implements
    CreateKitDslPort,
    LoadJsonKitDslPort,
    UpdateAssessmentKitDslPort{

    private final AssessmentKitDslJpaRepository repository;

    @Override
    public Long create(String dslFilePath, String jsonFilePath) {
        return repository.save(AssessmentKitDslMapper.toJpaEntity(dslFilePath, jsonFilePath)).getId();
    }

    @Override
    public Optional<AssessmentKitDsl> load(Long id) {
        return repository.findById(id).map(AssessmentKitDslMapper::toDomainModel);
    }

    @Override
    public void update(UpdateAssessmentKitDslPort.Param param) {
        repository.updateById(param.id(), param.kitId());
    }
}
