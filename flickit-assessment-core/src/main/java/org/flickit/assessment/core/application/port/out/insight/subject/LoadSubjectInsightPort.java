package org.flickit.assessment.core.application.port.out.insight.subject;

import org.flickit.assessment.core.application.domain.insight.SubjectInsight;

import java.util.Optional;
import java.util.UUID;

public interface LoadSubjectInsightPort {

    Optional<SubjectInsight> load(UUID assessmentResultId, Long subjectId);
}
