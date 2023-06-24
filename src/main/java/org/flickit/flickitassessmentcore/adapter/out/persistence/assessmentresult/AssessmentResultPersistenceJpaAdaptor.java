package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class AssessmentResultPersistenceJpaAdaptor implements
    InvalidateAssessmentResultPort {

    private final AssessmentResultJpaRepository repository;

    @Override
    public void invalidateById(UUID id) {
        repository.invalidateById(id);
    }
}
