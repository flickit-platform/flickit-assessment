package org.flickit.assessment.kit.adapter.out.persistence.assessmentkitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.AssessmentKitDslJpaRepository;
import org.flickit.assessment.kit.application.domain.AssessmentKitDsl;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.LoadJsonKitDslPort;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.UpdateAssessmentKitDslPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AssessmentKitDslPersistenceJpaAdapter implements
    CreateAssessmentKitDslPort,
    LoadJsonKitDslPort,
    UpdateAssessmentKitDslPort {

    private final AssessmentKitDslJpaRepository repository;

    @Override
    public CreateAssessmentKitDslPort.Result create(CreateAssessmentKitDslPort.Param param) {
        Long kitZipDslId = repository.save(AssessmentKitDslMapper.toJpaEntity(param.zipFilePath())).getId();
        Long kitJsonDslId = repository.save(AssessmentKitDslMapper.toJpaEntity(param.jsonFilePath())).getId();
        return new CreateAssessmentKitDslPort.Result(kitZipDslId, kitJsonDslId);
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
