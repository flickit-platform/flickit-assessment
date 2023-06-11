package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.flickitassessmentcore.application.port.out.assessment.CheckAssessmentUniqueConstraintPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckAssessmentUniqueConstraintJpaAdaptor implements CheckAssessmentUniqueConstraintPort {

    private final AssessmentJpaRepository repository;

    @Override
    public boolean checkCodeAndSpaceIdUniqueConstraint(String code, Long spaceId) {
        return repository.existsByCodeAndSpaceId(code, spaceId);
    }

    @Override
    public boolean checkTitleAndSpaceIdUniqueConstraint(String title, Long spaceId) {
        return repository.existsByTitleAndSpaceId(title, spaceId);
    }
}
