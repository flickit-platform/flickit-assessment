package org.flickit.assessment.core.application.port.out.insight.subjectinsight;

import org.flickit.assessment.core.application.domain.SubjectInsight;

import java.util.List;
import java.util.UUID;

public interface LoadSubjectInsightsPort {

    List<SubjectInsight> loadSubjectInsights(UUID assessmentResultId);
}
