package org.flickit.assessment.core.application.port.out.insight.subjectinsight;

import org.flickit.assessment.core.application.domain.SubjectInsight;

public interface CreateSubjectInsightPort {

    void persist(SubjectInsight subjectInsight);
}
