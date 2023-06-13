package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentcolor;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessmentcolor.CheckAssessmentColorExistencePort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssessmentColorPersistenceJpaAdaptor implements CheckAssessmentColorExistencePort {

    private final AssessmentColorJpaRepository repository;

    @Override
    public boolean isColorIdExist(Long id) {
        return repository.existsById(id);
    }
}
