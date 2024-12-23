package org.flickit.assessment.core.adapter.out.persistence.advice;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardAdvices;
import org.flickit.assessment.core.application.port.out.advice.LoadAdvicesDashboardPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdvicePersistenceJpaAdapter implements LoadAdvicesDashboardPort {

    @Override
    public DashboardAdvices loadAdviceDashboard() {
        return null;
    }
}
