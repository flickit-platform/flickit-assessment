package org.flickit.assessment.core.application.port.out.insight.assessmentinsight;

import org.flickit.assessment.core.application.domain.AssessmentInsight;

import java.util.UUID;

public interface CreateAssessmentInsightPort {

    UUID persist(AssessmentInsight assessmentInsight);
}
