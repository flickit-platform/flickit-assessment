package org.flickit.assessment.core.application.port.out.subjectinsight;

import org.flickit.assessment.core.application.domain.SubjectInsight;

import java.util.Optional;
import java.util.UUID;

public interface LoadSubjectInsightPort {

    Optional<SubjectInsight> loadByAssessmentResultIdAndSubjectId(UUID assessmentResultId, long subjectId);
}
