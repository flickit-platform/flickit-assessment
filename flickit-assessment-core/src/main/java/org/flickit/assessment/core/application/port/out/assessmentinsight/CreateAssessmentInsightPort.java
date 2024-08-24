package org.flickit.assessment.core.application.port.out.assessmentinsight;

import org.flickit.assessment.core.application.domain.AssessmentInsight;

import java.util.UUID;

public interface CreateAssessmentInsightPort {

    UUID persist(AssessmentInsight assessmentInsight);
}
