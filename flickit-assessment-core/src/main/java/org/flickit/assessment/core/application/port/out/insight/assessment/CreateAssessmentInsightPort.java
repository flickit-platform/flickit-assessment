package org.flickit.assessment.core.application.port.out.insight.assessment;

import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;

import java.util.UUID;

public interface CreateAssessmentInsightPort {

    UUID persist(AssessmentInsight assessmentInsight);
}
