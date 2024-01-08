package org.flickit.assessment.kit.adapter.out.persistence.assessmentkitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkitdsl.AssessmentKitDslJpaRepository;
import org.flickit.assessment.kit.application.port.out.assessmentkit.CreateAssessmentKitDslPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssessmentKitDslPersistenceJpaAdapter implements CreateAssessmentKitDslPort {

    private final AssessmentKitDslJpaRepository repository;

    @Override
    public CreateAssessmentKitDslPort.Result create(Param param) {
        Long kitZipDslId = repository.save(AssessmentKitDslMapper.toJpaEntity(param.zipFilePath())).getId();
        Long kitJsonDslId = repository.save(AssessmentKitDslMapper.toJpaEntity(param.jsonFilePath())).getId();
        return new CreateAssessmentKitDslPort.Result(kitZipDslId, kitJsonDslId);
    }
}
