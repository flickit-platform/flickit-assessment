package org.flickit.assessment.core.adapter.out.persistence.advice;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.domain.assessmentdashboard.DashboardAdvices;
import org.flickit.assessment.core.application.port.out.advice.CountAdvicesDashboardPort;
import org.flickit.assessment.data.jpa.advice.adviceitem.AdviceItemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdvicePersistenceJpaAdapter implements CountAdvicesDashboardPort {

    private final AdviceItemJpaRepository adviceItemJpaRepository;

    @Override
    public DashboardAdvices loadAdviceDashboard(UUID assessmentResultId) {
        int count = adviceItemJpaRepository.countByAssessmentResultId(assessmentResultId);
        return new DashboardAdvices(count);
    }
}
