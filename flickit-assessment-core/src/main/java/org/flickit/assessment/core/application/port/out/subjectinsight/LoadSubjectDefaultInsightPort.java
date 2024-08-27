package org.flickit.assessment.core.application.port.out.subjectinsight;

import java.util.UUID;

public interface LoadSubjectDefaultInsightPort {

    String loadDefaultInsight(UUID assessmentResultId, long subjectId);
}
