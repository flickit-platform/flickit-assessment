package org.flickit.assessment.core.application.port.out.insight.subject;

import org.flickit.assessment.core.application.domain.insight.SubjectInsight;

import java.util.List;
import java.util.UUID;

public interface LoadSubjectInsightsPort {

    List<SubjectInsight> loadSubjectInsights(UUID assessmentResultId);
}
